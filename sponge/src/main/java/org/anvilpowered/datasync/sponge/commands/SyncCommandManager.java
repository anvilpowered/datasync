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

package org.anvilpowered.datasync.sponge.commands;

import com.google.inject.Inject;
import org.anvilpowered.datasync.common.data.key.DataSyncKeys;
import org.anvilpowered.datasync.sponge.commands.optimize.OptimizeHelpCommand;
import org.anvilpowered.datasync.sponge.commands.optimize.OptimizeInfoCommand;
import org.anvilpowered.datasync.sponge.commands.optimize.OptimizeStartCommand;
import org.anvilpowered.datasync.sponge.commands.optimize.OptimizeStopCommand;
import org.anvilpowered.datasync.sponge.commands.snapshot.SnapshotCreateCommand;
import org.anvilpowered.datasync.sponge.commands.snapshot.SnapshotDeleteCommand;
import org.anvilpowered.datasync.sponge.commands.snapshot.SnapshotHelpCommand;
import org.anvilpowered.datasync.sponge.commands.snapshot.SnapshotInfoCommand;
import org.anvilpowered.datasync.sponge.commands.snapshot.SnapshotListCommand;
import org.anvilpowered.datasync.sponge.commands.snapshot.SnapshotRestoreCommand;
import org.anvilpowered.datasync.sponge.commands.snapshot.SnapshotViewCommand;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SyncCommandManager implements CommandManager {

    @Inject
    private SyncBaseCommand syncBaseCommand;

    @Inject
    private SyncHelpCommand syncHelpCommand;

    @Inject
    private SyncInfoCommand syncInfoCommand;

    @Inject
    private SyncUploadCommand syncUploadCommand;

    @Inject
    private SyncLockCommand syncLockCommand;

    @Inject
    private SyncReloadCommand syncReloadCommand;

    @Inject
    private SnapshotCreateCommand snapshotCreateCommand;

    @Inject
    private SnapshotDeleteCommand snapshotDeleteCommand;

    @Inject
    private SnapshotHelpCommand snapshotHelpCommand;

    @Inject
    private SnapshotInfoCommand snapshotInfoCommand;

    @Inject
    private SnapshotListCommand snapshotListCommand;

    @Inject
    private SnapshotRestoreCommand snapshotRestoreCommand;

    @Inject
    private SnapshotViewCommand snapshotViewCommand;

    @Inject
    private OptimizeHelpCommand optimizeHelpCommand;

    @Inject
    private OptimizeInfoCommand optimizeInfoCommand;

    @Inject
    private OptimizeStartCommand optimizeStartCommand;

    @Inject
    private OptimizeStopCommand optimizeStopCommand;

    public static Map<List<String>, CommandSpec> subCommands = new HashMap<>();
    public static Map<List<String>, CommandSpec> snapshotSubCommands = new HashMap<>();
    public static Map<List<String>, CommandSpec> optimizeSubCommands = new HashMap<>();

    @Override
    public void register(Object plugin) {
        Map<List<String>, CommandSpec> subCommands = new HashMap<>();
        Map<List<String>, CommandSpec> snapshotSubCommands = new HashMap<>();
        Map<List<String>, CommandSpec> optimizeSubCommands = new HashMap<>();

        snapshotSubCommands.put(Arrays.asList("create", "c", "upload", "up"), CommandSpec.builder()
            .description(Text.of("Creates a manual snapshot for user and uploads it to DB."))
            .permission(DataSyncKeys.SNAPSHOT_CREATE_PERMISSION.getFallbackValue())
            .arguments(
                GenericArguments.onlyOne(GenericArguments.user(Text.of("user")))
            )
            .executor(snapshotCreateCommand)
            .build()
        );

        snapshotSubCommands.put(Collections.singletonList("delete"), CommandSpec.builder()
            .description(Text.of("Deletes snapshot for user."))
            .permission(DataSyncKeys.SNAPSHOT_DELETE_PERMISSION.getFallbackValue())
            .arguments(
                GenericArguments.onlyOne(GenericArguments.user(Text.of("user"))),
                GenericArguments.string(Text.of("snapshot"))
            )
            .executor(snapshotDeleteCommand)
            .build()
        );

        snapshotSubCommands.put(Arrays.asList("edit", "e", "view"), CommandSpec.builder()
            .description(Text.of("Edit/view snapshot for user from DB. If no date is provided, latest snapshot is selected."))
            .permission(DataSyncKeys.SNAPSHOT_VIEW_BASE_PERMISSION.getFallbackValue())
            .arguments(
                GenericArguments.onlyOne(GenericArguments.user(Text.of("user"))),
                GenericArguments.optional(GenericArguments.string(Text.of("snapshot")))
            )
            .executor(snapshotViewCommand)
            .build()
        );

        snapshotSubCommands.put(Collections.singletonList("help"), CommandSpec.builder()
            .description(Text.of("Shows this help page."))
            .permission(DataSyncKeys.SNAPSHOT_BASE_PERMISSION.getFallbackValue())
            .executor(snapshotHelpCommand)
            .build()
        );

        snapshotSubCommands.put(Arrays.asList("info", "i"), CommandSpec.builder()
            .description(Text.of("More info for snapshot for user from DB. If no date is provided, latest snapshot is selected."))
            .permission(DataSyncKeys.SNAPSHOT_BASE_PERMISSION.getFallbackValue())
            .arguments(
                GenericArguments.onlyOne(GenericArguments.user(Text.of("user"))),
                GenericArguments.optional(GenericArguments.string(Text.of("snapshot")))
            )
            .executor(snapshotInfoCommand)
            .build()
        );

        snapshotSubCommands.put(Arrays.asList("list", "l"), CommandSpec.builder()
            .description(Text.of("Lists available snapshots for user."))
            .permission(DataSyncKeys.SNAPSHOT_BASE_PERMISSION.getFallbackValue())
            .arguments(
                GenericArguments.onlyOne(GenericArguments.user(Text.of("user")))
            )
            .executor(snapshotListCommand)
            .build()
        );

        snapshotSubCommands.put(Arrays.asList("restore", "r", "download", "down"), CommandSpec.builder()
            .description(Text.of("Manually restores snapshot from DB. If no date is selected, latest snapshot is restored."))
            .permission(DataSyncKeys.SNAPSHOT_RESTORE_PERMISSION.getFallbackValue())
            .arguments(
                GenericArguments.onlyOne(GenericArguments.user(Text.of("user"))),
                GenericArguments.optional(GenericArguments.string(Text.of("snapshot")))
            )
            .executor(snapshotRestoreCommand)
            .build()
        );

        subCommands.put(Arrays.asList("snapshot", "snap", "s"), CommandSpec.builder()
            .description(Text.of("Snapshot base command."))
            .permission(DataSyncKeys.SNAPSHOT_BASE_PERMISSION.getFallbackValue())
            .executor(snapshotHelpCommand)
            .children(snapshotSubCommands)
            .build());

        Map<String, String> optimizeStartChoices = new HashMap<>();

        optimizeStartChoices.put("all", "all");
        optimizeStartChoices.put("user", "user");

        optimizeSubCommands.put(Arrays.asList("start", "s"), CommandSpec.builder()
            .description(Text.of("Starts manual optimization, deletes old snapshots."))
            .permission(DataSyncKeys.MANUAL_OPTIMIZATION_BASE_PERMISSION.getFallbackValue())
            .arguments(
                GenericArguments.choices(Text.of("mode"), optimizeStartChoices),
                GenericArguments.optional(GenericArguments.user(Text.of("user")))
            )
            .executor(optimizeStartCommand)
            .build());

        optimizeSubCommands.put(Arrays.asList("info", "i"), CommandSpec.builder()
            .description(Text.of("Gets info on current manual optimization."))
            .permission(DataSyncKeys.MANUAL_OPTIMIZATION_BASE_PERMISSION.getFallbackValue())
            .executor(optimizeInfoCommand)
            .build());

        optimizeSubCommands.put(Collections.singletonList("stop"), CommandSpec.builder()
            .description(Text.of("Stops current manual optimization."))
            .permission(DataSyncKeys.MANUAL_OPTIMIZATION_BASE_PERMISSION.getFallbackValue())
            .executor(optimizeStopCommand)
            .build());

        optimizeSubCommands.put(Collections.singletonList("help"), CommandSpec.builder()
            .description(Text.of("Shows this help page."))
            .permission(DataSyncKeys.MANUAL_OPTIMIZATION_BASE_PERMISSION.getFallbackValue())
            .executor(optimizeHelpCommand)
            .build());

        subCommands.put(Arrays.asList("optimize", "opt", "o"), CommandSpec.builder()
            .description(Text.of("Optimize base command. (To delete old snapshots)"))
            .permission(DataSyncKeys.MANUAL_OPTIMIZATION_BASE_PERMISSION.getFallbackValue())
            .executor(optimizeHelpCommand)
            .children(optimizeSubCommands)
            .build());

        Map<String, String> lockChoices = new HashMap<>();

        lockChoices.put("on", "on");
        lockChoices.put("off", "off");

        subCommands.put(Arrays.asList("lock", "l"), CommandSpec.builder()
            .description(Text.of("Lock / Unlock commands."))
            .permission(DataSyncKeys.LOCK_COMMAND_PERMISSION.getFallbackValue())
            .arguments(
                GenericArguments.optional(GenericArguments.choices(Text.of("value"), lockChoices))
            )
            .executor(syncLockCommand)
            .build());

        subCommands.put(Collections.singletonList("reload"), CommandSpec.builder()
            .description(Text.of("Reloads plugin."))
            .permission(DataSyncKeys.RELOAD_COMMAND_PERMISSION.getFallbackValue())
            .executor(syncReloadCommand)
            .build());

        subCommands.put(Arrays.asList("upload", "up"), CommandSpec.builder()
            .description(Text.of("Uploads all players on server."))
            .permission(DataSyncKeys.SNAPSHOT_CREATE_PERMISSION.getFallbackValue())
            .executor(syncUploadCommand)
            .build());

        subCommands.put(Collections.singletonList("help"), CommandSpec.builder()
            .description(Text.of("Shows this help page."))
            .executor(syncHelpCommand)
            .build());

        subCommands.put(Collections.singletonList("info"), CommandSpec.builder()
            .description(Text.of("Shows plugin info."))
            .executor(syncInfoCommand)
            .build());

        //Build all commands
        CommandSpec mainCommand = CommandSpec.builder()
            .description(Text.of("Displays all available sync sub commands."))
            .executor(syncBaseCommand)
            .children(subCommands)
            .build();

        //Register commands
        Sponge.getCommandManager().register(plugin, mainCommand, "sync", "msdatasync", "datasync", "synchronize");
        SyncCommandManager.subCommands = subCommands;
        SyncCommandManager.snapshotSubCommands = snapshotSubCommands;
        SyncCommandManager.optimizeSubCommands = optimizeSubCommands;
    }
}
