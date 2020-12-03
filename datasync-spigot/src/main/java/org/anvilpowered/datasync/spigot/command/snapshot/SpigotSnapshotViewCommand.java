package org.anvilpowered.datasync.spigot.command.snapshot;

import com.google.inject.Inject;
import net.md_5.bungee.api.chat.TextComponent;
import org.anvilpowered.anvil.api.util.TextService;
import org.anvilpowered.anvil.api.util.TimeFormatService;
import org.anvilpowered.datasync.api.member.MemberManager;
import org.anvilpowered.datasync.api.model.snapshot.Snapshot;
import org.anvilpowered.datasync.api.registry.DataSyncKeys;
import org.anvilpowered.datasync.api.serializer.InventorySerializer;
import org.anvilpowered.datasync.api.snapshot.SnapshotManager;
import org.anvilpowered.datasync.spigot.DataSyncSpigot;
import org.anvilpowered.datasync.spigot.command.SpigotSyncLockCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.function.Consumer;

public class SpigotSnapshotViewCommand implements CommandExecutor {

    @Inject
    private DataSyncSpigot plugin;

    @Inject
    private MemberManager<TextComponent> memberManager;

    @Inject
    private SnapshotManager<String> snapshotManager;

    @Inject
    private InventorySerializer<Player, Inventory, ItemStack> inventorySerializer;

    @Inject
    private TimeFormatService timeFormatService;

    @Inject
    private TextService<TextComponent, CommandSender> textService;

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.hasPermission(DataSyncKeys.SNAPSHOT_VIEW_BASE_PERMISSION.getFallbackValue())) {
            textService.builder()
                .appendPrefix()
                .red().append("Insufficient Permissions!")
                .sendTo(sender);
            return false;
        }
        if (!(sender instanceof Player)) {
            textService.builder()
                .appendPrefix()
                .yellow().append("Player only command!")
                .sendToConsole();
            return false;
        }
        if (!SpigotSyncLockCommand.assertUnlocked(sender)) {
            return false;
        }
        String player;
        String snapshot0;

        if (args.length == 0) {
            textService.builder()
                .appendPrefix()
                .red().append("User is required!")
                .sendTo(sender);
            return false;
        } else if (args.length == 1) {
            player = args[0];
            snapshot0 = null;
        } else {
            player = args[0];
            snapshot0 = args[1];
        }

        Optional<Player> optionalPlayer = Optional.ofNullable(Bukkit.getPlayer(player));
        if (!optionalPlayer.isPresent()) {
            return false;
        }
        Player targetPlayer = optionalPlayer.get();
        Consumer<Optional<? extends Snapshot<?>>> afterFound = optionalSnapshot -> {
            if (!optionalSnapshot.isPresent()) {
                return;
            }
            Snapshot<?> snapshot = optionalSnapshot.get();
            textService.builder()
                .appendPrefix()
                .yellow().append("Editing snapshot ")
                .gold().append(timeFormatService.format(optionalSnapshot.get().getCreatedUtc()))
                .sendTo(sender);

            try {
                Inventory inventory = Bukkit.createInventory(null, 45, "DataSync Inventory");
                inventorySerializer.deserializeInventory(snapshot, inventory);
                Bukkit.getScheduler().runTask(plugin, () -> {
                    ((Player) sender).openInventory(inventory);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        memberManager.getPrimaryComponent().getSnapshotForUser(
            targetPlayer.getUniqueId(),
            snapshot0
        ).thenAccept(afterFound);
        return true;
    }
}
