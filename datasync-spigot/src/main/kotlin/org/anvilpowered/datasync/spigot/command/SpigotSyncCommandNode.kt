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

package org.anvilpowered.datasync.spigot.command

import com.google.common.collect.ImmutableList
import com.google.inject.Inject
import com.google.inject.Singleton
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.datasync.common.command.CommonSyncCommandNode
import org.anvilpowered.datasync.spigot.DataSyncSpigot
import org.anvilpowered.datasync.spigot.command.optimize.SpigotOptimizeCommandNode
import org.anvilpowered.datasync.spigot.command.snapshot.SpigotSnapshotCommandNode
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import java.util.HashMap
import java.util.Objects

@Singleton
class SpigotSyncCommandNode @Inject constructor(
    registry: Registry
) : CommonSyncCommandNode<CommandExecutor, CommandSender>(registry) {
    
    @Inject
    private lateinit var plugin: DataSyncSpigot

    @Inject
    private lateinit var syncLockCommand: SpigotSyncLockCommand

    @Inject
    private lateinit var syncReloadCommand: SpigotSyncReloadCommand

    @Inject
    private lateinit var syncTestCommand: SpigotSyncTestCommand

    @Inject
    private lateinit var syncUploadCommand: SpigotSyncUploadCommand

    @Inject
    private lateinit var optimizeCommandNode: SpigotOptimizeCommandNode

    @Inject
    private lateinit var snapshotCommandNode: SpigotSnapshotCommandNode

    override fun loadCommands() {
        val subCommands: MutableMap<List<String>, CommandExecutor> = HashMap()
        subCommands[LOCK_ALIAS] = syncLockCommand
        subCommands[RELOAD_ALIAS] = syncReloadCommand
        subCommands[TEST_ALIAS] = syncTestCommand
        subCommands[UPLOAD_ALIAS] = syncUploadCommand
        subCommands[HELP_ALIAS] = commandService.generateHelpCommand(this)
        subCommands[ImmutableList.of("optimize", "opt", "o")] = commandService.generateRoutingCommand(
            commandService.generateHelpCommand(optimizeCommandNode), optimizeCommandNode.getSubCommands(), false
        )
        subCommands[ImmutableList.of("snapshot", "snap", "s")] = commandService.generateRoutingCommand(
            commandService.generateHelpCommand(snapshotCommandNode), snapshotCommandNode.getSubCommands(), false
        )
        Objects.requireNonNull(plugin.getCommand("datasync"))?.setExecutor(
            commandService.generateRoutingCommand(
                commandService.generateRootCommand(HELP_COMMAND), subCommands, false
            )
        )
    }
}
