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

package rocks.milspecsg.msdatasync.sponge.plugin;

import com.google.common.reflect.TypeToken;
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
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import rocks.milspecsg.msdatasync.api.model.snapshot.Snapshot;
import rocks.milspecsg.msdatasync.api.serializer.user.UserSerializerManager;
import rocks.milspecsg.msdatasync.api.tasks.SerializationTaskService;
import rocks.milspecsg.msdatasync.common.plugin.MSDataSyncPluginInfo;
import rocks.milspecsg.msdatasync.sponge.commands.SyncCommandManager;
import rocks.milspecsg.msdatasync.sponge.keys.CommonSpongeDataKeyService;
import rocks.milspecsg.msdatasync.sponge.listeners.PlayerListener;
import rocks.milspecsg.msdatasync.sponge.module.SpongeModule;
import rocks.milspecsg.msdatasync.sponge.serializer.SpongeSnapshotSerializer;
import rocks.milspecsg.msrepository.api.MSRepository;
import rocks.milspecsg.msrepository.api.data.registry.Registry;
import rocks.milspecsg.msrepository.api.misc.BindingExtensions;
import rocks.milspecsg.msrepository.api.plugin.PluginInfo;
import rocks.milspecsg.msrepository.sponge.module.ApiSpongeModule;

@Plugin(
    id = MSDataSyncPluginInfo.id,
    name = MSDataSyncPluginInfo.name,
    version = MSDataSyncPluginInfo.version,
    description = MSDataSyncPluginInfo.description,
    authors = {"Cableguy20"},
    url = MSDataSyncPluginInfo.url,
    dependencies = {
        @Dependency(id = "mscore", version = "1.0.0-SNAPSHOT"),
        @Dependency(id = "spotlin", optional = true, version = "0.2.0")
    }
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

    public static MSDataSync plugin;
    private Injector injector;
    private PluginInfo<Text> pluginInfo;

    private boolean alreadyLoadedOnce = false;

    @Listener
    public void onServerInitialization(GameInitializationEvent event) {
        plugin = this;
        injector = spongeRootInjector.createChildInjector(new SpongeModule(), new ApiSpongeModule());
        MSRepository.registerEnvironment(MSDataSyncPluginInfo.id, injector, BindingExtensions.getKey(new TypeToken<PluginInfo<Text>>() {
        }));
        pluginInfo = injector.getInstance(com.google.inject.Key.get(new TypeLiteral<PluginInfo<Text>>() {
        }));
        Sponge.getServer().getConsole().sendMessage(Text.of(pluginInfo.getPrefix(), TextColors.YELLOW, "Loading..."));
        initSingletonServices();
        initListeners();
        initCommands();

        loadRegistry();
        Sponge.getServer().getConsole().sendMessage(Text.of(pluginInfo.getPrefix(), TextColors.YELLOW, "Done"));
    }

    @Listener
    public void reload(GameReloadEvent event) {
        loadRegistry();
        logger.info("Reloaded successfully!");
    }

    @Listener
    public void stop(GameStoppingEvent event) {
        Sponge.getServer().getConsole().sendMessage(Text.of(pluginInfo.getPrefix(), TextColors.YELLOW, "Stopping..."));
        logger.info("Saving all players on server");
        UserSerializerManager<Snapshot<?>, User, Text> userSerializer = injector.getInstance(com.google.inject.Key.get(new TypeLiteral<UserSerializerManager<Snapshot<?>, User, Text>>() {
        }));

        Sponge.getServer().getOnlinePlayers().forEach(player -> userSerializer.getPrimaryComponent().serialize(player, "Server Stop"));

        removeListeners();
        logger.info("Unregistered listeners");

        stopTasks();
        logger.info("Stopped tasks");

        Sponge.getServer().getConsole().sendMessage(Text.of(pluginInfo.getPrefix(), TextColors.YELLOW, "Done"));
    }

    private void loadRegistry() {
        injector.getInstance(Registry.class).load();
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
}
