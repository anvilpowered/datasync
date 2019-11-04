/*
 *     MSDataSync - MilSpecSG
 *     Copyright (C) 2019 Cableguy20
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package rocks.milspecsg.msdatasync.service.common.snapshotoptimization.component;

import com.google.common.reflect.TypeToken;
import rocks.milspecsg.msdatasync.api.serializer.user.UserSerializerManager;
import rocks.milspecsg.msdatasync.api.member.repository.MemberRepository;
import rocks.milspecsg.msdatasync.api.misc.DateFormatService;
import rocks.milspecsg.msdatasync.api.misc.SyncUtils;
import rocks.milspecsg.msdatasync.api.serializer.user.component.UserSerializerComponent;
import rocks.milspecsg.msdatasync.api.snapshotoptimization.component.SnapshotOptimizationService;
import rocks.milspecsg.msdatasync.api.config.ConfigKeys;
import rocks.milspecsg.msdatasync.api.snapshot.repository.SnapshotRepository;
import rocks.milspecsg.msdatasync.model.core.member.Member;
import rocks.milspecsg.msdatasync.model.core.snapshot.Snapshot;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;
import rocks.milspecsg.msrepository.datastore.DataStoreConfig;
import rocks.milspecsg.msrepository.datastore.DataStoreContext;
import rocks.milspecsg.msrepository.service.common.component.CommonComponent;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public abstract class CommonSnapshotOptimizationService<
    TKey,
    TMember extends Member<TKey>,
    TSnapshot extends Snapshot<TKey>,
    TPlayer extends TUser,
    TUser,
    TCommandSource,
    TDataKey,
    TDataStore,
    TDataStoreConfig extends DataStoreConfig>
    extends CommonComponent<TKey, TDataStore, TDataStoreConfig>
    implements SnapshotOptimizationService<TKey, TUser, TCommandSource, TDataStore, TDataStoreConfig> {

    @Inject
    protected MemberRepository<TKey, TMember, TSnapshot, TUser, TDataStore, TDataStoreConfig> memberRepository;

    @Inject
    protected SnapshotRepository<TKey, TSnapshot, TDataKey, TDataStore, TDataStoreConfig> snapshotRepository;

    @Inject
    protected UserSerializerComponent<TKey, TSnapshot, TUser, TDataStore, TDataStoreConfig> userSerializer;

    @Inject
    protected SyncUtils syncUtils;

    @Inject
    protected DateFormatService dateFormatService;

    private List<int[]> optimizationStrategy;

    // players that should not be edited (e.g. if they are currently being serialized / deserialized)
    protected volatile ConcurrentLinkedQueue<UUID> lockedPlayers;

    protected volatile boolean optimizationTaskRunning;
    protected volatile boolean requestCancelOptimizationTask;

    private volatile int totalMembers;
    private volatile int membersCompleted;
    private volatile int snapshotsDeleted;
    private volatile int snapshotsUploaded;

    protected volatile ConcurrentMap<TKey, Long> idTimeStampMap;

    //TODO: add time taken and estimated time left

    private ConfigurationService configurationService;




    @Inject
    public CommonSnapshotOptimizationService(ConfigurationService configurationService, DataStoreContext<TKey, TDataStore, TDataStoreConfig> dataStoreContext) {
        super(dataStoreContext);
        this.configurationService = configurationService;
        configurationService.addConfigLoadedListener(this::configLoaded);
        lockedPlayers = new ConcurrentLinkedQueue<>();
        idTimeStampMap = new ConcurrentHashMap<>();
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

    protected final void setTotalMembers(final int totalMembers) {
        this.totalMembers = totalMembers;
    }

    protected final synchronized void resetCounters() {
        totalMembers = 0;
        membersCompleted = 0;
        snapshotsDeleted = 0;
        snapshotsUploaded = 0;
    }

    @Override
    public final void addLockedPlayer(final UUID uuid) {
        lockedPlayers.add(uuid);
    }

    @Override
    public final void removeLockedPlayer(final UUID uuid) {
        lockedPlayers.remove(uuid);
    }

    @Override
    public final int getTotalMembers() {
        return totalMembers;
    }

    @Override
    public final int getMembersCompleted() {
        return membersCompleted;
    }

    @Override
    public final int getSnapshotsDeleted() {
        return snapshotsDeleted;
    }

    @Override
    public final int getSnapshotsUploaded() {
        return snapshotsUploaded;
    }

    @Override
    public final boolean isOptimizationTaskRunning() {
        return optimizationTaskRunning;
    }

    @Override
    public final boolean stopOptimizationTask() {
        if (!optimizationTaskRunning) {
            return false;
        }
        resetCounters();
        optimizationTaskRunning = false;
        requestCancelOptimizationTask = false;
        return true;
    }

    protected abstract Optional<TPlayer> getPlayer(final TUser user);

    protected abstract String getName(final TUser user);

    protected abstract void sendError(final TCommandSource source, final String message);

    protected abstract void submitTask(final Runnable runnable, final Object plugin);

    /**
     * @return true if something was deleted
     */
    protected final CompletableFuture<Boolean> optimizeFull(final List<TKey> snapshotIds, final UUID userUUID, final TCommandSource source, final String name, final Object plugin) {
        int baseInterval = configurationService.getConfigInteger(ConfigKeys.SNAPSHOT_UPLOAD_INTERVAL);
        Optional<TUser> optionalUser = memberRepository.getUser(userUUID);
        if (!optionalUser.isPresent()) return CompletableFuture.completedFuture(null);
        Optional<TPlayer> optionalPlayer = getPlayer(optionalUser.get());

        CompletableFuture<Void> uploadFuture = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            if (optionalPlayer.isPresent() && snapshotIds.stream().noneMatch(objectId -> within(objectId, baseInterval).join())) {
                submitTask(() -> {
                    userSerializer.serialize(optionalPlayer.get(), name).thenAcceptAsync(optionalSnapshot -> {
                        if (optionalSnapshot.isPresent()) {
                            incrementUploaded();
                            snapshotIds.add(optionalSnapshot.get().getId());
                        } else {
                            sendError(source, "There was an error serializing user " + getName(optionalPlayer.get()));
                        }
                        uploadFuture.complete(null);
                    });
                }, plugin);
            } else {
                uploadFuture.complete(null);
            }
        });
        return uploadFuture.thenApplyAsync(v -> {
            if (optimizationStrategy == null) {
                sendError(source, "Invalid optimization strategy, optimization is disabled. Please check your config!");
                return false;
            }
            int minCount = configurationService.getConfigInteger(ConfigKeys.SNAPSHOT_MIN_COUNT);
            int snapshotCount = snapshotIds.size();
            if (snapshotCount <= minCount) return false;
            TKey latestSnapshotId = snapshotCount > 0 ? snapshotIds.get(snapshotCount - 1) : null; // this one should not be deleted
            List<TKey> toDelete = new ArrayList<>();
            optimizationStrategy.forEach(i -> filter(snapshotIds, toDelete, i[0], i[1]).join()); // this line adds snapshot ids to the delete list based on config settings
            int[] last = optimizationStrategy.get(optimizationStrategy.size() - 1);
            snapshotIds.stream().filter(snapshotId -> within(snapshotId, last[0] * last[1]).join()).collect(Collectors.toList()).forEach(snapshotIds::remove);
            toDelete.addAll(snapshotIds);
            boolean[] deletedAnything = {false};
            for (TKey id : toDelete) {
                // always leave minCount number of snapshots
                if (snapshotCount-- <= minCount) break;
                // do not delete latest snapshot
                if (id.equals(latestSnapshotId)) break;

                memberRepository.deleteSnapshot(userUUID, id).thenAcceptAsync(success -> {
                    if (success) {
                        deletedAnything[0] = true;
                        incrementDeleted();
//                        source.sendMessage(
//                            Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.YELLOW, "Successfully removed snapshot ", dateFormatService.format(objectId.getDate()), " from " + optionalUser.get().getName())
//                        );
                    } else {
                        String[] dateOrId = {id.toString()};
                        snapshotRepository.getCreatedUtcDate(id).thenAcceptAsync(optionalDate -> optionalDate.ifPresent(date -> dateOrId[0] = dateFormatService.format(date)));
                        sendError(source, "There was an error removing snapshot " + dateOrId[0] + " from " + getName(optionalUser.get()));
                    }
                }).join();
            }
            return deletedAnything[0];
        });
    }

    protected final CompletableFuture<Boolean> within(final TKey id, final int minutes) {
        return getTimeStamp(id).thenApplyAsync(timestamp -> within(timestamp, minutes));
    }

    protected final boolean within(final long timeStamp, final int minutes) {
        return System.currentTimeMillis() <= ((timeStamp & 0xFFFFFFFFL) * 1000L) + (minutes * 60000L);
    }

    protected final CompletableFuture<Void> filter(final List<TKey> snapshotIds, final List<TKey> toDelete, final int intervalMinutes, final int maxCount) {
        return CompletableFuture.runAsync(() -> {
            snapshotIds.stream().filter(snapshotId -> within(snapshotId, intervalMinutes).join()).collect(Collectors.toList()).forEach(snapshotIds::remove);
            for (int i = 2; i <= maxCount; i++) {
                TKey allowed = null;
                for (TKey snapshotId : snapshotIds) {
                    if (within(snapshotId, intervalMinutes * i).join())  {
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
        });
    }

    protected CompletableFuture<Long> getTimeStamp(TKey id) {
        if (idTimeStampMap.containsKey(id)) {
            return CompletableFuture.completedFuture(idTimeStampMap.get(id));
        }

        return snapshotRepository.getCreatedUtcTimeStamp(id).thenApplyAsync(timeStamp -> {
            if (!timeStamp.isPresent()) {
                return -1L;
            }
            idTimeStampMap.put(id, timeStamp.get());
            return timeStamp.get();
        });
    }
}
