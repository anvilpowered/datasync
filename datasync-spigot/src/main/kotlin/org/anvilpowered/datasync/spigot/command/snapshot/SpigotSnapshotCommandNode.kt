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
package org.anvilpowered.datasync.spigot.command.snapshot

import com.google.inject.Inject
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.datasync.common.command.snapshot.CommonSnapshotCommandNode
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import java.util.HashMap

class SpigotSnapshotCommandNode @Inject constructor(
    registry: Registry
) : CommonSnapshotCommandNode<CommandExecutor, CommandSender>(registry) {

    @Inject
    private lateinit var snapshotCreateCommand: SpigotSnapshotCreateCommand

    @Inject
    private lateinit var snapshotDeleteCommand: SpigotSnapshotDeleteCommand

    @Inject
    private lateinit var snapshotInfoCommand: SpigotSnapshotInfoCommand

    @Inject
    private lateinit var snapshotListCommand: SpigotSnapshotListCommand

    @Inject
    private lateinit var snapshotRestoreCommand: SpigotSnapshotRestoreCommand

    @Inject
    private lateinit var snapshotViewCommand: SpigotSnapshotViewCommand

    private val subCommands: MutableMap<List<String>, CommandExecutor?> = HashMap()
    override fun loadCommands() {
        subCommands[CREATE_ALIAS] = snapshotCreateCommand
        subCommands[DELETE_ALIAS] = snapshotDeleteCommand
        subCommands[INFO_ALIAS] = snapshotInfoCommand
        subCommands[LIST_ALIAS] = snapshotListCommand
        subCommands[RESTORE_ALIAS] = snapshotRestoreCommand
        subCommands[VIEW_ALIAS] = snapshotViewCommand
    }

    fun getSubCommands(): Map<List<String>, CommandExecutor?> {
        return subCommands
    }
}
