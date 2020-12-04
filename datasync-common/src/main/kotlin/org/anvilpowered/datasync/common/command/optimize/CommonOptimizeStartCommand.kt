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
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.anvil.api.util.PermissionService
import org.anvilpowered.anvil.api.util.TextService
import org.anvilpowered.anvil.api.util.UserService
import org.anvilpowered.datasync.api.registry.DataSyncKeys
import org.anvilpowered.datasync.api.snapshotoptimization.SnapshotOptimizationManager
import java.util.ArrayList
import java.util.Arrays

open class CommonOptimizeStartCommand<
    TString : Any,
    TUser : Any,
    TPlayer : Any,
    TCommandSource : Any> {
    
    @Inject
    private lateinit var permissionService: PermissionService

    @Inject
    protected lateinit var registry: Registry

    @Inject
    private lateinit var snapshotOptimizationManager: SnapshotOptimizationManager<TUser, TString, TCommandSource>

    @Inject
    private lateinit var textService: TextService<TString, TCommandSource>

    @Inject
    private lateinit var userService: UserService<TUser, TPlayer>
    
    fun execute(source: TCommandSource, context: Array<String>) {
        if (!permissionService.hasPermission(source, registry.getOrDefault(DataSyncKeys.MANUAL_OPTIMIZATION_BASE_PERMISSION))) {
            textService.builder()
                .appendPrefix()
                .red().append("Insufficient Permissions!")
                .sendTo(source)
            return
        }
        if (context.isEmpty()) {
            textService.builder()
                .appendPrefix()
                .yellow().append("Mode is required")
                .sendTo(source)
            return
        }
        val mode = context[0]
        if (context.size < 2) {
            textService.builder()
                .appendPrefix()
                .yellow().append("No users were selected by your query")
                .sendTo(source)
        }
        context[0] = ""
        val playerNames = Arrays.asList(*context.clone())
        if (mode == "all") {
            if (!permissionService.hasPermission(source,
                    registry.getOrDefault(DataSyncKeys.MANUAL_OPTIMIZATION_ALL_PERMISSION))) {
                textService.builder()
                    .appendPrefix()
                    .red().append("You do not have permission to start optimization task: all")
                    .sendTo(source)
            } else if (snapshotOptimizationManager.primaryComponent.optimize(source)) {
                textService.builder()
                    .appendPrefix()
                    .yellow().append("Successfully started optimization task: all")
                    .sendTo(source)
            } else {
                textService.builder()
                    .appendPrefix()
                    .yellow().append("Optimizer already running! Use /sync optimize info")
            }
        } else {
            if (playerNames.isEmpty()) {
                textService.builder()
                    .appendPrefix()
                    .yellow().append("No users were selected by your query")
                    .sendTo(source)
                return
            } else {
                val players: MutableList<TUser> = ArrayList()
                for (name in playerNames) {
                    if (!userService[name].isPresent) {
                        continue
                    }
                    players.add(userService[name].get())
                }
                if (snapshotOptimizationManager.primaryComponent.optimize(players, source, "Manual")) {
                    textService.builder()
                        .appendPrefix()
                        .yellow().append("Successfully started optimization task: user")
                        .sendTo(source)
                } else {
                    textService.builder()
                        .appendPrefix()
                        .yellow().append("Optimizer already running! Use /sync optimize info")
                        .sendTo(source)
                }
            }
        }
    }
}
