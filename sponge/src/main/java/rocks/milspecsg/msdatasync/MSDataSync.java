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

package rocks.milspecsg.msdatasync;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
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
import rocks.milspecsg.msdatasync.api.serializer.user.UserSerializerManager;
import rocks.milspecsg.msdatasync.api.tasks.SerializationTaskService;
import rocks.milspecsg.msdatasync.commands.SyncCommandManager;
import rocks.milspecsg.msdatasync.listeners.PlayerListener;
import rocks.milspecsg.msdatasync.model.core.snapshot.Snapshot;
import rocks.milspecsg.msdatasync.service.sponge.config.MSConfigurationService;
import rocks.milspecsg.msdatasync.service.sponge.keys.CommonSpongeDataKeyService;
import rocks.milspecsg.msdatasync.service.sponge.serializer.SpongeSnapshotSerializer;
import rocks.milspecsg.msrepository.CommonConfigurationModule;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;
import rocks.milspecsg.msrepository.service.common.config.CommonConfigurationService;

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
        UserSerializerManager<Snapshot<?>, User> userSerializer = injector.getInstance(com.google.inject.Key.get(new TypeLiteral<UserSerializerManager<Snapshot<?>, User>>() {
        }));

        Sponge.getServer().getOnlinePlayers().forEach(player -> userSerializer.getPrimaryComponent().serialize(player, "Server Stop"));

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
        injector.getInstance(CommonSpongeDataKeyService.class).initializeDefaultMappings();
        injector.getInstance(SpongeSnapshotSerializer.class);
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

    private static class MSDataSyncConfigurationModule extends CommonConfigurationModule {
        @Override
        protected void configure() {
            super.configure();

            bind(new TypeLiteral<CommonConfigurationService>() {
            }).to(new TypeLiteral<MSConfigurationService>() {
            });
        }
    }

    private static class MSDataSyncModule extends SpongeModule {
        @Override
        protected void configure() {
            super.configure();

            bind(PlayerListener.class);

        }
    }
}
