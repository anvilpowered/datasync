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
import org.anvilpowered.datasync.api.registry.DataSyncKeys
import org.anvilpowered.datasync.api.serializer.user.UserSerializerManager

open class CommonSyncTestCommand<
    TString : Any,
    TUser : Any,
    TPlayer : Any,
    TCommandSource : Any> {

    @Inject
    private lateinit var permissionService: PermissionService

    @Inject
    protected lateinit var registry: Registry

    @Inject
    private lateinit var textService: TextService<TString, TCommandSource>

    @Inject
    private lateinit var userService: UserService<TUser, TPlayer>

    @Inject
    private lateinit var userSerializerManager: UserSerializerManager<TUser, TString>
    
    fun execute(source: TCommandSource, playerClass: Class<TPlayer>) {
        if (!playerClass.isAssignableFrom(source.javaClass)) {
            textService.builder()
                .appendPrefix()
                .yellow().append("Player only command!")
                .sendTo(source)
            return
        }
        if (!permissionService.hasPermission(source, registry.getOrDefault(DataSyncKeys.TEST_COMMAND_PERMISSION))) {
            textService.builder()
                .appendPrefix()
                .red().append("Insufficient Permissions!")
                .sendTo(source)
            return
        }
        val optionalPlayer = userService.getPlayer(source as TUser)
        if (!optionalPlayer.isPresent) {
            return
        }
        userSerializerManager.serialize(optionalPlayer.get() as TUser)
            .exceptionally { e: Throwable ->
                e.printStackTrace()
                textService.send(textService.of("An error has occurred!"), source)
                null
            }
            .thenAcceptAsync { text: TString? ->
                if (text == null) {
                    return@thenAcceptAsync
                }
                textService.send(text, source)
                textService.builder()
                    .appendPrefix()
                    .green().append("Deserializing in 5 seconds")
                    .sendTo(source)
                try {
                    Thread.sleep(5000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                userSerializerManager.restore(userService.getUUID(optionalPlayer.get() as TUser), null)
                    .thenAcceptAsync { msg: TString? -> textService.send(msg, source) }
            }
    }
}
