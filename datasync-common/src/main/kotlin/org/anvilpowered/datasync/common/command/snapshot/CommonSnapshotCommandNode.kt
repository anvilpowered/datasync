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
package org.anvilpowered.datasync.common.command.snapshot

import com.google.inject.Inject
import org.anvilpowered.anvil.api.command.CommandNode
import org.anvilpowered.anvil.api.command.CommandService
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.datasync.common.plugin.DataSyncPluginInfo
import java.util.HashMap
import java.util.function.Function
import java.util.function.Predicate

abstract class CommonSnapshotCommandNode<TCommandExecutor, TCommandSource> protected constructor(
    protected var registry: Registry
) : CommandNode<TCommandSource> {
    companion object {
        val CREATE_ALIAS = listOf("create", "c", "upload", "up")
        val DELETE_ALIAS = listOf("delete")
        val VIEW_ALIAS = listOf("view", "e", "edit")
        val INFO_ALIAS = listOf("info", "i")
        val LIST_ALIAS = listOf("list", "l")
        val RESTORE_ALIAS = listOf("restore", "r", "download", "down")
        val HELP_ALIAS = listOf("help")
        val VERSION_ALIAS = listOf("version")

        const val CREATE_DESCRIPTION = "Creates a manual snapshot for user and uploads it to DB."
        const val DELETE_DESCRIPTION = "Deletes snapshot for user."
        const val VIEW_DESCRIPTION = "Edit/view snapshot for user from DB. If no date is provided, latest snapshot is selected."
        const val INFO_DESCRIPTION = "More info for snapshot for user from DB. If no date is provided, latest snapshot is selected."
        const val LIST_DESCRIPTION = "Lists available snapshots for user."
        const val RESTORE_DESCRIPTION = "Manually restores snapshot from DB. If no date is selected, latest snapshot is restored."
        const val HELP_DESCRIPTION = "Shows this help page."
        const val VERSION_DESCRIPTION = "Shows plugin version."
        const val ROOT_DESCRIPTION = "Snapshot base command."

        const val CREATE_USAGE = "<user>"
        const val DELETE_USAGE = "<user> <snapshot>"
        const val VIEW_USAGE = "<user> [<snapshot>]"
        const val INFO_USAGE = "<user> [<snapshot>]"
        const val RESTORE_USAGE = "<user> [<snapshot>]"
        const val LIST_USAGE = "<user>"

        const val HELP_COMMAND = "/sync snapshot|snap|s help"
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
        descriptions.put(CREATE_ALIAS) { CREATE_DESCRIPTION }
        descriptions.put(DELETE_ALIAS) { DELETE_DESCRIPTION }
        descriptions.put(VIEW_ALIAS) { VIEW_DESCRIPTION }
        descriptions.put(INFO_ALIAS) { INFO_DESCRIPTION }
        descriptions.put(RESTORE_ALIAS) { RESTORE_DESCRIPTION }
        descriptions.put(LIST_ALIAS) { LIST_DESCRIPTION }
        descriptions.put(HELP_ALIAS) { HELP_DESCRIPTION }
        descriptions.put(VERSION_ALIAS) { VERSION_DESCRIPTION }
        usages.put(CREATE_ALIAS) { CREATE_USAGE }
        usages.put(DELETE_ALIAS) { DELETE_USAGE }
        usages.put(VIEW_ALIAS) { VIEW_USAGE }
        usages.put(INFO_ALIAS) { INFO_USAGE }
        usages.put(RESTORE_ALIAS) { RESTORE_USAGE }
        usages.put(LIST_ALIAS) { LIST_USAGE }
    }

    protected abstract fun loadCommands()
    override fun getDescriptions(): Map<List<String>, Function<TCommandSource, String>> = descriptions
    override fun getPermissions(): Map<List<String>, Predicate<TCommandSource>> = permissions
    override fun getUsages(): Map<List<String>, Function<TCommandSource, String>> = usages
    override fun getName(): String = DataSyncPluginInfo.id
}
