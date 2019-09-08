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
import rocks.milspecsg.msdatasync.misc.SnapshotOptimizationService;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.model.core.Snapshot;
import rocks.milspecsg.msdatasync.service.tasks.ApiSerializationTaskService;
import rocks.milspecsg.msdatasync.service.config.ConfigKeys;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Singleton
public class ApiSpongeSerializationTaskService<M extends Member, S extends Snapshot> extends ApiSerializationTaskService<M, S, User> {

    @Inject
    SnapshotOptimizationService snapshotOptimizationService;

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
        baseInterval = configurationService.getConfigInteger(ConfigKeys.SNAPSHOT_UPLOAD_INTERVAL);

        stopSerializationTask();
        startSerializationTask();
    }

    @Override
    public void startSerializationTask() {

        if (baseInterval > 0) {
            Sponge.getServer().getConsole().sendMessage(Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.YELLOW, "Submitting sync task! Upload interval: ", baseInterval, " minutes"));
            task = Task.builder().async().interval(30, TimeUnit.SECONDS).execute(getSerializationTask()).submit(MSDataSync.plugin);
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
            for (Player player : players) {
                snapshotOptimizationService.optimize(player, Sponge.getServer().getConsole(), "Auto");
            }
        };
    }
}
