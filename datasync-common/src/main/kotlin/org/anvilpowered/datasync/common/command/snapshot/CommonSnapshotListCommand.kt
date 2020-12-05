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
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.anvil.api.util.PermissionService
import org.anvilpowered.anvil.api.util.TextService
import org.anvilpowered.anvil.api.util.UserService
import org.anvilpowered.datasync.api.member.MemberManager
import org.anvilpowered.datasync.api.plugin.PluginMessages
import org.anvilpowered.datasync.api.registry.DataSyncKeys

open class CommonSnapshotListCommand<TString, TUser, TPlayer, TCommandSource> {

    @Inject
    private lateinit var memberManager: MemberManager<TString>

    @Inject
    private lateinit var permissionService: PermissionService

    @Inject
    private lateinit var pluginMessages: PluginMessages<TString>

    @Inject
    protected lateinit var registry: Registry

    @Inject
    private lateinit var textService: TextService<TString, TCommandSource>

    @Inject
    private lateinit var userService: UserService<TUser, TPlayer>

    fun execute(source: TCommandSource, context: Array<String>) {
        if (!permissionService.hasPermission(source, registry.getOrDefault(DataSyncKeys.SNAPSHOT_BASE_PERMISSION))) {
            textService.send(pluginMessages.noPermissions, source)
            return
        }
        if (context.size != 1) {
            textService.send(pluginMessages.userRequired, source)
            return
        }
        val optionalPlayer = userService.getPlayer(context[0])
        if (!optionalPlayer.isPresent) {
            textService.send(pluginMessages.invalidUser, source)
            return
        }
        try {
            memberManager.list(userService.getUUID(optionalPlayer.get() as TUser))
                .thenAcceptAsync { list: Iterable<TString> ->
                    textService.paginationBuilder()
                        .title(textService.builder().gold().append("Snapshots - " + context[0]).build())
                        .padding(textService.builder().dark_green().append("-"))
                        .contents(list).linesPerPage(10)
                        .build().sendTo(source)
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
