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

package org.anvilpowered.datasync.nukkit.listener

import cn.nukkit.Player
import cn.nukkit.command.CommandSender
import cn.nukkit.event.EventHandler
import cn.nukkit.event.EventPriority
import cn.nukkit.event.Listener
import cn.nukkit.event.inventory.InventoryClickEvent
import cn.nukkit.event.inventory.InventoryCloseEvent
import cn.nukkit.event.player.PlayerDeathEvent
import cn.nukkit.event.player.PlayerJoinEvent
import cn.nukkit.event.player.PlayerQuitEvent
import cn.nukkit.inventory.Inventory
import cn.nukkit.inventory.PlayerInventory
import cn.nukkit.item.Item
import cn.nukkit.item.ItemTool
import com.google.inject.Inject
import com.google.inject.Singleton
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.anvil.api.util.TextService
import org.anvilpowered.anvil.api.util.TimeFormatService
import org.anvilpowered.datasync.api.misc.ListenerUtils
import org.anvilpowered.datasync.api.model.snapshot.Snapshot
import org.anvilpowered.datasync.api.registry.DataSyncKeys
import org.anvilpowered.datasync.api.serializer.InventorySerializer
import org.anvilpowered.datasync.api.serializer.user.UserSerializerManager
import org.anvilpowered.datasync.api.snapshot.SnapshotManager
import java.util.UUID

@Singleton
class NukkitPlayerListener @Inject constructor(
    private val registry: Registry
) : Listener {

    @Inject
    private lateinit var inventorySerializer: InventorySerializer<Player, Inventory, Item>

    @Inject
    private lateinit var listenerUtils: ListenerUtils

    @Inject
    private lateinit var snapshotRepository: SnapshotManager<String>

    @Inject
    private lateinit var textService: TextService<String, CommandSender>

    @Inject
    private lateinit var timeFormatService: TimeFormatService

    @Inject
    private lateinit var userSerializerManager: UserSerializerManager<Player, String>

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
            print("Attempting to deserialize")
            userSerializerManager.deserialize(event.player)
                .thenAcceptAsync { msg: String -> textService.sendToConsole(msg) }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    fun onPlayerDisconnect(event: PlayerQuitEvent) {
        if (disconnectSerializationEnabled) {
            userSerializerManager.serializeSafe(event.player, "Disconnect")
                .thenAcceptAsync { msg: String -> textService.sendToConsole(msg) }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    fun onPlayerDeath(event: PlayerDeathEvent) {
        if (deathSerializationEnabled) {
            userSerializerManager.serializeSafe(event.entity.player, "Death")
                .thenAcceptAsync { msg: String -> textService.sendToConsole(msg) }
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
        if (listenerUtils.contains(event.player.uniqueId)
            && event.inventory !is PlayerInventory) {

            if (!event.player.hasPermission(registry.getOrDefault(DataSyncKeys.SNAPSHOT_VIEW_EDIT_PERMISSION))) {
                event.isCancelled = true
                return
            }

            if (event.sourceItem == null) {
                return
            }
            if (event.sourceItem.tier == ItemTool.GOLD_INGOT
                && event.sourceItem.hasCustomName()
                && event.sourceItem.customName.equals("Exit without saving")) {
                event.isCancelled = true
                listenerUtils.setCloseData(event.player.uniqueId, true)
                event.inventory.close(event.player)
            }/* else if (event.currentItem!!.type == Material.GOLD_INGOT) {
                event.isCancelled = true
                listenerUtils.setCloseData(event.whoClicked.uniqueId, true)
                event.whoClicked.closeInventory()
            }*/
        } else {
            listenerUtils.remove(event.player.uniqueId)
        }
    }
}
