/*
 *   DataSync - AnvilPowered
 *   Copyright (C) 2020 Cableguy20
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.datasync.spigot.listener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.md_5.bungee.api.chat.TextComponent;
import org.anvilpowered.anvil.api.plugin.PluginInfo;
import org.anvilpowered.anvil.api.registry.Registry;
import org.anvilpowered.datasync.api.registry.DataSyncKeys;
import org.anvilpowered.datasync.api.serializer.user.UserSerializerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@Singleton
public class SpigotPlayerListener implements Listener {

    private Registry registry;

    @Inject
    private PluginInfo<TextComponent> pluginInfo;

    @Inject
    private UserSerializerManager<Player, TextComponent> userSerializerManager;

    private boolean joinSerializationEnabled;
    private boolean disconnectSerializationEnabled;
    private boolean deathSerializationEnabled;

    @Inject
    public SpigotPlayerListener(Registry registry) {
        this.registry = registry;
        registry.whenLoaded(this::registryLoaded).register();
    }


    private void registryLoaded() {
        joinSerializationEnabled = registry.getOrDefault(DataSyncKeys.DESERIALIZE_ON_JOIN);
        if (!joinSerializationEnabled) {
            sendWarning("serialize.deserializeOnJoin");
        }
        disconnectSerializationEnabled = registry.getOrDefault(DataSyncKeys.SERIALIZE_ON_DISCONNECT);
        if (!disconnectSerializationEnabled) {
            sendWarning("serialize.serializeOnDisconnect");
        }
        deathSerializationEnabled = registry.getOrDefault(DataSyncKeys.SERIALIZE_ON_DEATH);
        if (!deathSerializationEnabled) {
            sendWarning("serialize.serializeOnDeath");
        }
    }

    private void sendWarning(String name) {
        Bukkit.getConsoleSender().sendMessage(
            "Attention! You have opted to disable " + name + ".\n" +
                "If you would like to enable this, set `" + name + "=true` in the config and restart your server or run /sync " +
                "reload."
        );
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (joinSerializationEnabled) {
            userSerializerManager.deserializeJoin(event.getPlayer())
                .thenAcceptAsync(msg -> Bukkit.getConsoleSender().sendMessage(msg.getText()));
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        if (disconnectSerializationEnabled) {
            userSerializerManager.serializeSafe(event.getPlayer(), "Disconnect")
                .thenAcceptAsync(msg -> Bukkit.getConsoleSender().sendMessage(msg.getText()));
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (deathSerializationEnabled) {
            userSerializerManager.serializeSafe(event.getEntity().getPlayer(), "Death")
                .thenAcceptAsync(msg -> Bukkit.getConsoleSender().sendMessage(msg.getText()));
        }
    }

}
