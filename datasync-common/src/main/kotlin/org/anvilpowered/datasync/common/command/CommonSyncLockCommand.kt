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
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.anvil.api.util.PermissionService
import org.anvilpowered.anvil.api.util.TextService
import org.anvilpowered.anvil.api.util.UserService
import org.anvilpowered.datasync.api.misc.LockService
import org.anvilpowered.datasync.api.registry.DataSyncKeys

open class CommonSyncLockCommand<
    TString : Any,
    TUser : Any,
    TPlayer : Any,
    TCommandSource : Any> {

    @Inject
    private lateinit var lockService: LockService

    @Inject
    private lateinit var permissionService: PermissionService

    @Inject
    protected lateinit var registry: Registry

    @Inject
    private lateinit var textService: TextService<TString, TCommandSource>

    @Inject
    private lateinit var userService: UserService<TUser, TPlayer>

    fun execute(source: TCommandSource, context: Array<String>, playerClass: Class<TPlayer>) {
        if (!playerClass.isAssignableFrom(source.javaClass)) {
            textService.builder()
                .appendPrefix()
                .yellow().append("Console is always unlocked!")
                .sendTo(source)
            return
        }
        if (!permissionService.hasPermission(source, registry.getOrDefault(DataSyncKeys.LOCK_COMMAND_PERMISSION))) {
            return
        }
        val player = userService.getPlayer(source as TUser)
        if (!player.isPresent) {
            return
        }
        val index = lockService.unlockedPlayers.indexOf(userService.getUUID(source as TUser))
        val status = if (index >= 0) "unlocked" else "locked"
        if (context.size < 1) {
            textService.builder()
                .appendPrefix()
                .yellow().append("Currently $status")
                .sendTo(source)
            return
        }
        when (context[0]) {
            "on" -> if (index >= 0) {
                lockService.unlockedPlayers.removeAt(index)
                textService.builder()
                    .appendPrefix()
                    .yellow().append("Lock enabled")
                    .sendTo(source)
            } else {
                textService.builder()
                    .appendPrefix()
                    .yellow().append("Lock already enabled")
                    .sendTo(source)
            }
            "off" -> if (index < 0) {
                lockService.add(userService.getUUID(source as TUser))
                textService.builder()
                    .appendPrefix()
                    .yellow().append("Lock disabled")
                    .red().append(" (be careful)")
                    .sendTo(source)
            } else {
                textService.builder()
                    .appendPrefix()
                    .yellow().append("Lock already disabled")
                    .sendTo(source)
            }
            else -> textService.builder()
                .appendPrefix()
                .red().append("Unrecognized option: \"" + context[0] + "\". Lock is")
                .yellow().append(status)
                .sendTo(source)
        }
    }
}
