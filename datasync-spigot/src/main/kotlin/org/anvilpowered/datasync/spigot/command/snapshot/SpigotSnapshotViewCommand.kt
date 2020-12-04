package org.anvilpowered.datasync.spigot.command.snapshot

import com.google.inject.Inject
import net.md_5.bungee.api.chat.TextComponent
import org.anvilpowered.anvil.api.util.TextService
import org.anvilpowered.anvil.api.util.TimeFormatService
import org.anvilpowered.datasync.api.member.MemberManager
import org.anvilpowered.datasync.api.misc.LockService
import org.anvilpowered.datasync.api.model.snapshot.Snapshot
import org.anvilpowered.datasync.api.registry.DataSyncKeys
import org.anvilpowered.datasync.api.serializer.InventorySerializer
import org.anvilpowered.datasync.spigot.DataSyncSpigot
import org.bukkit.Bukkit
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
    private lateinit var lockService: LockService

    @Inject
    private lateinit var memberManager: MemberManager<TextComponent>

    @Inject
    private lateinit var inventorySerializer: InventorySerializer<Player, Inventory, ItemStack>

    @Inject
    private lateinit var timeFormatService: TimeFormatService

    @Inject
    private lateinit var textService: TextService<TextComponent, CommandSender>
    
    override fun onCommand(sender: CommandSender, command: Command, s: String, args: Array<String>): Boolean {
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
                .sendTo(sender)
            try {
                val inventory = Bukkit.createInventory(null, 45, "DataSync Inventory")
                inventorySerializer.deserializeInventory(snapshot, inventory)
                Bukkit.getScheduler().runTask(plugin, Runnable { sender.openInventory(inventory) })
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
