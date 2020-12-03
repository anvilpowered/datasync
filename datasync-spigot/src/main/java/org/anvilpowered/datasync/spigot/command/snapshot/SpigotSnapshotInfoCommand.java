package org.anvilpowered.datasync.spigot.command.snapshot;

import net.md_5.bungee.api.chat.TextComponent;
import org.anvilpowered.datasync.common.command.snapshot.CommonSnapshotInfoCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpigotSnapshotInfoCommand
    extends CommonSnapshotInfoCommand<TextComponent, Player, Player, CommandSender>
    implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] context) {
        execute(sender, context);
        return true;
    }
}
