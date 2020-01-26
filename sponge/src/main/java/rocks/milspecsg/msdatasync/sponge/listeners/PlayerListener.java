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
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import rocks.milspecsg.msdatasync.api.model.snapshot.Snapshot;
import rocks.milspecsg.msdatasync.api.serializer.user.UserSerializerManager;
import rocks.milspecsg.msdatasync.common.data.key.MSDataSyncKeys;
import rocks.milspecsg.msdatasync.sponge.commands.SyncLockCommand;
import rocks.milspecsg.msdatasync.sponge.plugin.MSDataSync;
import rocks.milspecsg.msrepository.api.data.registry.Registry;
import rocks.milspecsg.msrepository.api.util.PluginInfo;

import java.util.concurrent.CompletableFuture;

@Singleton
public class PlayerListener {

    private Registry registry;

    @Inject
    private PluginInfo<Text> pluginInfo;

    @Inject
    private UserSerializerManager<Snapshot<?>, User, Text> userSerializer;

    private boolean enabled;

    @Inject
    public PlayerListener(Registry registry) {
        this.registry = registry;
        registry.addRegistryLoadedListener(this::registryLoaded);
    }

    private void registryLoaded(Object plugin) {
        enabled = registry.getOrDefault(MSDataSyncKeys.SERIALIZE_ON_JOIN_LEAVE);
        if (!enabled) {
            Sponge.getServer().getConsole().sendMessage(
                Text.of(pluginInfo.getPrefix(), TextColors.RED,
                    "Attention! You have opted to disable join/leave syncing.\n" +
                        "If you would like to enable this, set `serializeOnJoinLeave=true` in the config and restart your server or run /sync reload")
            );
        }
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join joinEvent) {
        if (enabled) {
            Player player = joinEvent.getTargetEntity();
            CompletableFuture.runAsync(() -> {
                userSerializer.getPrimaryComponent().deserialize(player, MSDataSync.plugin).thenAcceptAsync(optionalSnapshot -> {
                    if (optionalSnapshot.isPresent()) {
                        Sponge.getServer().getConsole().sendMessage(
                            Text.of(pluginInfo.getPrefix(), TextColors.YELLOW, "Successfully deserialized ", player.getName(), " on join!")
                        );
                    } else {
                        Sponge.getServer().getConsole().sendMessage(
                            Text.of(pluginInfo.getPrefix(), TextColors.RED, "An error occurred while deserializing ", player.getName(), " on join!")
                        );
                    }
                }).join();
            });
        }
    }

    @Listener
    public void onPlayerDisconnect(ClientConnectionEvent.Disconnect disconnectEvent) {
        SyncLockCommand.lockPlayer(disconnectEvent.getTargetEntity());
        if (enabled) {
            Player player = disconnectEvent.getTargetEntity();
            userSerializer.getPrimaryComponent().serialize(player, "Disconnect").thenAcceptAsync(optionalSnapshot -> {
                if (optionalSnapshot.isPresent()) {
                    Sponge.getServer().getConsole().sendMessage(
                        Text.of(pluginInfo.getPrefix(), TextColors.YELLOW, "Successfully serialized ", player.getName(), " on disconnect!")
                    );
                } else {
                    Sponge.getServer().getConsole().sendMessage(
                        Text.of(pluginInfo.getPrefix(), TextColors.RED, "An error occurred while serializing ", player.getName(), " on disconnect!")
                    );
                }
            });
        }
    }
}
