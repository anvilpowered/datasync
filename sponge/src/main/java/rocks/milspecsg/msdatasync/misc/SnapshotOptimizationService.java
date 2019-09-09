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
import java.util.concurrent.FutureTask;
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

    private volatile boolean optimizationTaskRunning = false;
    private volatile boolean requestCancelOptimizationTask = false;

    private volatile int total = 0;
    private volatile int membersCompleted = 0;
    private volatile int snapshotsDeleted = 0;

    private synchronized void incrementCompleted() {
        membersCompleted++;
    }

    private synchronized void incrementDeleted() {
        snapshotsDeleted++;
    }

    private synchronized void setTotal(final int total) {
        this.total = total;
    }

    private synchronized void resetCounters() {
        total = 0;
        membersCompleted = 0;
        snapshotsDeleted = 0;
    }

    public int getTotalMembers() {
        return total;
    }

    public int getMembersCompleted() {
        return membersCompleted;
    }

    public int getSnapshotsDeleted() {
        return snapshotsDeleted;
    }

    public boolean isOptimizationTaskRunning() {
        return optimizationTaskRunning;
    }

    public boolean stopOptimizationTask() {
        if (!isOptimizationTaskRunning()) {
            return false;
        }
        resetCounters();
        optimizationTaskRunning = false;
        requestCancelOptimizationTask = false;
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
                        incrementDeleted();
//                        source.sendMessage(
//                            Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.YELLOW, "Successfully removed snapshot ", dateFormatService.format(objectId.getDate()), " from " + optionalUser.get().getName())
//                        );
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

    public boolean optimize(final Collection<? extends User> users, final CommandSource source, final String name) {
        if (isOptimizationTaskRunning()) {
            return false;
        }
        CompletableFuture.supplyAsync(() -> {
            for (User user : users) {
                optimize(user, source, name).join();
                incrementCompleted();

                if (requestCancelOptimizationTask) {
                    break;
                }
            }
            return null;
        }).thenAcceptAsync(v -> {
            printOptimizationFinished(source, snapshotsDeleted, membersCompleted);
            resetCounters();
            stopOptimizationTask();
        });
        return true;
    }

    public CompletableFuture<Boolean> optimize(final User user, final CommandSource source, final String name) {
        return memberRepository.getSnapshotIds(user.getUniqueId()).thenApplyAsync(snapshotIds -> optimizeFull(snapshotIds, user.getUniqueId(), source, name).join());
    }

    public boolean startOptimizeAll(final CommandSource source) {
        if (optimizationTaskRunning) {
            return false;
        }
        optimizationTaskRunning = true;
        CompletableFuture.supplyAsync(() -> {
            List<ObjectId> memberIds = memberRepository.getAllIds().join();
            setTotal(memberIds.size());
            for (ObjectId memberId : memberIds) {
                Optional<Member> optionalMember = memberRepository.getOne(memberId).join();
                if (!optionalMember.isPresent()) continue;
                Member member = optionalMember.get();
                optimizeFull(member.snapshotIds, member.userUUID, source, "Manual").join();
                incrementCompleted();

                if (requestCancelOptimizationTask) {
                    break;
                }
            }
            return null;
        }).thenAcceptAsync(v -> {
            printOptimizationFinished(source, snapshotsDeleted, membersCompleted);
            resetCounters();
            stopOptimizationTask();
        });
        return true;
    }

    private static void printOptimizationFinished(final CommandSource source, final int snapshotsDeleted, final int membersCompleted) {
        String snapshotString = snapshotsDeleted == 1 ? " snapshot from " : " snapshots from ";
        String memberString = membersCompleted == 1 ? " user!" : " users!";
        source.sendMessage(Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.YELLOW, "Optimization complete! Removed ", snapshotsDeleted, snapshotString, membersCompleted, memberString));
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
