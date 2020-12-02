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

public class SpigotSnapshotInfoCommand implements CommandExecutor {

    @Inject
    private MemberManager<TextComponent> memberManager;

    @Inject
    private TextService<TextComponent, CommandSender> textService;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.hasPermission(DataSyncKeys.SNAPSHOT_BASE_PERMISSION.getFallbackValue())) {
            textService.builder()
                .appendPrefix()
                .red().append("Insufficient Permissions!")
                .sendTo(sender);
            return false;
        }
        String player;
        String snapshot;

        if (args.length == 0) {
            textService.builder()
                .appendPrefix()
                .red().append("User is required!")
                .sendTo(sender);
            return false;
        } else if (args.length == 1) {
            player = args[0];
            snapshot = null;
        } else {
            player = args[0];
            snapshot = args[1];
        }
        Optional<Player> optionalPlayer = Optional.ofNullable(Bukkit.getPlayer(player));
        if (!optionalPlayer.isPresent()) {
            textService.builder()
                .appendPrefix()
                .red().append("Invalid player!")
                .sendTo(sender);
            return false;
        }

        memberManager.info(optionalPlayer.get().getUniqueId(), snapshot)
            .thenAcceptAsync(msg -> textService.send(msg, sender));
        return true;
    }
}
