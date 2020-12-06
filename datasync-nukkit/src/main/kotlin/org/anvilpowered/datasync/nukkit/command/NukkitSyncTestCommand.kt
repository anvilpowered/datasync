package org.anvilpowered.datasync.nukkit.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandExecutor
import cn.nukkit.command.CommandSender
import org.anvilpowered.datasync.common.command.CommonSyncTestCommand

class NukkitSyncTestCommand : CommonSyncTestCommand<String, Player, Player, CommandSender>(), CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        execute(sender, Player::class.java)
        return true
    }
}
