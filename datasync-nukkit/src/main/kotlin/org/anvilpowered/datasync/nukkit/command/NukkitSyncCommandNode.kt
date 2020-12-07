/*
 *   DataSync - AnvilPowered
 *   Copyright (C) 2020 Cableguy20
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.datasync.nukkit.command

import cn.nukkit.Server
import cn.nukkit.command.CommandExecutor
import cn.nukkit.command.CommandSender
import cn.nukkit.command.PluginCommand
import cn.nukkit.plugin.Plugin
import com.google.common.collect.ImmutableList
import com.google.inject.Inject
import com.google.inject.Singleton
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.datasync.common.command.CommonSyncCommandNode
import org.anvilpowered.datasync.nukkit.DataSyncNukkit
import org.anvilpowered.datasync.nukkit.command.snapshot.NukkitSnapshotCommandNode

@Singleton
class NukkitSyncCommandNode @Inject constructor(
    registry: Registry
) : CommonSyncCommandNode<CommandExecutor, CommandSender>(registry) {


    @Inject
    private lateinit var plugin: DataSyncNukkit

    @Inject
    private lateinit var syncLockCommand: NukkitSyncLockCommand

    @Inject
    private lateinit var syncReloadCommand: NukkitSyncReloadCommand

    @Inject
    private lateinit var syncTestCommand: NukkitSyncTestCommand

    @Inject
    private lateinit var syncUploadCommand: NukkitSyncUploadCommand


    @Inject
    private lateinit var snapshotCommandNode: NukkitSnapshotCommandNode

    override fun loadCommands() {
        val subCommands: MutableMap<List<String>, CommandExecutor> = HashMap()
        subCommands[LOCK_ALIAS] = syncLockCommand
        subCommands[RELOAD_ALIAS] = syncReloadCommand
        subCommands[TEST_ALIAS] = syncTestCommand
        subCommands[UPLOAD_ALIAS] = syncUploadCommand
        subCommands[ImmutableList.of("snapshot", "snap", "s")] = commandService.generateRoutingCommand(
            commandService.generateHelpCommand(snapshotCommandNode), snapshotCommandNode.getSubCommands(), false
        )
        val root: PluginCommand<Plugin> = PluginCommand(name, plugin)
        root.executor = commandService.generateRoutingCommand(
            commandService.generateRootCommand(HELP_COMMAND), subCommands, false)

        Server.getInstance().commandMap.register(name, root)
    }
}
