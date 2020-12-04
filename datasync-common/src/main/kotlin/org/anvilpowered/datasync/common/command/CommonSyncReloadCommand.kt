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
import org.anvilpowered.datasync.api.registry.DataSyncKeys

open class CommonSyncReloadCommand<
    TString : Any,
    TCommandSource : Any> {

    @Inject
    private lateinit var permissionService: PermissionService

    @Inject
    protected lateinit var registry: Registry

    @Inject
    private lateinit var textService: TextService<TString, TCommandSource>

    fun execute(source: TCommandSource) {
        if (!permissionService.hasPermission(source, registry.getOrDefault(DataSyncKeys.RELOAD_COMMAND_PERMISSION))) {
            textService.builder()
                .appendPrefix()
                .red().append("Insufficient Permissions!")
                .sendTo(source)
            return
        }
        registry.load()
        textService.builder()
            .appendPrefix()
            .green().append("Successfully reloaded!")
            .sendTo(source)
    }
}
