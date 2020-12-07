/*
 * DataSync - AnvilPowered
 *   Copyright (C) 2020
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
 *     along with this program.  If not, see https://www.gnu.org/licenses/.
 */

package org.anvilpowered.datasync.nukkit.command.snapshot

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.command.Command
import cn.nukkit.command.CommandExecutor
import cn.nukkit.command.CommandSender
import cn.nukkit.inventory.Inventory
import cn.nukkit.item.Item
import com.google.inject.Inject
import com.nukkitx.fakeinventories.inventory.DoubleChestFakeInventory
import com.nukkitx.fakeinventories.inventory.FakeInventories
import org.anvilpowered.anvil.api.util.TextService
import org.anvilpowered.anvil.api.util.TimeFormatService
import org.anvilpowered.datasync.api.member.MemberManager
import org.anvilpowered.datasync.api.misc.LockService
import org.anvilpowered.datasync.api.model.snapshot.Snapshot
import org.anvilpowered.datasync.api.registry.DataSyncKeys
import org.anvilpowered.datasync.api.serializer.InventorySerializer
import org.anvilpowered.datasync.nukkit.DataSyncNukkit
import java.util.Optional
import java.util.function.Consumer

class NukkitSnapshotViewCommand : CommandExecutor {

    @Inject
    private lateinit var plugin: DataSyncNukkit

    @Inject
    private lateinit var lockService: LockService

    @Inject
    private lateinit var memberManager: MemberManager<String>

    @Inject
    private lateinit var inventorySerializer: InventorySerializer<Player, Inventory, Item>

    @Inject
    private lateinit var timeFormatService: TimeFormatService

    @Inject
    private lateinit var textService: TextService<String, CommandSender>

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (!sender.hasPermission(DataSyncKeys.SNAPSHOT_VIEW_BASE_PERMISSION.fallbackValue)) {
            textService.builder()
                .appendPrefix()
                .red().append("Insufficient Permissions!")
                .sendTo(sender)
            return false
        }
        if (sender !is Player) {
            textService.builder()
                .appendPrefix()
                .yellow().append("Player only command!")
                .sendToConsole()
            return false
        }
        if (!lockService.assertUnlocked(sender)) {
            return false
        }
        val player: String
        val snapshot0: String?
        if (args.isEmpty()) {
            textService.builder()
                .appendPrefix()
                .red().append("User is required!")
                .sendTo(sender)
            return false
        } else if (args.size == 1) {
            player = args[0]
            snapshot0 = null
        } else {
            player = args[0]
            snapshot0 = args[1]
        }
        val optionalPlayer = Optional.of(Server.getInstance().getPlayer(player))
        if (!optionalPlayer.isPresent) {
            sender.sendMessage("Offline Player! " + optionalPlayer.isPresent)
            return false
        }

        val targetPlayer = optionalPlayer.get()
        val afterFound = Consumer { optionalSnapshot: Optional<out Snapshot<*>> ->
            if (!optionalSnapshot.isPresent) {
                return@Consumer
            }
            val snapshot = optionalSnapshot.get()
            textService.builder()
                .appendPrefix()
                .yellow().append("Editing snapshot ")
                .gold().append(timeFormatService.format(optionalSnapshot.get().createdUtc))
                .sendTo(sender)
            val player: Player = sender as Player
            try {
                val fakeInv : DoubleChestFakeInventory = FakeInventories().createDoubleChestInventory()
                inventorySerializer.deserializeInventory(snapshot, fakeInv)
                Server.getInstance().scheduler.scheduleTask(plugin, Runnable {
                    player.addWindow(fakeInv)
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        memberManager.primaryComponent.getSnapshotForUser(
            targetPlayer.uniqueId,
            snapshot0
        ).thenAccept(afterFound)
        return true
    }
}
