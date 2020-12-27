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
import org.anvilpowered.anvil.api.util.PermissionService
import org.anvilpowered.anvil.api.util.TextService
import org.anvilpowered.anvil.api.util.TimeFormatService
import org.anvilpowered.datasync.api.misc.ListenerUtils
import org.anvilpowered.datasync.api.model.snapshot.Snapshot
import org.anvilpowered.datasync.api.registry.DataSyncKeys
import org.anvilpowered.datasync.api.serializer.InventorySerializer
import org.anvilpowered.datasync.api.serializer.user.UserSerializerManager
import org.anvilpowered.datasync.api.snapshot.SnapshotManager
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import java.util.UUID

@Singleton
class SpigotPlayerListener @Inject constructor(
    private val registry: Registry
) : Listener {

    @Inject
    private lateinit var inventorySerializer: InventorySerializer<Player, Inventory, ItemStack>

    @Inject
    private lateinit var listenerUtils: ListenerUtils

    @Inject
    private lateinit var snapshotRepository: SnapshotManager<String>

    @Inject
    private lateinit var textService: TextService<TextComponent, CommandSender>

    @Inject
    private lateinit var timeFormatService: TimeFormatService

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
            .red().append("Attention! You have opted to disable ", name, ".\n",
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

    @EventHandler(priority = EventPriority.NORMAL)
    fun onIntentoryClose(event: InventoryCloseEvent) {
        val userUUID: UUID = event.player.uniqueId
        if (listenerUtils.contains(userUUID)) {
            val snapshot: Snapshot<*> = listenerUtils.getSnapshot(userUUID)
            if (listenerUtils.getCloseData(userUUID)
                || !event.player.hasPermission(registry.getOrDefault(DataSyncKeys.SNAPSHOT_VIEW_EDIT_PERMISSION))) {
                textService.builder()
                    .appendPrefix()
                    .yellow().append("Closed snapshot ")
                    .gold().append(timeFormatService.format(snapshot.createdUtc))
                    .yellow().append(" without saving")
                    .sendTo(event.player)
                return
            }
            inventorySerializer.serializeInventory(snapshot, event.inventory)
            snapshotRepository.primaryComponent.parseAndSetInventory(snapshot.id, snapshot.inventory)
                .thenAcceptAsync { b: Boolean ->
                    val targetUser: String = listenerUtils.getTargetUser(userUUID)
                    if (b) {
                        textService.builder()
                            .appendPrefix()
                            .yellow().append("Successfully edited snapshot ")
                            .gold().append(timeFormatService.format(snapshot.createdUtc))
                            .yellow().append(" for ", targetUser)
                            .sendTo(event.player)
                    } else {
                        textService.builder()
                            .appendPrefix()
                            .red().append("An error occurred while serializing user ", targetUser)
                            .sendTo(event.player)
                    }
                }.join()
            listenerUtils.remove(userUUID)
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    fun onInventoryInteract(event: InventoryClickEvent) {
        if (listenerUtils.contains(event.whoClicked.uniqueId) && event.inventory !is PlayerInventory) {
            if (!event.whoClicked.hasPermission(registry.getOrDefault(DataSyncKeys.SNAPSHOT_VIEW_EDIT_PERMISSION))) {
                event.isCancelled = true
                return
            }

            if (event.currentItem == null) {
                return
            }

            if (event.currentItem!!.type == Material.BARRIER
                && event.currentItem!!.itemMeta != null
                && event.currentItem!!.itemMeta!!.displayName == "Not an actual slot") {
                event.isCancelled = true
            } else if (event.currentItem!!.type == Material.GOLD_INGOT) {
                event.isCancelled = true
                listenerUtils.setCloseData(event.whoClicked.uniqueId, true)
                event.whoClicked.closeInventory()
            }
        } else {
            listenerUtils.remove(event.whoClicked.uniqueId)
        }
    }
}
