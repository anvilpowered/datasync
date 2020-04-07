/*
 *   DataSync - AnvilPowered
 *   Copyright (C) 2020 Cableguy20
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.datasync.sponge.command;

import com.google.inject.Inject;
import org.anvilpowered.anvil.api.data.registry.Registry;
import org.anvilpowered.datasync.common.command.CommonSyncCommandNode;
import org.anvilpowered.datasync.common.data.key.DataSyncKeys;
import org.anvilpowered.datasync.sponge.command.optimize.SpongeOptimizeCommandNode;
import org.anvilpowered.datasync.sponge.command.snapshot.SpongeSnapshotCommandNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpongeSyncCommandNode
    extends CommonSyncCommandNode<CommandExecutor, CommandSource> {

    @Inject
    private SpongeOptimizeCommandNode optimizeCommandNode;

    @Inject
    private SpongeSnapshotCommandNode snapshotCommandNode;

    @Inject
    private SyncLockCommand syncLockCommand;

    @Inject
    private SyncReloadCommand syncReloadCommand;

    @Inject
    private SyncUploadCommand syncUploadCommand;

    @Inject
    public SpongeSyncCommandNode(Registry registry) {
        super(registry);
    }

    @Override
    protected void loadCommands() {

        Map<List<String>, CommandSpec> subCommands = new HashMap<>();

        subCommands.put(Arrays.asList("optimize", "opt", "o"), optimizeCommandNode.getRoot());
        subCommands.put(Arrays.asList("snapshot", "snap", "s"), snapshotCommandNode.getRoot());

        Map<String, String> lockChoices = new HashMap<>();

        lockChoices.put("on", "on");
        lockChoices.put("off", "off");

        subCommands.put(LOCK_ALIAS, CommandSpec.builder()
            .description(Text.of(LOCK_DESCRIPTION))
            .permission(registry.getDefault(DataSyncKeys.LOCK_COMMAND_PERMISSION))
            .arguments(
                GenericArguments.optional(GenericArguments.choices(Text.of("value"), lockChoices))
            )
            .executor(syncLockCommand)
            .build());

        subCommands.put(RELOAD_ALIAS, CommandSpec.builder()
            .description(Text.of(RELOAD_DESCRIPTION))
            .permission(registry.getOrDefault(DataSyncKeys.RELOAD_COMMAND_PERMISSION))
            .executor(syncReloadCommand)
            .build());

        subCommands.put(UPLOAD_ALIAS, CommandSpec.builder()
            .description(Text.of(UPLOAD_DESCRIPTION))
            .permission(registry.getOrDefault(DataSyncKeys.SNAPSHOT_CREATE_PERMISSION))
            .executor(syncUploadCommand)
            .build());

        subCommands.put(HELP_ALIAS, CommandSpec.builder()
            .description(Text.of(HELP_DESCRIPTION))
            .executor(commandService.generateHelpCommand(this))
            .build());

        subCommands.put(VERSION_ALIAS, CommandSpec.builder()
            .description(Text.of(VERSION_DESCRIPTION))
            .executor(commandService.generateVersionCommand(HELP_COMMAND))
            .build());

        CommandSpec root = CommandSpec.builder()
            .description(Text.of(ROOT_DESCRIPTION))
            .executor(commandService.generateRootCommand(HELP_COMMAND))
            .children(subCommands)
            .build();

        Sponge.getCommandManager()
            .register(environment.getPlugin(), root, "sync", "datasync");
    }
}
