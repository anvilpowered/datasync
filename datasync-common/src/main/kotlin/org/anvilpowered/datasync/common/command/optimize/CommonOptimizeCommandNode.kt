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
package org.anvilpowered.datasync.common.command.optimize

import com.google.inject.Inject
import org.anvilpowered.anvil.api.command.CommandNode
import org.anvilpowered.anvil.api.command.CommandService
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.datasync.common.command.CommonSyncCommandNode
import org.anvilpowered.datasync.common.plugin.DataSyncPluginInfo
import java.util.HashMap
import java.util.function.Function
import java.util.function.Predicate

abstract class CommonOptimizeCommandNode<TCommandExecutor, TCommandSource> protected constructor(
    protected var registry: Registry
) : CommandNode<TCommandSource> {
    companion object {
        val START_ALIAS = listOf("start", "s")
        val INFO_ALIAS = listOf("info", "i")
        val STOP_ALIAS = listOf("stop")
        val HELP_ALIAS = listOf("help")

        const val START_DESCRIPTION = "Starts manual optimization, deletes old snapshots."
        const val INFO_DESCRIPTION = "Gets info on current manual optimization."
        const val STOP_DESCRIPTION = "Stops current manual optimization."
        const val HELP_DESCRIPTION = "Shows this help page."
        const val ROOT_DESCRIPTION = "Optimize base command. (To delete old snapshots)"

        const val START_USAGE = "all|user [<user>]"

        const val HELP_COMMAND = "/sync optimize help"
    }

    private var alreadyLoaded: Boolean
    private var descriptions: MutableMap<List<String>, Function<TCommandSource, String>>
    private var permissions: Map<List<String>, Predicate<TCommandSource>>
    private var usages: MutableMap<List<String>, Function<TCommandSource, String>>

    @Inject
    protected lateinit var commandService: CommandService<TCommandExecutor, TCommandSource>

    init {
        alreadyLoaded = false
        registry.whenLoaded {
            if (alreadyLoaded) return@whenLoaded
            loadCommands()
            alreadyLoaded = true
        }.register()
        alreadyLoaded = false
        descriptions = mutableMapOf()
        permissions = mutableMapOf()
        usages = mutableMapOf()
        descriptions.put(START_ALIAS) { START_DESCRIPTION }
        descriptions.put(INFO_ALIAS) { INFO_DESCRIPTION }
        descriptions.put(STOP_ALIAS) { INFO_DESCRIPTION }
        descriptions.put(HELP_ALIAS) { HELP_DESCRIPTION }
        usages.put(START_ALIAS) { START_USAGE }
    }

    protected abstract fun loadCommands()
    override fun getDescriptions(): Map<List<String>, Function<TCommandSource, String>> = descriptions
    override fun getPermissions(): Map<List<String>, Predicate<TCommandSource>> = permissions
    override fun getUsages(): Map<List<String>, Function<TCommandSource, String>> = usages
    override fun getName(): String = DataSyncPluginInfo.id
    override fun getPath(): Array<String> = CommonSyncCommandNode.SYNC_PATH
}
