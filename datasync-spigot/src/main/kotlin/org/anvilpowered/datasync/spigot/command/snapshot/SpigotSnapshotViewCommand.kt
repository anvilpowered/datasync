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

package org.anvilpowered.datasync.spigot.command.snapshot

import com.google.inject.Inject
import net.md_5.bungee.api.chat.TextComponent
import org.anvilpowered.anvil.api.util.TextService
import org.anvilpowered.anvil.api.util.TimeFormatService
import org.anvilpowered.datasync.api.member.MemberManager
import org.anvilpowered.datasync.api.misc.ListenerUtils
import org.anvilpowered.datasync.api.misc.LockService
import org.anvilpowered.datasync.api.model.snapshot.Snapshot
import org.anvilpowered.datasync.api.registry.DataSyncKeys
import org.anvilpowered.datasync.api.serializer.InventorySerializer
import org.anvilpowered.datasync.spigot.DataSyncSpigot
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.Optional
import java.util.function.Consumer

class SpigotSnapshotViewCommand : CommandExecutor {
    @Inject
    private lateinit var plugin: DataSyncSpigot

    @Inject
    private lateinit var listenerUtils: ListenerUtils

    @Inject
    private lateinit var lockService: LockService

    @Inject
    private lateinit var memberManager: MemberManager<TextComponent>

    @Inject
    private lateinit var inventorySerializer: InventorySerializer<Player, Inventory, ItemStack>

    @Inject
    private lateinit var timeFormatService: TimeFormatService

    @Inject
    private lateinit var textService: TextService<TextComponent, CommandSender>

    override fun onCommand(source: CommandSender, command: Command, s: String, context: Array<String>): Boolean {
        if (!source.hasPermission(DataSyncKeys.SNAPSHOT_VIEW_BASE_PERMISSION.fallbackValue)) {
            textService.builder()
                .appendPrefix()
                .red().append("Insufficient Permissions!")
                .sendTo(source)
            return false
        }
        if (source !is Player) {
            textService.builder()
                .appendPrefix()
                .yellow().append("Player only command!")
                .sendToConsole()
            return false
        }
        if (!lockService.assertUnlocked(source)) {
            return false
        }
        val player: String
        val snapshot0: String?
        when {
            context.isEmpty() -> {
                textService.builder()
                    .appendPrefix()
                    .red().append("User is required!")
                    .sendTo(source)
                return false
            }
            context.size == 1 -> {
                player = context[0]
                snapshot0 = null
            }
            else -> {
                player = context[0]
                snapshot0 = context[1]
            }
        }
        val optionalPlayer = Optional.ofNullable(Bukkit.getPlayer(player))
        if (!optionalPlayer.isPresent) {
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
                .sendTo(source)
            try {
                val inventory = Bukkit.createInventory(
                    null,
                    45,
                    ChatColor.translateAlternateColorCodes(
                        '&',
                        "&3" + timeFormatService.format(snapshot.createdUtc).toString()
                    ))
                listenerUtils.add(source.uniqueId, snapshot, targetPlayer.displayName)
                inventorySerializer.deserializeInventory(snapshot, inventory)
                Bukkit.getScheduler().runTask(plugin, Runnable { source.openInventory(inventory) })
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
