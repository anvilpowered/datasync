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
package org.anvilpowered.datasync.spigot.listener

import com.google.inject.Inject
import com.google.inject.Singleton
import net.md_5.bungee.api.chat.TextComponent
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.anvil.api.util.TextService
import org.anvilpowered.datasync.api.registry.DataSyncKeys
import org.anvilpowered.datasync.api.serializer.user.UserSerializerManager
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

@Singleton
class SpigotPlayerListener @Inject constructor(
    private val registry: Registry
) : Listener {

    @Inject
    private lateinit var textService: TextService<TextComponent, CommandSender>

    @Inject
    private lateinit var userSerializerManager: UserSerializerManager<Player, TextComponent>

    private var joinSerializationEnabled = false
    private var disconnectSerializationEnabled = false
    private var deathSerializationEnabled = false

    init {
        registry.whenLoaded { registryLoaded() }.register()
    }

    private fun registryLoaded() {
        joinSerializationEnabled = registry.getOrDefault(DataSyncKeys.DESERIALIZE_ON_JOIN)
        if (!joinSerializationEnabled) {
            sendWarning("serialize.deserializeOnJoin")
        }
        disconnectSerializationEnabled = registry.getOrDefault(DataSyncKeys.SERIALIZE_ON_DISCONNECT)
        if (!disconnectSerializationEnabled) {
            sendWarning("serialize.serializeOnDisconnect")
        }
        deathSerializationEnabled = registry.getOrDefault(DataSyncKeys.SERIALIZE_ON_DEATH)
        if (!deathSerializationEnabled) {
            sendWarning("serialize.serializeOnDeath")
        }
    }

    private fun sendWarning(name: String) {
        textService.builder()
            .appendPrefix()
            .red().append("Attention! You have opted to disable", name, ".\n",
                "If you would like to disable this, set`", name,
                "=true` in the config and restart your server or run /sync reload"
            ).sendToConsole()
    }

    @EventHandler(priority = EventPriority.NORMAL)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        if (joinSerializationEnabled) {
            userSerializerManager.deserializeJoin(event.player)
                .thenAcceptAsync { msg: TextComponent -> Bukkit.getConsoleSender().sendMessage(msg.text) }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    fun onPlayerDisconnect(event: PlayerQuitEvent) {
        if (disconnectSerializationEnabled) {
            userSerializerManager.serializeSafe(event.player, "Disconnect")
                .thenAcceptAsync { msg: TextComponent -> Bukkit.getConsoleSender().sendMessage(msg.text) }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    fun onPlayerDeath(event: PlayerDeathEvent) {
        if (deathSerializationEnabled) {
            userSerializerManager.serializeSafe(event.entity.player, "Death")
                .thenAcceptAsync { msg: TextComponent -> Bukkit.getConsoleSender().sendMessage(msg.text) }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onIntentoryInteract(event: InventoryInteractEvent?) {
    }
}
