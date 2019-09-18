package rocks.milspecsg.msdatasync.service.implementation.tasks;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import rocks.milspecsg.msdatasync.MSDataSync;
import rocks.milspecsg.msdatasync.MSDataSyncPluginInfo;
import rocks.milspecsg.msdatasync.api.snapshot.SnapshotOptimizationService;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.model.core.Snapshot;
import rocks.milspecsg.msdatasync.service.config.ConfigKeys;
import rocks.milspecsg.msdatasync.service.tasks.ApiSerializationTaskService;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;

import java.util.concurrent.TimeUnit;

@Singleton
public class ApiSpongeSerializationTaskService<M extends Member, S extends Snapshot> extends ApiSerializationTaskService<M, S, User> {

    @Inject
    SnapshotOptimizationService<User, CommandSource> snapshotOptimizationService;

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
            if (snapshotOptimizationService.isOptimizationTaskRunning()) {
                Sponge.getServer().getConsole().sendMessage(Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.RED, "Optimization task already running! Task will skip"));
            } else {
                snapshotOptimizationService.optimize(Sponge.getServer().getOnlinePlayers(), Sponge.getServer().getConsole(), "Auto", MSDataSync.plugin);
            }
        };
    }
}
