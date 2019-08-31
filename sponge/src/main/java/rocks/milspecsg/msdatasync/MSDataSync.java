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
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import rocks.milspecsg.msdatasync.api.tasks.SerializationTaskService;
import rocks.milspecsg.msdatasync.commands.SyncCommandManager;
import rocks.milspecsg.msdatasync.listeners.PlayerListener;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.service.data.*;
import rocks.milspecsg.msdatasync.service.implementation.config.MSConfigurationService;
import rocks.milspecsg.msdatasync.service.implementation.data.*;
import rocks.milspecsg.msdatasync.service.implementation.keys.MSDataKeyService;
import rocks.milspecsg.msdatasync.service.implementation.member.MSMemberRepository;
import rocks.milspecsg.msdatasync.service.implementation.tasks.MSSerializationTaskService;
import rocks.milspecsg.msdatasync.service.keys.ApiDataKeyService;
import rocks.milspecsg.msdatasync.service.member.ApiMemberRepository;
import rocks.milspecsg.msdatasync.service.tasks.ApiSerializationTaskService;
import rocks.milspecsg.msrepository.APIConfigurationModule;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;
import rocks.milspecsg.msrepository.service.config.ApiConfigurationService;

@Plugin(id = PluginInfo.Id, name = PluginInfo.Name, version = PluginInfo.Version, description = PluginInfo.Description, authors = PluginInfo.Authors, url = PluginInfo.Url)
public class MSDataSync {

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
        Sponge.getServer().getConsole().sendMessage(Text.of(PluginInfo.PluginPrefix, TextColors.YELLOW, "Loading..."));
        initServices();
        initSingletonServices();
        initListeners();
        initCommands();

        loadConfig();
        Sponge.getServer().getConsole().sendMessage(Text.of(PluginInfo.PluginPrefix, TextColors.YELLOW, "Done"));
    }

    @Listener
    public void reload(GameReloadEvent event) {
//        Cause cause =
//            Cause.builder()
//                .append(this)
//                .build(EventContext.builder().add(EventContextKeys.PLUGIN, pluginContainer).build());
//
//        // Unregistering everything
//        GameStoppingEvent gameStoppingEvent = SpongeEventFactory.createGameStoppingEvent(cause);
//        stop(gameStoppingEvent);
//
//        // Starting over
//        GameInitializationEvent gameInitializationEvent = SpongeEventFactory.createGameInitializationEvent(cause);
//        onServerInitialization(gameInitializationEvent);

        loadConfig();
        logger.info("Reloaded successfully!");
    }

    @Listener
    public void stop(GameStoppingEvent event) {
        Sponge.getServer().getConsole().sendMessage(Text.of(PluginInfo.PluginPrefix, TextColors.YELLOW, "Stopping..."));

        removeListeners();
        logger.debug("Unregistered listeners");

        stopTasks();
        logger.debug("Stopped tasks");

        Sponge.getServer().getConsole().sendMessage(Text.of(PluginInfo.PluginPrefix, TextColors.YELLOW, "Done"));
    }

    private void loadConfig() {
        injector.getInstance(ConfigurationService.class).load();
    }

    private void initServices() {
        injector = spongeRootInjector.createChildInjector(new MSDataSyncConfigurationModule(), new MSDataSyncModule());
    }

    private void initSingletonServices() {
        injector.getInstance(MSDataKeyService.class).initializeDefaultMappings();
        injector.getInstance(MSPlayerSerializer.class);
        injector.getInstance(SerializationTaskService.class);
    }

    private void initListeners() {
        Sponge.getEventManager().registerListeners(this, injector.getInstance(PlayerListener.class));
    }

    private void initCommands() {
        if (!alreadyLoadedOnce) {
            injector.getInstance(com.google.inject.Key.get(new TypeLiteral<SyncCommandManager>() {})).register(this);
            alreadyLoadedOnce = true;
        }
    }

    private void removeListeners() {
        Sponge.getEventManager().unregisterPluginListeners(this);
    }

    private void stopTasks() {
        Sponge.getScheduler().getScheduledTasks(this).forEach(Task::cancel);
    }

    private class MSDataSyncConfigurationModule extends APIConfigurationModule {
        @Override
        protected void configure() {
            super.configure();

            bind(new TypeLiteral<ApiConfigurationService>() {
            }).to(new TypeLiteral<MSConfigurationService>() {
            });
        }
    }

    private class MSDataSyncModule extends ApiModule<Member, Player, Key, User> {
        @Override
        protected void configure() {
            super.configure();

            bind(new TypeLiteral<ApiExperienceSerializer<Member, Player, Key, User>>() {
            })
                .to(new TypeLiteral<MSExperienceSerializer>() {
                });

            bind(new TypeLiteral<ApiGameModeSerializer<Member, Player, Key, User>>() {
            })
                .to(new TypeLiteral<MSGameModeSerializer>() {
                });

            bind(new TypeLiteral<ApiHealthSerializer<Member, Player, Key, User>>() {
            })
                .to(new TypeLiteral<MSHealthSerializer>() {
                });

            bind(new TypeLiteral<ApiHungerSerializer<Member, Player, Key, User>>() {
            })
                .to(new TypeLiteral<MSHungerSerializer>() {
                });

            bind(new TypeLiteral<ApiPlayerSerializer<Member, Player, Key, User>>() {
            })
                .to(new TypeLiteral<MSPlayerSerializer>() {
                });

            bind(new TypeLiteral<ApiInventorySerializer<Member, Player, Key, User>>() {
            })
                .to(new TypeLiteral<MSInventorySerializer>() {
                });

            bind(new TypeLiteral<ApiDataKeyService<Key>>() {
            })
                .to(new TypeLiteral<MSDataKeyService>() {
                });

            bind(new TypeLiteral<ApiMemberRepository<Member, Player, Key, User>>() {
            })
                .to(new TypeLiteral<MSMemberRepository>() {
                });

            bind(ApiSerializationTaskService.class).to(MSSerializationTaskService.class);

            bind(PlayerListener.class);
        }
    }
}
