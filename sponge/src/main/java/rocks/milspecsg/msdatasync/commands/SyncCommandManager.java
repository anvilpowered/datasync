package rocks.milspecsg.msdatasync.commands;

import com.google.inject.Inject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import rocks.milspecsg.msdatasync.PluginPermissions;
import rocks.milspecsg.msdatasync.commands.optimize.OptimizeHelpCommand;
import rocks.milspecsg.msdatasync.commands.optimize.OptimizeInfoCommand;
import rocks.milspecsg.msdatasync.commands.optimize.OptimizeStartCommand;
import rocks.milspecsg.msdatasync.commands.optimize.OptimizeStopCommand;
import rocks.milspecsg.msdatasync.commands.snapshot.*;

import java.util.*;

public class SyncCommandManager implements CommandManager {

    @Inject
    SyncHelpCommand syncHelpCommand;

    @Inject
    SyncInfoCommand syncInfoCommand;

    @Inject
    UploadStartCommand uploadStartCommand;

    @Inject
    SyncLockCommand syncLockCommand;

    @Inject
    SyncReloadCommand syncReloadCommand;

    @Inject
    SnapshotCreateCommand snapshotCreateCommand;

    @Inject
    SnapshotDeleteCommand snapshotDeleteCommand;

    @Inject
    SnapshotHelpCommand snapshotHelpCommand;

    @Inject
    SnapshotInfoCommand snapshotInfoCommand;

    @Inject
    SnapshotListCommand snapshotListCommand;

    @Inject
    SnapshotRestoreCommand snapshotRestoreCommand;

    @Inject
    SnapshotViewCommand snapshotViewCommand;

    @Inject
    OptimizeHelpCommand optimizeHelpCommand;

    @Inject
    OptimizeInfoCommand optimizeInfoCommand;

    @Inject
    OptimizeStartCommand optimizeStartCommand;

