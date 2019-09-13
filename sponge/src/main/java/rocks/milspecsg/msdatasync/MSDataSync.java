package rocks.milspecsg.msdatasync;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import rocks.milspecsg.msdatasync.api.data.UserSerializer;
import rocks.milspecsg.msdatasync.api.tasks.SerializationTaskService;
import rocks.milspecsg.msdatasync.commands.SyncCommandManager;
import rocks.milspecsg.msdatasync.listeners.PlayerListener;
import rocks.milspecsg.msdatasync.misc.SnapshotOptimizationService;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.model.core.Snapshot;
import rocks.milspecsg.msdatasync.service.implementation.config.MSConfigurationService;
import rocks.milspecsg.msdatasync.service.implementation.data.*;
import rocks.milspecsg.msdatasync.service.implementation.keys.ApiSpongeDataKeyService;
import rocks.milspecsg.msdatasync.service.implementation.member.ApiSpongeMemberRepository;
import rocks.milspecsg.msdatasync.service.implementation.member.MSMemberRepository;
import rocks.milspecsg.msdatasync.service.implementation.snapshot.ApiSpongeSnapshotRepository;
import rocks.milspecsg.msdatasync.service.implementation.snapshot.MSSnapshotRepository;
import rocks.milspecsg.msdatasync.service.implementation.tasks.ApiSpongeSerializationTaskService;
import rocks.milspecsg.msdatasync.service.keys.ApiDataKeyService;
import rocks.milspecsg.msdatasync.service.tasks.ApiSerializationTaskService;
import rocks.milspecsg.msrepository.APIConfigurationModule;
import rocks.milspecsg.msrepository.SpongePluginInfo;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;
import rocks.milspecsg.msrepository.service.config.ApiConfigurationService;

@Plugin(
    id = MSDataSyncPluginInfo.id,
    name = MSDataSyncPluginInfo.name,
    version = MSDataSyncPluginInfo.version,
    description = MSDataSyncPluginInfo.description,
    authors = MSDataSyncPluginInfo.authors,
    url = MSDataSyncPluginInfo.url
)
public class MSDataSync {

    @Override
    public String toString() {
        return MSDataSyncPluginInfo.id;
    }

    @Inject
    public Injector spongeRootInjector;

    @Inject
    Logger logger;

    @Inject
    private PluginContainer pluginContainer;

    public static MSDataSync plugin = null;
    private Injector injector = null;

    private boolean alreadyLoadedOnce = false;

    @Listener
    public void onServerInitialization(GameInitializationEvent event) {
        plugin = this;
        Sponge.getServer().getConsole().sendMessage(Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.YELLOW, "Loading..."));
        initServices();
        initSingletonServices();
        initListeners();
        initCommands();

        loadConfig();
        Sponge.getServer().getConsole().sendMessage(Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.YELLOW, "Done"));
    }

    @Listener
    public void reload(GameReloadEvent event) {

        loadConfig();
        logger.info("Reloaded successfully!");
    }

    @Listener
    public void stop(GameStoppingEvent event) {
        Sponge.getServer().getConsole().sendMessage(Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.YELLOW, "Stopping..."));
        logger.info("Saving all players on server");
        UserSerializer<Snapshot, User> userSerializer = injector.getInstance(com.google.inject.Key.get(new TypeLiteral<UserSerializer<Snapshot, User>>() {
        }));

        Sponge.getServer().getOnlinePlayers().forEach(player -> userSerializer.serialize(player, "Server Stop"));

        removeListeners();
        logger.info("Unregistered listeners");

        stopTasks();
        logger.info("Stopped tasks");

        Sponge.getServer().getConsole().sendMessage(Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.YELLOW, "Done"));
    }

    private void loadConfig() {
        injector.getInstance(ConfigurationService.class).load(this);
    }

    private void initServices() {
        injector = spongeRootInjector.createChildInjector(new MSDataSyncConfigurationModule(), new MSDataSyncModule());
    }

    private void initSingletonServices() {
        injector.getInstance(ApiSpongeDataKeyService.class).initializeDefaultMappings();
        injector.getInstance(ApiSpongeSnapshotSerializer.class);
        injector.getInstance(SerializationTaskService.class);
    }

    private void initListeners() {
        Sponge.getEventManager().registerListeners(this, injector.getInstance(PlayerListener.class));
    }

    private void initCommands() {
        if (!alreadyLoadedOnce) {
            injector.getInstance(com.google.inject.Key.get(new TypeLiteral<SyncCommandManager>() {
            })).register(this);
            alreadyLoadedOnce = true;
        }
    }

    private void removeListeners() {
        Sponge.getEventManager().unregisterPluginListeners(this);
    }

    private void stopTasks() {
        Sponge.getScheduler().getScheduledTasks(this).forEach(Task::cancel);
    }

    private static class MSDataSyncConfigurationModule extends APIConfigurationModule {
        @Override
        protected void configure() {
            super.configure();

            bind(new TypeLiteral<ApiConfigurationService>() {
            }).to(new TypeLiteral<MSConfigurationService>() {
            });
        }
    }

    private static class MSDataSyncModule extends ApiSpongeModule {
        @Override
        protected void configure() {
            super.configure();

            bind(PlayerListener.class);

            bind(SpongePluginInfo.class).to(MSDataSyncPluginInfo.class);

            bind(SnapshotOptimizationService.class);

            bind(new TypeLiteral<ApiSpongeMemberRepository>() {
            })
                .to(new TypeLiteral<MSMemberRepository>() {
                });

            bind(new TypeLiteral<ApiSpongeSnapshotRepository>() {
            })
                .to(new TypeLiteral<MSSnapshotRepository>() {
                });

            bind(new TypeLiteral<ApiSerializationTaskService<Member, Snapshot, User>>() {
            })
                .to(new TypeLiteral<ApiSpongeSerializationTaskService<Member, Snapshot>>() {
                });

        }
    }
}
