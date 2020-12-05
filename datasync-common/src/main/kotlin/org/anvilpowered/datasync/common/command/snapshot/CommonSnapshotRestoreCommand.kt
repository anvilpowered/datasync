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
import org.anvilpowered.datasync.api.misc.LockService
import org.anvilpowered.datasync.api.plugin.PluginMessages
import org.anvilpowered.datasync.api.registry.DataSyncKeys
import org.anvilpowered.datasync.api.serializer.user.UserSerializerManager

open class CommonSnapshotRestoreCommand<TString, TUser, TPlayer, TCommandSource> {
    @Inject
    private lateinit var lockService: LockService

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

    @Inject
    private lateinit var userSerializerManager: UserSerializerManager<TUser, TString>

    fun execute(source: TCommandSource, context: Array<String>) {
        if (!permissionService.hasPermission(source, registry.getOrDefault(DataSyncKeys.SNAPSHOT_RESTORE_PERMISSION))) {
            textService.send(pluginMessages.noPermissions, source)
            return
        }
        if (!lockService.assertUnlocked(source)) {
            return
        }
        val player: String?
        val snapshot: String?
        when {
            context.isEmpty() -> {
                textService.send(pluginMessages.userRequired, source)
                return
            }
            context.size == 1 -> {
                player = context[0]
                snapshot = null
            }
            else -> {
                player = context[0]
                snapshot = context[1]
            }
        }
        val optionalUser = userService.getPlayer(player)
        if (!optionalUser.isPresent) {
            textService.send(pluginMessages.invalidUser, source)
            return
        }
        userSerializerManager.restore(userService.getUUID(optionalUser.get() as TUser), snapshot)
            .thenAcceptAsync { msg: TString -> textService.send(msg, source) }
    }
}
