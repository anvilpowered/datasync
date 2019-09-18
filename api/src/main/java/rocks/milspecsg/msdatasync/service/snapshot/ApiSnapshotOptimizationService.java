package rocks.milspecsg.msdatasync.service.snapshot;

import com.google.common.reflect.TypeToken;
import org.bson.types.ObjectId;
import rocks.milspecsg.msdatasync.api.data.UserSerializer;
import rocks.milspecsg.msdatasync.api.member.MemberRepository;
import rocks.milspecsg.msdatasync.api.misc.DateFormatService;
import rocks.milspecsg.msdatasync.api.misc.SyncUtils;
import rocks.milspecsg.msdatasync.api.snapshot.SnapshotOptimizationService;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.model.core.Snapshot;
import rocks.milspecsg.msdatasync.service.config.ConfigKeys;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public abstract class ApiSnapshotOptimizationService<M extends Member, S extends Snapshot, P extends U, U, CS> implements SnapshotOptimizationService<U, CS> {

    @Inject
    protected MemberRepository<M, S, U> memberRepository;

    @Inject
    protected UserSerializer<S, U> userSerializer;

    @Inject
    protected SyncUtils syncUtils;

    @Inject
    protected DateFormatService dateFormatService;

    protected List<int[]> optimizationStrategy = null;

    // players that should not be edited (e.g. if they are currently being serialized / deserialized)
    protected volatile ConcurrentLinkedQueue<UUID> lockedPlayers;

    protected volatile boolean optimizationTaskRunning = false;
    protected volatile boolean requestCancelOptimizationTask = false;

    protected volatile int total = 0;
    protected volatile int membersCompleted = 0;
    protected volatile int snapshotsDeleted = 0;
    protected volatile int snapshotsUploaded = 0;

    //TODO: add time taken and estimated time left

    private ConfigurationService configurationService;

    @Inject
    public ApiSnapshotOptimizationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
        configurationService.addConfigLoadedListener(this::configLoaded);
        lockedPlayers = new ConcurrentLinkedQueue<>();
    }

    private void configLoaded(final Object plugin) {
        Optional<List<int[]>> optional = syncUtils.decodeOptimizationStrategy(configurationService.getConfigList(ConfigKeys.SNAPSHOT_OPTIMIZATION_STRATEGY, new TypeToken<List<String>>() {
        }));
        optimizationStrategy = optional.orElse(null);
    }

    protected final synchronized void incrementCompleted() {
        membersCompleted++;
    }

    protected final synchronized void incrementDeleted() {
        snapshotsDeleted++;
    }

    protected final synchronized void incrementUploaded() {
        snapshotsUploaded++;
    }

    protected final synchronized void setTotal(final int total) {
        this.total = total;
    }

    protected final synchronized void resetCounters() {
        total = 0;
        membersCompleted = 0;
        snapshotsDeleted = 0;
        snapshotsUploaded = 0;
    }

    public final void addLockedPlayer(final UUID uuid) {
        lockedPlayers.add(uuid);
    }

    public final void removeLockedPlayer(final UUID uuid) {
        lockedPlayers.remove(uuid);
    }

    public final int getTotalMembers() {
        return total;
    }

    public final int getMembersCompleted() {
        return membersCompleted;
    }

    public final int getSnapshotsDeleted() {
        return snapshotsDeleted;
    }

    public final int getSnapshotsUploaded() {
        return snapshotsUploaded;
    }

    public final boolean isOptimizationTaskRunning() {
        return optimizationTaskRunning;
    }

    public final boolean stopOptimizationTask() {
        if (!isOptimizationTaskRunning()) {
            return false;
        }
        resetCounters();
        optimizationTaskRunning = false;
        requestCancelOptimizationTask = false;
        return true;
    }

    protected abstract Optional<P> getPlayer(final U user);

    protected abstract String getName(final U user);

    protected abstract void sendError(final CS source, final String message);

    protected abstract void submitTask(final Runnable runnable, final Object plugin);

    /**
     * @return true if something was deleted
     */
    protected final CompletableFuture<Boolean> optimizeFull(final List<ObjectId> snapshotIds, final UUID userUUID, final CS source, final String name, final Object plugin) {
        int baseInterval = configurationService.getConfigInteger(ConfigKeys.SNAPSHOT_UPLOAD_INTERVAL);
        Optional<U> optionalUser = memberRepository.getUser(userUUID);
        if (!optionalUser.isPresent()) return CompletableFuture.completedFuture(null);
        Optional<P> optionalPlayer = getPlayer(optionalUser.get());
        boolean uploadSnapshot = optionalPlayer.isPresent() && snapshotIds.stream().noneMatch(objectId -> within(objectId, baseInterval));
        CompletableFuture<Void> uploadFuture = new CompletableFuture<>();
        if (uploadSnapshot) {
            submitTask(() -> {
                userSerializer.serialize(optionalPlayer.get(), name).thenAcceptAsync(optionalSnapshot -> {
                    if (!optionalSnapshot.isPresent()) {
                        sendError(source,"There was an error serializing user " + getName(optionalPlayer.get()));
                    } else {
                        incrementUploaded();
                        snapshotIds.add(optionalSnapshot.get().getId());
                    }
                    uploadFuture.complete(null);
                });
            }, plugin);
        } else {
            uploadFuture.complete(null);
        }
        return uploadFuture.thenApplyAsync(v -> {
            if (optimizationStrategy == null) {
                sendError(source,"Invalid optimization strategy, optimization is disabled. Please check your config!");
                return false;
            }
            int minCount = configurationService.getConfigInteger(ConfigKeys.SNAPSHOT_MIN_COUNT);
            int snapshotCount = snapshotIds.size();
            if (snapshotCount <= minCount) return false;
            ObjectId latestSnapshotId = snapshotCount > 0 ? snapshotIds.get(snapshotCount - 1) : null; // this one should not be deleted
            List<ObjectId> toDelete = new ArrayList<>();
            optimizationStrategy.forEach(i -> filter(snapshotIds, toDelete, i[0], i[1])); // this line adds snapshot ids to the delete list based on config settings
            int[] last = optimizationStrategy.get(optimizationStrategy.size() - 1);
            snapshotIds.stream().filter(snapshotId -> within(snapshotId, last[0] * last[1])).collect(Collectors.toList()).forEach(snapshotIds::remove);
            toDelete.addAll(snapshotIds);
            boolean[] deletedAnything = new boolean[]{false};
            for (ObjectId objectId : toDelete) {
                // always leave minCount number of snapshots
                if (snapshotCount-- <= minCount) break;
                // do not delete latest snapshot
                if (objectId.equals(latestSnapshotId)) break;

                memberRepository.deleteSnapshot(userUUID, objectId).thenAcceptAsync(success -> {
                    if (success) {
                        deletedAnything[0] = true;
                        incrementDeleted();
//                        source.sendMessage(
//                            Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.YELLOW, "Successfully removed snapshot ", dateFormatService.format(objectId.getDate()), " from " + optionalUser.get().getName())
//                        );
                    } else {
                        sendError(source,"There was an error removing snapshot " + dateFormatService.format(objectId.getDate()) + " from " + getName(optionalUser.get()));
                    }
                }).join();
            }
            return deletedAnything[0];
        });
    }

    protected final boolean within(final ObjectId id, final int minutes) {
        return System.currentTimeMillis() <= ((id.getTimestamp() & 0xFFFFFFFFL) * 1000L) + (minutes * 60000L);
    }

    protected final void filter(final List<ObjectId> snapshotIds, final List<ObjectId> toDelete, final int intervalMinutes, final int maxCount) {
        snapshotIds.stream().filter(snapshotId -> within(snapshotId, intervalMinutes)).collect(Collectors.toList()).forEach(snapshotIds::remove);
        // allow one snapshot per hour for 24 hours
        for (int i = 2; i <= maxCount; i++) {
            ObjectId allowed = null;
            for (ObjectId snapshotId : snapshotIds) {
                if (within(snapshotId, intervalMinutes * i)) {
                    if (allowed == null) {
                        allowed = snapshotId;
                    } else {
                        toDelete.add(snapshotId);
                    }
                }
            }
            if (allowed != null) {
                snapshotIds.remove(allowed);
                snapshotIds.removeAll(toDelete);
            }
        }
    }
}
