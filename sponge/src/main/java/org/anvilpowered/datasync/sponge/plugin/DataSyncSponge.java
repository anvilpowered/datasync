/*
 *   DataSync - AnvilPowered
 *   Copyright (C) 2020 Cableguy20
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

package org.anvilpowered.datasync.sponge.plugin;

import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import org.anvilpowered.anvil.api.data.registry.Registry;
import org.anvilpowered.datasync.api.model.snapshot.Snapshot;
import org.anvilpowered.datasync.api.serializer.user.UserSerializerManager;
import org.anvilpowered.datasync.api.tasks.SerializationTaskService;
import org.anvilpowered.datasync.common.plugin.DataSync;
import org.anvilpowered.datasync.common.plugin.DataSyncPluginInfo;
import org.anvilpowered.datasync.sponge.commands.SyncCommandManager;
import org.anvilpowered.datasync.sponge.keys.CommonSpongeDataKeyService;
import org.anvilpowered.datasync.sponge.listeners.PlayerListener;
import org.anvilpowered.datasync.sponge.module.SpongeModule;
import org.anvilpowered.datasync.sponge.serializer.SpongeSnapshotSerializer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Key;
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

@Plugin(
    id = DataSyncPluginInfo.id,
    name = DataSyncPluginInfo.name,
    version = DataSyncPluginInfo.version,
    dependencies = @Dependency(id = "anvil", version = "1.0.0-SNAPSHOT"),
    description = DataSyncPluginInfo.description,
    url = DataSyncPluginInfo.url,
    authors = "Cableguy20"
)
public class DataSyncSponge extends DataSync<PluginContainer, Key<?>> {

    public DataSyncSponge(Injector injector) {
        super(injector, new SpongeModule(), PlayerListener.class);
    }

    @Listener
    public void onServerInitialization(GameInitializationEvent event) {
        initSingletonServices();
        initListeners();
        initCommands();

        loadRegistry();
        Sponge.getServer().getConsole().sendMessage(Text.of(pluginInfo.getPrefix(), TextColors.YELLOW, "Done"));
    }

    @Listener
    public void reload(GameReloadEvent event) {
        environment.reload();
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

    private void initListeners() {
        Sponge.getEventManager().registerListeners(this, injector.getInstance());
    }

    private void initCommands() {
        if (!alreadyLoadedOnce) {
            injector.getInstance(com.google.inject.Key.get(new TypeLiteral<SyncCommandManager>() {
            })).register(this);
            alreadyLoadedOnce = true;
        }
    }

    private void removeListeners() {
        Sponge.getEventManager().un*registerPluginListeners(this);
    }

    private void stopTasks() {
        Sponge.getScheduler().getScheduledTasks(this).forEach(Task::cancel);
    }
}
