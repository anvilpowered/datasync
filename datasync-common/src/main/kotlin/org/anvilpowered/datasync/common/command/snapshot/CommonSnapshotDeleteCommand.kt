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
import org.anvilpowered.datasync.api.misc.LockService
import org.anvilpowered.datasync.api.registry.DataSyncKeys

open class CommonSnapshotDeleteCommand<TString, TUser, TPlayer, TCommandSource> {
    
    @Inject
    private lateinit var lockService: LockService

    @Inject
    private lateinit var memberManager: MemberManager<TString>

    @Inject
    private lateinit var permissionService: PermissionService

    @Inject
    protected lateinit var registry: Registry

    @Inject
    private lateinit var textService: TextService<TString, TCommandSource>

    @Inject
    private lateinit var userService: UserService<TUser, TPlayer>

    fun execute(source: TCommandSource, context: Array<String>) {
        if (!permissionService.hasPermission(source, registry.getOrDefault(DataSyncKeys.SNAPSHOT_DELETE_PERMISSION))) {
            textService.builder()
                .appendPrefix()
                .red().append("Insufficient Permissions!")
                .sendTo(source)
            return
        }
        if (!lockService.assertUnlocked(source)) {
            return
        }
        if (context.size != 2) {
            textService.builder()
                .appendPrefix()
                .yellow().append("User and Snapshot are required!")
                .sendTo(source)
            return
        }
        val optionalPlayer = userService.getPlayer(context[0])
        if (!optionalPlayer.isPresent) {
            textService.builder()
                .appendPrefix()
                .red().append("Invalid player!")
                .sendTo(source)
            return
        }
        memberManager.deleteSnapshot(userService.getUUID(optionalPlayer.get() as TUser), context[1])
            .thenAcceptAsync { msg: TString -> textService.send(msg, source) }
    }
}
