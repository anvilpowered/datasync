package rocks.milspecsg.msdatasync.misc;

import com.google.common.reflect.TypeToken;
import org.bson.types.ObjectId;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import rocks.milspecsg.msdatasync.MSDataSync;
import rocks.milspecsg.msdatasync.MSDataSyncPluginInfo;
import rocks.milspecsg.msdatasync.api.data.UserSerializer;
import rocks.milspecsg.msdatasync.api.member.MemberRepository;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.model.core.Snapshot;
import rocks.milspecsg.msdatasync.service.config.ConfigKeys;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Singleton
public class SnapshotOptimizationService {

    @Inject
    private MemberRepository<Member, Snapshot, User> memberRepository;

    @Inject
    private UserSerializer<Snapshot, User> userSerializer;

    @Inject
    private DateFormatService dateFormatService;

    @Inject
    private SyncUtils syncUtils;

    private List<int[]> optimizationStrategy = null;

    private ConfigurationService configurationService;

    private volatile CompletableFuture<Void> currentOptimizeAllTask = null;

    private volatile int total = 0;
    private volatile int completed = 0;

    private synchronized void incrementCompleted() {
        completed++;
    }

    private synchronized void setTotal(final int total) {
        this.total = total;
    }

    private synchronized void resetCounters() {
        total = 0;
        completed = 0;
    }

    public int getTotal() {
        return total;
    }

    public int getCompleted() {
        return completed;
    }

    public boolean isOptimizationTaskRunning() {
        return currentOptimizeAllTask != null;
    }

    public boolean stopOptimizationTask() {
        if (!isOptimizationTaskRunning()) {
            return false;
        }
        if (!currentOptimizeAllTask.isDone()){
            currentOptimizeAllTask.complete(null);
        }
        currentOptimizeAllTask = null;
        return true;
    }

    @Inject
    public SnapshotOptimizationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
        configurationService.addConfigLoadedListener(this::configLoaded);
    }

    private void configLoaded(Object plugin) {
        Optional<List<int[]>> optional = syncUtils.decodeOptimizationStrategy(configurationService.getConfigList(ConfigKeys.SNAPSHOT_OPTIMIZATION_STRATEGY, new TypeToken<List<String>>() {
        }));
        optimizationStrategy = optional.orElse(null);
    }

    public CompletableFuture<Boolean> optimize(final Collection<User> users, final CommandSource source, final String name) {
        return CompletableFuture.supplyAsync(() -> {
            boolean deletedAnything = false;
            for (User user : users) {
                if (optimize(user, source, name).join()) {
                    deletedAnything = true;
                }
            }
            return deletedAnything;
        });
    }

    public CompletableFuture<Boolean> optimize(final User user, final CommandSource source, final String name) {
        return memberRepository.getSnapshotIds(user.getUniqueId()).thenApplyAsync(snapshotIds -> {
            if (!optimizeFull(snapshotIds, user.getUniqueId(), source, name).join()) {
                source.sendMessage(
                    Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.YELLOW, "Successfully ran optimization for ", user.getName(), " but no snapshots were deleted!")
                );
                return true;
            } else return false;
        });
    }

    /**
     * @return true if something was deleted
     */
    public CompletableFuture<Boolean> optimizeFull(final List<ObjectId> snapshotIds, final UUID userUUID, final CommandSource source, final String name) {
        int baseInterval = configurationService.getConfigInteger(ConfigKeys.SNAPSHOT_UPLOAD_INTERVAL);
        Optional<User> optionalUser = memberRepository.getUser(userUUID);
        if (!optionalUser.isPresent()) return CompletableFuture.completedFuture(null);
        Optional<Player> optionalPlayer = optionalUser.get().getPlayer();
        boolean uploadSnapshot = optionalPlayer.isPresent() && snapshotIds.stream().noneMatch(objectId -> within(objectId, baseInterval));
        CompletableFuture<Void> uploadFuture = new CompletableFuture<>();
        if (uploadSnapshot) {
            Task.builder().execute(() -> {
                userSerializer.serialize(optionalPlayer.get(), name).thenAcceptAsync(optionalSnapshot -> {
                    if (!optionalSnapshot.isPresent()) {
                        Sponge.getServer().getConsole().sendMessage(
                            Text.of(TextColors.RED, "There was an error serializing user " + optionalPlayer.get().getName())
                        );
                    }
                    uploadFuture.complete(null);
                });
            }).submit(MSDataSync.plugin);
        } else {
            uploadFuture.complete(null);
        }
        return uploadFuture.thenApplyAsync(v -> {
            if (optimizationStrategy == null) {
                source.sendMessage(
                    Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.RED, "Invalid optimization strategy, optimization is disabled. Please check your config!")
                );
                return false;
            }
            int minCount = configurationService.getConfigInteger(ConfigKeys.SNAPSHOT_MIN_COUNT);
            int snapshotCount = snapshotIds.size();
            if (snapshotCount <= minCount) return false;
            List<ObjectId> toDelete = new ArrayList<>();
            optimizationStrategy.forEach(i -> filter(snapshotIds, toDelete, i[0], i[1]));
            int[] last = optimizationStrategy.get(optimizationStrategy.size() - 1);
            snapshotIds.stream().filter(snapshotId -> within(snapshotId, last[0] * last[1])).collect(Collectors.toList()).forEach(snapshotIds::remove);
            toDelete.addAll(snapshotIds);
            boolean[] deletedAnything = new boolean[]{false};
            for (ObjectId objectId : toDelete) {
                if (snapshotCount-- <= minCount) break;
                memberRepository.deleteSnapshot(userUUID, objectId).thenAcceptAsync(success -> {
                    if (success) {
                        deletedAnything[0] = true;
                        source.sendMessage(
                            Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.YELLOW, "Successfully removed snapshot ", dateFormatService.format(objectId.getDate()), " from " + optionalUser.get().getName())
                        );
                    } else {
                        source.sendMessage(
                            Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.RED, "There was an error removing snapshot ", dateFormatService.format(objectId.getDate()), " from " + optionalUser.get().getName())
                        );
                    }
                }).join();
            }
            return deletedAnything[0];
        });
    }

    public boolean startOptimizeAll(final CommandSource source) {
        if (currentOptimizeAllTask != null) {
            return false;
        }
        this.currentOptimizeAllTask = CompletableFuture.supplyAsync(() -> {
            List<ObjectId> memberIds = memberRepository.getAllIds().join();
            setTotal(memberIds.size());
            for (ObjectId memberId : memberIds) {
                Optional<Member> optionalMember = memberRepository.getOne(memberId).join();
                if (!optionalMember.isPresent()) continue;
                Member member = optionalMember.get();
                optimizeFull(member.snapshotIds, member.userUUID, source, "Manual").join();
                incrementCompleted();
            }
            return null;
        }).thenAcceptAsync(v -> {
            source.sendMessage(Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.YELLOW, "Optimization complete!"));
            resetCounters();
            stopOptimizationTask();
        });
        return true;
    }

    private static boolean within(ObjectId id, int minutes) {
        return System.currentTimeMillis() <= ((id.getTimestamp() & 0xFFFFFFFFL) * 1000L) + (minutes * 60000L);
    }

    private static void filter(List<ObjectId> snapshotIds, List<ObjectId> toDelete, int intervalMinutes, int maxCount) {
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
