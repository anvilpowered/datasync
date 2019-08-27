package msdatasync;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import msdatasync.service.config.implementation.MSConfigurationService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import rocks.milspecsg.msrepository.APIConfigurationModule;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;
import rocks.milspecsg.msrepository.service.config.ApiConfigurationService;

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
        Sponge.getServer().getConsole().sendMessage(Text.of(PluginInfo.PluginPrefix, "Finished"));
    }

    private void initServices() {
        injector = spongeRootInjector.createChildInjector(new MSDataSyncModule());
    }

    private void initSingletonServices() {
        injector.getInstance(ConfigurationService.class);
    }

    private class MSDataSyncModule extends APIConfigurationModule {
        @Override
        protected void configure() {
            super.configure();
            bind(new TypeLiteral<ApiConfigurationService>() {}).to(new TypeLiteral<MSConfigurationService>() {});
        }
    }
}
