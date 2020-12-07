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
package org.anvilpowered.datasync.sponge.command

import com.google.inject.Inject
import com.google.inject.Singleton
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.datasync.common.command.CommonSyncCommandNode
import org.anvilpowered.datasync.sponge.command.optimize.SpongeOptimizeCommandNode
import org.anvilpowered.datasync.sponge.command.snapshot.SpongeSnapshotCommandNode
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandCallable
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.spec.CommandExecutor
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.text.Text
import java.util.Arrays
import java.util.HashMap

@Singleton
class SpongeSyncCommandNode @Inject constructor(
    registry: Registry
) : CommonSyncCommandNode<CommandExecutor, CommandSource>(registry) {

    companion object {
        var root: CommandSpec? = null
    }

    @Inject
    private lateinit var optimizeCommandNode: SpongeOptimizeCommandNode

    @Inject
    private lateinit var snapshotCommandNode: SpongeSnapshotCommandNode

    @Inject
    private lateinit var syncLockCommand: SpongeSyncLockCommand

    @Inject
    private lateinit var syncReloadCommand: SpongeSyncReloadCommand

    @Inject
    private lateinit var syncTestCommand: SpongeSyncTestCommand

    @Inject
    private lateinit var syncUploadCommand: SpongeSyncUploadCommand

    override fun loadCommands() {
        println("Loading Commands")
        val subCommands: MutableMap<List<String>, CommandCallable> = HashMap()
        subCommands[Arrays.asList("optimize", "opt", "o")] = optimizeCommandNode.getRoot()
        subCommands[Arrays.asList("snapshot", "snap", "s")] = snapshotCommandNode.getRoot()
        val lockChoices: MutableMap<String, String> = HashMap()
        lockChoices["on"] = "on"
        lockChoices["off"] = "off"
        subCommands[LOCK_ALIAS] = syncLockCommand
        subCommands[RELOAD_ALIAS] = syncReloadCommand
        subCommands[TEST_ALIAS] = syncTestCommand
        subCommands[UPLOAD_ALIAS] = syncUploadCommand
        subCommands[HELP_ALIAS] = CommandSpec.builder()
            .description(Text.of(HELP_DESCRIPTION))
            .executor(commandService.generateHelpCommand(this))
            .build()
        subCommands[VERSION_ALIAS] = CommandSpec.builder()
            .description(Text.of(VERSION_DESCRIPTION))
            .executor(commandService.generateVersionCommand(HELP_COMMAND))
            .build()
        root = CommandSpec.builder()
            .description(Text.of(ROOT_DESCRIPTION))
            .executor(commandService.generateRootCommand(HELP_COMMAND))
            .children(subCommands)
            .build()
        Sponge.getCommandManager()
            .register(environment.plugin, root!!, "sync", "datasync")
    }
}
