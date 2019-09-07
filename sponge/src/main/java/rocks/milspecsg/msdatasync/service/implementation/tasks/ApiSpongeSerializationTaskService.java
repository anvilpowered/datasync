package rocks.milspecsg.msdatasync.service.implementation.tasks;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bson.types.ObjectId;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import rocks.milspecsg.msdatasync.MSDataSync;
import rocks.milspecsg.msdatasync.MSDataSyncPluginInfo;
import rocks.milspecsg.msdatasync.misc.DateFormatService;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.model.core.Snapshot;
import rocks.milspecsg.msdatasync.service.tasks.ApiSerializationTaskService;
import rocks.milspecsg.msdatasync.service.config.ConfigKeys;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Singleton
public class ApiSpongeSerializationTaskService<M extends Member, S extends Snapshot> extends ApiSerializationTaskService<M, S, User> {

    @Inject
    DateFormatService dateFormatService;

    private ConfigurationService configurationService;

    private Task task = null;

    private int baseInterval = 5;

    @Inject
    public ApiSpongeSerializationTaskService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
        this.configurationService.addConfigLoadedListener(this::loadConfig);
    }

    private void loadConfig(Object plugin) {
        baseInterval = configurationService.getConfigInteger(ConfigKeys.SERIALIZATION_TASK_INTERVAL_MINUTES);

        stopSerializationTask();
        startSerializationTask();
    }

    @Override
    public void startSerializationTask() {


        if (baseInterval > 0 ) {
            Sponge.getServer().getConsole().sendMessage(Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.YELLOW, "Submitting sync task! Upload interval: ", baseInterval, " minutes"));
            task = Task.builder().interval(30, TimeUnit.SECONDS).execute(getSerializationTask()).submit(MSDataSync.plugin);
        } else {
            Sponge.getServer().getConsole().sendMessage(Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.RED, "Sync task has been disabled from config!"));
        }
    }

    @Override
    public void stopSerializationTask() {
        if (task != null) task.cancel();
    }

    @Override
    public Runnable getSerializationTask() {
        return () -> {
            Collection<Player> players = Sponge.getServer().getOnlinePlayers();
//            ConcurrentLinkedQueue<Player> successful = new ConcurrentLinkedQueue<>();
//            ConcurrentLinkedQueue<Player> unsuccessful = new ConcurrentLinkedQueue<>();

//            Text toSend;
//
//            if (players.isEmpty()) {
//                toSend = Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.YELLOW, "Starting sync task... no players online, skipping!");
//            } else {
//                toSend = Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.YELLOW, "Starting sync task...");
//            }

//            Sponge.getServer().getConsole().sendMessage(toSend);

            for (Player player : players) {

                memberRepository.getSnapshotIds(player.getUniqueId()).thenAcceptAsync(snapshotIds -> {

                    // check if there is a snapshot within base interval minutes
                    boolean uploadSnapshot = snapshotIds.stream().noneMatch(objectId -> within(objectId, baseInterval));

                    CompletableFuture<Void> uploadFuture = new CompletableFuture<>();
                    if (uploadSnapshot) {
                        Sponge.getServer().getConsole().sendMessage(
                            Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.YELLOW, "Uploading player " + player.getName())
                        );
                        Task.builder().execute(() -> {
                            userSerializer.serialize(player, "Auto").thenAcceptAsync(optionalSnapshot -> {
                                if (!optionalSnapshot.isPresent()) {
                                    Sponge.getServer().getConsole().sendMessage(
                                        Text.of(TextColors.RED, "There was an error serializing player " + player.getName())
                                    );
                                }
                                uploadFuture.complete(null);
                            });
                        }).submit(MSDataSync.plugin);
                    } else {
                        uploadFuture.complete(null);
                    }

                    uploadFuture.thenAcceptAsync(v -> {

                        List<ObjectId> toDelete = new ArrayList<>();

                        // now check if snapshots have expired

                        filter(snapshotIds, toDelete, 20, 3);
                        filter(snapshotIds, toDelete, 60, 24);
                        filter(snapshotIds, toDelete, 1440, 7);

                        snapshotIds.stream().filter(snapshotId -> within(snapshotId, 10080)).collect(Collectors.toList()).forEach(snapshotIds::remove);

                        // delete all snapshots older than 1 week
                        toDelete.addAll(snapshotIds);

                        // delete from member
                        toDelete.forEach(objectId -> memberRepository.deleteSnapshot(player.getUniqueId(), objectId).thenAcceptAsync(success -> {
                            if (success) {
                                Sponge.getServer().getConsole().sendMessage(
                                    Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.YELLOW, "Successfully removed snapshot ", dateFormatService.format(objectId.getDate()), " from " + player.getName())
                                );
                            } else {
                                Sponge.getServer().getConsole().sendMessage(
                                    Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.RED, "There was an error removing snapshot ", dateFormatService.format(objectId.getDate()), " from " + player.getName())
                                );
                            }
                        }));
                    });
                });





//                userSerializer.serialize(player, "Auto").thenAcceptAsync(optionalSnapshot -> {
//                    if (optionalSnapshot.isPresent()) {
//                        successful.add(player);
//                    } else {
//                        unsuccessful.add(player);
//                    }
//                    if (successful.size() + unsuccessful.size() >= players.size()) {
//                        if (successful.size() > 0) {
//                            String s = successful.stream().map(User::getName).collect(Collectors.joining(","));
//                            Sponge.getServer().getConsole().sendMessage(
//                                Text.of(TextColors.YELLOW, "The following players were successfully serialized: \n", TextColors.GREEN, s)
//                            );
//                        }
//                        if (unsuccessful.size() > 0) {
//                            String u = unsuccessful.stream().map(User::getName).collect(Collectors.joining(","));
//                            Sponge.getServer().getConsole().sendMessage(
//                                Text.of(TextColors.RED, "The following players were unsuccessfully serialized: \n", u)
//                            );
//                        }
//                    }
//                });
            }
        };
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
