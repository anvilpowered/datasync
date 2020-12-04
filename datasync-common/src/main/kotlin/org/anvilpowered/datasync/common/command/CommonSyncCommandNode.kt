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
package org.anvilpowered.datasync.common.command

import com.google.inject.Inject
import org.anvilpowered.anvil.api.Environment
import org.anvilpowered.anvil.api.command.CommandNode
import org.anvilpowered.anvil.api.command.CommandService
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.datasync.common.plugin.DataSyncPluginInfo
import java.util.Arrays
import java.util.HashMap
import java.util.function.Function
import java.util.function.Predicate

abstract class CommonSyncCommandNode<TCommandExecutor, TCommandSource> protected constructor(
    protected var registry: Registry
) : CommandNode<TCommandSource> {
    companion object {
        val LOCK_ALIAS = Arrays.asList("lock", "l")
        val RELOAD_ALIAS = listOf("reload")
        val TEST_ALIAS = listOf("test")
        val UPLOAD_ALIAS = Arrays.asList("upload", "up")
        val HELP_ALIAS = listOf("help")
        val VERSION_ALIAS = listOf("version")
        const val LOCK_DESCRIPTION = "Lock / Unlock sensitive commands."
        const val RELOAD_DESCRIPTION = "Reloads DataSync."
        const val TEST_DESCRIPTION = "Uploads and then downloads a snapshot."
        const val UPLOAD_DESCRIPTION = "Uploads all players on server."
        const val HELP_DESCRIPTION = "Shows this help page."
        const val VERSION_DESCRIPTION = "Shows plugin version."
        val ROOT_DESCRIPTION = String.format("%s root command", DataSyncPluginInfo.name)
        const val RELOAD_USAGE = "[-a|--all|-r|--regex] [plugin]"
        const val HELP_COMMAND = "/datasync help"
        var SYNC_PATH = arrayOf("sync")
        private const val ERROR_MESSAGE = "Sync command has not been loaded yet"
    }

    private var alreadyLoaded: Boolean
    private var descriptions: MutableMap<List<String>, Function<TCommandSource, String>>
    private var permissions: Map<List<String>, Predicate<TCommandSource>>

    private var usages: MutableMap<List<String>, Function<TCommandSource, String>>

    @Inject
    protected lateinit var commandService: CommandService<TCommandExecutor, TCommandSource>

    @Inject
    protected lateinit var environment: Environment

    init {
        alreadyLoaded = false
        registry.whenLoaded {
            if (alreadyLoaded) return@whenLoaded
            loadCommands()
            alreadyLoaded = true
        }.register()
        alreadyLoaded = false
        descriptions = HashMap()
        permissions = HashMap()
        usages = HashMap()
        descriptions[LOCK_ALIAS] = Function { c: TCommandSource -> LOCK_DESCRIPTION }
        descriptions[RELOAD_ALIAS] = Function { c: TCommandSource -> RELOAD_DESCRIPTION }
        descriptions[TEST_ALIAS] = Function { c: TCommandSource -> TEST_DESCRIPTION }
        descriptions[UPLOAD_ALIAS] = Function { c: TCommandSource -> UPLOAD_DESCRIPTION }
        descriptions[HELP_ALIAS] = Function { c: TCommandSource -> HELP_DESCRIPTION }
        descriptions[VERSION_ALIAS] = Function { c: TCommandSource -> VERSION_DESCRIPTION }
        usages[RELOAD_ALIAS] = Function { c: TCommandSource -> RELOAD_USAGE }
    }

    protected abstract fun loadCommands()
    override fun getDescriptions(): Map<List<String>, Function<TCommandSource, String>> = descriptions
    override fun getPermissions(): Map<List<String>, Predicate<TCommandSource>> = permissions
    override fun getUsages(): Map<List<String>, Function<TCommandSource, String>> = usages
    override fun getName(): String = DataSyncPluginInfo.id
}
