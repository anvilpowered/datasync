package org.anvilpowered.datasync.spigot.command.snapshot;

import net.md_5.bungee.api.chat.TextComponent;
import org.anvilpowered.datasync.common.command.snapshot.CommonSnapshotListCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpigotSnapshotListCommand
    extends CommonSnapshotListCommand<TextComponent, Player, Player, CommandSender>
    implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        execute(sender, args);
        return true;
    }
}
