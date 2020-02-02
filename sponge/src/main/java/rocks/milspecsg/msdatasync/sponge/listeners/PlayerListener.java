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

package rocks.milspecsg.msdatasync.sponge.listeners;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import rocks.milspecsg.msdatasync.api.model.snapshot.Snapshot;
import rocks.milspecsg.msdatasync.api.serializer.user.UserSerializerManager;
import rocks.milspecsg.msdatasync.common.data.key.MSDataSyncKeys;
import rocks.milspecsg.msdatasync.sponge.commands.SyncLockCommand;
import rocks.milspecsg.msdatasync.sponge.plugin.MSDataSync;
import rocks.milspecsg.msrepository.api.data.registry.Registry;
import rocks.milspecsg.msrepository.api.plugin.PluginInfo;

@Singleton
public class PlayerListener {

    private Registry registry;

    @Inject
    private PluginInfo<Text> pluginInfo;

    @Inject
    private UserSerializerManager<Snapshot<?>, User, Text> userSerializerManager;

    private boolean joinSerializationEnabled;
    private boolean disconnectSerializationEnabled;
    private boolean deathSerializationEnabled;

    @Inject
    public PlayerListener(Registry registry) {
        this.registry = registry;
        registry.addRegistryLoadedListener(this::registryLoaded);
    }

    private void registryLoaded() {
        joinSerializationEnabled = registry.getOrDefault(MSDataSyncKeys.DESERIALIZE_ON_JOIN);
        if (!joinSerializationEnabled) {
            sendWarning("serialize.deserializeOnJoin");
        }
        disconnectSerializationEnabled = registry.getOrDefault(MSDataSyncKeys.SERIALIZE_ON_DISCONNECT);
        if (!disconnectSerializationEnabled) {
            sendWarning("serialize.serializeOnDisconnect");
        }
        deathSerializationEnabled = registry.getOrDefault(MSDataSyncKeys.SERIALIZE_ON_DEATH);
        if (!deathSerializationEnabled) {
            sendWarning("serialize.serializeOnDeath");
        }
    }

    private void sendWarning(String name) {
        Sponge.getServer().getConsole().sendMessage(
            Text.of(pluginInfo.getPrefix(), TextColors.RED,
                "Attention! You have opted to disable ", name, ".\n" +
                    "If you would like to enable this, set `", name, "=true` in the config and restart your server or run /sync reload")
        );
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join joinEvent, @Root Player player) {
        if (joinSerializationEnabled) {
            userSerializerManager.deserialize(player, MSDataSync.plugin, "Join")
                .thenAcceptAsync(Sponge.getServer().getConsole()::sendMessage);
        }
    }

    @Listener
    public void onPlayerDisconnect(ClientConnectionEvent.Disconnect disconnectEvent, @Root Player player) {
        SyncLockCommand.lockPlayer(player);
        if (disconnectSerializationEnabled) {
            userSerializerManager.serialize(player, "Disconnect")
                .thenAcceptAsync(Sponge.getServer().getConsole()::sendMessage);
        }
    }

    @Listener
    public void onPlayerDeath(DestructEntityEvent.Death deathEvent, @Getter("getTargetEntity") Player player) {
        if (deathSerializationEnabled) {
            userSerializerManager.serialize(player, "Death")
                .thenAcceptAsync(Sponge.getServer().getConsole()::sendMessage);
        }
    }
}
