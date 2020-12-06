package org.anvilpowered.datasync.nukkit.command

import cn.nukkit.Server
import cn.nukkit.command.CommandExecutor
import cn.nukkit.command.CommandSender
import cn.nukkit.command.PluginCommand
import cn.nukkit.plugin.Plugin
import com.google.inject.Inject
import com.google.inject.Singleton
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.datasync.common.command.CommonSyncCommandNode
import org.anvilpowered.datasync.nukkit.DataSyncNukkit

@Singleton
class NukkitSyncCommandNode @Inject constructor(
    registry: Registry
) : CommonSyncCommandNode<CommandExecutor, CommandSender>(registry) {


    @Inject
    private lateinit var plugin: DataSyncNukkit

    @Inject
    private lateinit var syncTestCommand: NukkitSyncTestCommand

    override fun loadCommands() {
        val subCommands: MutableMap<List<String>, CommandExecutor> = HashMap()
        subCommands[TEST_ALIAS] = syncTestCommand

        val root : PluginCommand<Plugin> = PluginCommand(name, plugin)
        root.executor = commandService.generateRoutingCommand(
            commandService.generateRootCommand(HELP_COMMAND), subCommands, false)

        Server.getInstance().commandMap.register(name, root)
    }
}
