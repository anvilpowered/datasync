package org.anvilpowered.datasync.spigot.command.snapshot

import net.md_5.bungee.api.chat.TextComponent
import org.anvilpowered.datasync.common.command.snapshot.CommonSnapshotInfoCommand
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SpigotSnapshotInfoCommand : CommonSnapshotInfoCommand<TextComponent, Player, Player, CommandSender>(), CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, s: String, context: Array<String>): Boolean {
        execute(sender, context)
        return true
    }
}
