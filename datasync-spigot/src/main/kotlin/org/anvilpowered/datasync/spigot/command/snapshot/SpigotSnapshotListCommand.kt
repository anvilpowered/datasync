package org.anvilpowered.datasync.spigot.command.snapshot

import net.md_5.bungee.api.chat.TextComponent
import org.anvilpowered.datasync.common.command.snapshot.CommonSnapshotListCommand
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SpigotSnapshotListCommand : CommonSnapshotListCommand<TextComponent, Player, Player, CommandSender?>(), CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, s: String, args: Array<String>): Boolean {
        execute(sender, args)
        return true
    }
}