    @Inject
    OptimizeStopCommand optimizeStopCommand;

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
            .permission(PluginPermissions.SNAPSHOT_CREATE)
            .arguments(
                GenericArguments.onlyOne(GenericArguments.user(Text.of("user")))
            )
            .executor(snapshotCreateCommand)
            .build()
        );

        snapshotSubCommands.put(Collections.singletonList("delete"), CommandSpec.builder()
            .description(Text.of("Deletes snapshot for user."))
            .permission(PluginPermissions.SNAPSHOT_DELETE)
            .arguments(
                GenericArguments.onlyOne(GenericArguments.user(Text.of("user"))),
                GenericArguments.string(Text.of("date"))
            )
            .executor(snapshotDeleteCommand)
            .build()
        );

        snapshotSubCommands.put(Arrays.asList("edit", "e", "view"), CommandSpec.builder()
            .description(Text.of("Edit/view snapshot for user from DB. If no date is provided, latest snapshot is selected."))
            .permission(PluginPermissions.SNAPSHOT_VIEW_BASE)
            .arguments(
                GenericArguments.onlyOne(GenericArguments.user(Text.of("user"))),
                GenericArguments.optional(GenericArguments.string(Text.of("date")))
            )
            .executor(snapshotViewCommand)
            .build()
        );

        snapshotSubCommands.put(Collections.singletonList("help"), CommandSpec.builder()
            .description(Text.of("Shows this help page."))
            .permission(PluginPermissions.SNAPSHOT_BASE)
            .executor(snapshotHelpCommand)
            .build()
        );

        snapshotSubCommands.put(Arrays.asList("info", "i"), CommandSpec.builder()
            .description(Text.of("More info for snapshot for user from DB. If no date is provided, latest snapshot is selected."))
            .permission(PluginPermissions.SNAPSHOT_BASE)
            .arguments(
                GenericArguments.onlyOne(GenericArguments.user(Text.of("user"))),
                GenericArguments.optional(GenericArguments.string(Text.of("date")))
            )
            .executor(snapshotInfoCommand)
            .build()
        );

        snapshotSubCommands.put(Arrays.asList("list", "l"), CommandSpec.builder()
            .description(Text.of("Lists available snapshots for user."))
            .permission(PluginPermissions.SNAPSHOT_BASE)
            .arguments(
                GenericArguments.onlyOne(GenericArguments.user(Text.of("user")))
            )
            .executor(snapshotListCommand)
            .build()
        );

        snapshotSubCommands.put(Arrays.asList("restore", "r", "download", "down"), CommandSpec.builder()
            .description(Text.of("Manually restores snapshot from DB. If no date is selected, latest snapshot is restored."))
            .permission(PluginPermissions.SNAPSHOT_RESTORE)
            .arguments(
                GenericArguments.onlyOne(GenericArguments.user(Text.of("user"))),
                GenericArguments.optional(GenericArguments.string(Text.of("date")))
            )
            .executor(snapshotRestoreCommand)
            .build()
        );

        subCommands.put(Arrays.asList("snapshot", "snap", "s"), CommandSpec.builder()
            .description(Text.of("Snapshot base command."))
            .permission(PluginPermissions.SNAPSHOT_BASE)
            .executor(snapshotHelpCommand)
            .children(snapshotSubCommands)
            .build());

        Map<String, String> optimizeStartChoices = new HashMap<>();

        optimizeStartChoices.put("all", "all");
        optimizeStartChoices.put("user", "user");

        optimizeSubCommands.put(Arrays.asList("start", "s"), CommandSpec.builder()
            .description(Text.of("Starts manual optimization, deletes old snapshots."))
            .permission(PluginPermissions.MANUAL_OPTIMIZATION_BASE)
            .arguments(
                GenericArguments.choices(Text.of("mode"), optimizeStartChoices),
                GenericArguments.optional(GenericArguments.user(Text.of("user")))
            )
            .executor(optimizeStartCommand)
            .build());

        optimizeSubCommands.put(Arrays.asList("info", "i"), CommandSpec.builder()
            .description(Text.of("Gets info on current manual optimization."))
            .permission(PluginPermissions.MANUAL_OPTIMIZATION_BASE)
            .executor(optimizeInfoCommand)
            .build());

        optimizeSubCommands.put(Collections.singletonList("stop"), CommandSpec.builder()
            .description(Text.of("Stops current manual optimization."))
            .permission(PluginPermissions.MANUAL_OPTIMIZATION_BASE)
            .executor(optimizeStopCommand)
            .build());

        optimizeSubCommands.put(Collections.singletonList("help"), CommandSpec.builder()
            .description(Text.of("Shows this help page."))
            .permission(PluginPermissions.MANUAL_OPTIMIZATION_BASE)
            .executor(optimizeHelpCommand)
            .build());

        subCommands.put(Arrays.asList("optimize", "opt", "o"), CommandSpec.builder()
            .description(Text.of("Optimize base command. (To delete old snapshots)"))
            .permission(PluginPermissions.MANUAL_OPTIMIZATION_BASE)
            .executor(optimizeHelpCommand)
            .children(optimizeSubCommands)
            .build());

        Map<String, String> lockChoices = new HashMap<>();

        lockChoices.put("on", "on");
        lockChoices.put("off", "off");

        subCommands.put(Arrays.asList("lock", "l"), CommandSpec.builder()
            .description(Text.of("Lock / Unlock commands."))
            .permission(PluginPermissions.LOCK_COMMAND)
            .arguments(
                GenericArguments.optional(GenericArguments.choices(Text.of("value"), lockChoices))
            )
            .executor(syncLockCommand)
            .build());

        subCommands.put(Collections.singletonList("reload"), CommandSpec.builder()
            .description(Text.of("Reloads plugin."))
            .permission(PluginPermissions.RELOAD_COMMAND)
            .executor(syncReloadCommand)
            .build());

        subCommands.put(Arrays.asList("upload", "up"), CommandSpec.builder()
            .description(Text.of("Uploads all players on server."))
            .permission(PluginPermissions.SNAPSHOT_CREATE)
            .executor(uploadStartCommand)
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
            .executor(syncHelpCommand)
            .children(subCommands)
            .build();

        //Register commands
        Sponge.getCommandManager().register(plugin, mainCommand, "sync", "msdatasync", "datasync", "synchronize");
        SyncCommandManager.subCommands = subCommands;
        SyncCommandManager.snapshotSubCommands = snapshotSubCommands;
        SyncCommandManager.optimizeSubCommands = optimizeSubCommands;
    }
}
