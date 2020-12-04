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
package org.anvilpowered.datasync.sponge.command.snapshot

import com.google.inject.Inject
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.datasync.api.registry.DataSyncKeys
import org.anvilpowered.datasync.common.command.CommonSyncCommandNode
import org.anvilpowered.datasync.common.command.snapshot.CommonSnapshotCommandNode
import org.spongepowered.api.command.CommandCallable
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandExecutor
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.text.Text
import java.util.HashMap

class SpongeSnapshotCommandNode @Inject constructor(
    registry: Registry
) : CommonSnapshotCommandNode<CommandExecutor, CommandSource>(registry) {

    companion object{
        var root : CommandSpec? = null
    }

    @Inject
    private lateinit var snapshotCreateCommand: SpongeSnapshotCreateCommand

    @Inject
    private lateinit var snapshotDeleteCommand: SpongeSnapshotDeleteCommand

    @Inject
    private lateinit var snapshotInfoCommand: SpongeSnapshotInfoCommand

    @Inject
    private lateinit var snapshotListCommand: SpongeSnapshotListCommand

    @Inject
    private lateinit var snapshotRestoreCommand: SpongeSnapshotRestoreCommand

    @Inject
    private lateinit var snapshotViewCommand: SpongeSnapshotViewCommand

    override fun loadCommands() {
        val subCommands: MutableMap<List<String>, CommandCallable> = HashMap()
        subCommands[CREATE_ALIAS] = snapshotCreateCommand
        subCommands[DELETE_ALIAS] = snapshotDeleteCommand
        subCommands[VIEW_ALIAS] = snapshotViewCommand
        subCommands[INFO_ALIAS] = snapshotInfoCommand
        subCommands[RESTORE_ALIAS] = snapshotRestoreCommand
        subCommands[LIST_ALIAS] = snapshotListCommand
        subCommands[HELP_ALIAS] = CommandSpec.builder()
            .description(Text.of(HELP_DESCRIPTION))
            .permission(registry.getOrDefault(DataSyncKeys.SNAPSHOT_BASE_PERMISSION))
            .executor(commandService.generateHelpCommand(this))
            .build()
        root = CommandSpec.builder()
            .description(Text.of(ROOT_DESCRIPTION))
            .permission(DataSyncKeys.SNAPSHOT_BASE_PERMISSION.fallbackValue)
            .executor(commandService.generateRootCommand(HELP_COMMAND))
            .children(subCommands)
            .build()
    }

    fun getRoot(): CommandSpec {
        if (root == null) {
            loadCommands()
        }
        return root!!
    }

    override fun getName(): String {
        return "snapshot"
    }

    override fun getPath(): Array<String> {
        return CommonSyncCommandNode.SYNC_PATH
    }
}
