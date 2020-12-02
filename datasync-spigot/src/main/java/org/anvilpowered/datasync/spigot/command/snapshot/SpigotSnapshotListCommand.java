package org.anvilpowered.datasync.spigot.command.snapshot;

import com.google.inject.Inject;
import net.md_5.bungee.api.chat.TextComponent;
import org.anvilpowered.anvil.api.util.TextService;
import org.anvilpowered.datasync.api.member.MemberManager;
import org.anvilpowered.datasync.api.registry.DataSyncKeys;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class SpigotSnapshotListCommand implements CommandExecutor {

    @Inject
    private MemberManager<TextComponent> memberManager;

    @Inject
    private TextService<TextComponent, CommandSender> textService;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.hasPermission(DataSyncKeys.SNAPSHOT_RESTORE_PERMISSION.getFallbackValue())) {
            textService.builder()
                .appendPrefix()
                .red().append("Insufficient Permissions!")
                .sendTo(sender);
            return false;
        }
        if (args.length != 1) {
            textService.builder()
                .appendPrefix()
                .red().append("User is required!")
                .sendTo(sender);
            return false;
        }
        Optional<Player> optionalPlayer = Optional.ofNullable(Bukkit.getPlayer(args[0]));
        if (!optionalPlayer.isPresent()) {
            textService.builder()
                .appendPrefix()
                .red().append("Invalid player!")
                .sendTo(sender);
            return false;
        }
        memberManager.list(optionalPlayer.get().getUniqueId()).thenAcceptAsync(list -> {
            textService.paginationBuilder()
                .title(textService.builder().gold().append("Snapshots - " + args[0]).build())
                .padding(textService.builder().dark_green().append("-"))
                .contents(list).linesPerPage(20)
                .build().sendTo(sender);
        });
        return true;
    }
}
