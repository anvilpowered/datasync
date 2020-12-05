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
package org.anvilpowered.datasync.sponge.command.optimize

import com.google.inject.Inject
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.datasync.api.registry.DataSyncKeys
import org.anvilpowered.datasync.common.command.optimize.CommonOptimizeCommandNode
import org.spongepowered.api.command.CommandCallable
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.spec.CommandExecutor
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.text.Text
import java.util.HashMap

class SpongeOptimizeCommandNode @Inject constructor(
    registry: Registry
) : CommonOptimizeCommandNode<CommandExecutor, CommandSource>(registry) {

    companion object {
        var root: CommandSpec? = null
    }

    @Inject
    private lateinit var optimizeInfoCommand: SpongeOptimizeInfoCommand

    @Inject
    private lateinit var optimizeStartCommand: SpongeOptimizeStartCommand

    @Inject
    private lateinit var optimizeStopCommand: SpongeOptimizeStopCommand

    override fun loadCommands() {
        val subCommands: MutableMap<List<String>, CommandCallable> = HashMap()
        val optimizeStartChoices: MutableMap<String, String> = HashMap()
        optimizeStartChoices["all"] = "all"
        optimizeStartChoices["user"] = "user"
        subCommands[START_ALIAS] = optimizeStartCommand
        subCommands[INFO_ALIAS] = optimizeInfoCommand
        subCommands[STOP_ALIAS] = optimizeStopCommand
        subCommands[HELP_ALIAS] = CommandSpec.builder()
            .description(Text.of(HELP_DESCRIPTION))
            .permission(registry.getOrDefault(DataSyncKeys.MANUAL_OPTIMIZATION_BASE_PERMISSION))
            .executor(commandService.generateHelpCommand(this))
            .build()
        root = CommandSpec.builder()
            .description(Text.of(ROOT_DESCRIPTION))
            .permission(registry.getOrDefault(DataSyncKeys.MANUAL_OPTIMIZATION_BASE_PERMISSION))
            .executor(commandService.generateRootCommand(HELP_COMMAND))
            .children(subCommands)
            .build()
    }

    fun getRoot(): CommandSpec {
        if (root == null) {
            loadCommands()
        }
        return root!!
    }

    override fun getName(): String = "optimize"
}
