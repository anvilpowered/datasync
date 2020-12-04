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
package org.anvilpowered.datasync.spigot.command.optimize

import com.google.inject.Inject
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.datasync.common.command.optimize.CommonOptimizeCommandNode
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import java.util.HashMap

class SpigotOptimizeCommandNode @Inject constructor(
    registry: Registry
) : CommonOptimizeCommandNode<CommandExecutor, CommandSender>(registry) {

    @Inject
    private lateinit var optimizeInfoCommand: SpigotOptimizeInfoCommand

    @Inject
    private lateinit var optimizeStartCommand: SpigotOptimizeStartCommand

    @Inject
    private lateinit var optimizeStopCommand: SpigotOptimizeStopCommand

    private val subCommands: MutableMap<List<String>, CommandExecutor?> = HashMap()
    override fun loadCommands() {
        subCommands[INFO_ALIAS] = optimizeInfoCommand
        subCommands[START_ALIAS] = optimizeStartCommand
        subCommands[STOP_ALIAS] = optimizeStopCommand
    }

    fun getSubCommands(): Map<List<String>, CommandExecutor?> {
        return subCommands
    }
}
