package rocks.milspecsg.msdatasync;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import rocks.milspecsg.msdatasync.api.keys.DataKeyService;
import rocks.milspecsg.msdatasync.commands.SyncCommandManager;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.service.data.*;
import rocks.milspecsg.msdatasync.service.implementation.config.MSConfigurationService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import rocks.milspecsg.msdatasync.service.implementation.data.*;
import rocks.milspecsg.msdatasync.service.implementation.keys.MSDataKeyService;
import rocks.milspecsg.msdatasync.service.implementation.member.MSMemberRepository;
import rocks.milspecsg.msdatasync.service.keys.ApiDataKeyService;
import rocks.milspecsg.msdatasync.service.member.ApiMemberRepository;
import rocks.milspecsg.msrepository.APIConfigurationModule;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;
import rocks.milspecsg.msrepository.service.config.ApiConfigurationService;

import java.io.File;

@Plugin(id = PluginInfo.Id, name = PluginInfo.Name, version = PluginInfo.Version, description = PluginInfo.Description, authors = PluginInfo.Authors, url = PluginInfo.Url)
public class MSDataSync {

    @Inject
    public Injector spongeRootInjector;

    public static MSDataSync plugin = null;
    private Injector injector = null;

    @Listener
    public void onServerInitialization(GameInitializationEvent event) {
        plugin = this;
        Sponge.getServer().getConsole().sendMessage(Text.of(PluginInfo.PluginPrefix, "Loading..."));
        initServices();
        initSingletonServices();
        initCommands();
        Sponge.getServer().getConsole().sendMessage(Text.of(PluginInfo.PluginPrefix, "Finished"));
    }

    private void initServices() {
        injector = spongeRootInjector.createChildInjector(new MSDataSyncConfigurationModule(), new MSDataSyncModule());
    }

    private void initSingletonServices() {
        injector.getInstance(ConfigurationService.class);
        injector.getInstance(MSDataKeyService.class).initializeDefaultMappings();
        injector.getInstance(MSPlayerSerializer.class).loadConfig();
    }

    private void initCommands() {
        injector.getInstance(com.google.inject.Key.get(new TypeLiteral<SyncCommandManager>() {})).register(this);
    }

    private class MSDataSyncConfigurationModule extends APIConfigurationModule {
        @Override
        protected void configure() {
            super.configure();

            bind(new TypeLiteral<ApiConfigurationService>() {}).to(new TypeLiteral<MSConfigurationService>() {});
        }
    }

    private class MSDataSyncModule extends ApiModule<Member, Player, Key, User> {
        @Override
        protected void configure() {
            super.configure();

            bind(new TypeLiteral<ApiExperienceSerializer<Member, Player, Key, User>>() {})
                .to(new TypeLiteral<MSExperienceSerializer>() {});

            bind(new TypeLiteral<ApiGameModeSerializer<Member, Player, Key, User>>() {})
                .to(new TypeLiteral<MSGameModeSerializer>() {});

            bind(new TypeLiteral<ApiHealthSerializer<Member, Player, Key, User>>() {})
                .to(new TypeLiteral<MSHealthSerializer>() {});

            bind(new TypeLiteral<ApiHungerSerializer<Member, Player, Key, User>>() {})
                .to(new TypeLiteral<MSHungerSerializer>() {});

            bind(new TypeLiteral<ApiPlayerSerializer<Member, Player, Key, User>>() {})
                .to(new TypeLiteral<MSPlayerSerializer>() {});

            bind(new TypeLiteral<ApiInventorySerializer<Member, Player, Key, User>>() {})
                .to(new TypeLiteral<MSInventorySerializer>() {});

            bind(new TypeLiteral<ApiDataKeyService<Key>>() {})
                .to(new TypeLiteral<MSDataKeyService>() {});

            bind(new TypeLiteral<ApiMemberRepository<Member, Player, Key, User>>() {})
                .to(new TypeLiteral<MSMemberRepository>() {});
        }
    }
}
