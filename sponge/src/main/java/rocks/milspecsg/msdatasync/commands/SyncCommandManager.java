package rocks.milspecsg.msdatasync.commands;

import com.google.inject.Inject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import rocks.milspecsg.msdatasync.PluginPermissions;
import rocks.milspecsg.msdatasync.commands.snapshot.*;

import java.util.*;

public class SyncCommandManager implements CommandManager {

    @Inject
    SyncHelpCommand syncHelpCommand;

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
    SnapshotEditCommand snapshotEditCommand;

    @Inject
    SnapshotInfoCommand snapshotInfoCommand;

    @Inject
    SnapshotListCommand snapshotListCommand;

    @Inject
    SnapshotRestoreCommand snapshotRestoreCommand;

    public static Map<List<String>, CommandSpec> subCommands = new HashMap<>();
    public static Map<List<String>, CommandSpec> snapshotSubCommands = new HashMap<>();

    @Override
    public void register(Object plugin) {
        Map<List<String>, CommandSpec> subCommands = new HashMap<>();
        Map<List<String>, CommandSpec> snapshotSubCommands = new HashMap<>();

        snapshotSubCommands.put(Arrays.asList("create", "c", "upload", "up"), CommandSpec.builder()
            .description(Text.of("Manually create new snapshot for user and upload to DB. If no user selected, all online are uploaded"))
            .permission(PluginPermissions.MANUAL_SYNC_COMMAND)
            .arguments(
                GenericArguments.onlyOne(GenericArguments.user(Text.of("user")))
            )
            .executor(snapshotCreateCommand)
            .build()
        );

        snapshotSubCommands.put(Collections.singletonList("delete"), CommandSpec.builder()
            .description(Text.of("Delete snapshot for user"))
            .permission(PluginPermissions.EDIT_SNAPSHOTS)
            .arguments(
                GenericArguments.onlyOne(GenericArguments.user(Text.of("user"))),
                GenericArguments.string(Text.of("date"))
            )
            .executor(snapshotDeleteCommand)
            .build()
        );

        snapshotSubCommands.put(Arrays.asList("edit", "e", "invsee"), CommandSpec.builder()
            .description(Text.of("Edit snapshot for user from DB. If no date is selected, latest snapshot is selected"))
            .permission(PluginPermissions.EDIT_SNAPSHOTS)
            .arguments(
                GenericArguments.onlyOne(GenericArguments.user(Text.of("user"))),
                GenericArguments.optional(GenericArguments.string(Text.of("date")))
            )
            .executor(snapshotEditCommand)
            .build()
        );

        snapshotSubCommands.put(Collections.singletonList("help"), CommandSpec.builder()
            .description(Text.of("Shows this help page"))
            .permission(PluginPermissions.MANUAL_SYNC_COMMAND)
            .executor(snapshotHelpCommand)
            .build()
        );

        snapshotSubCommands.put(Arrays.asList("info", "i"), CommandSpec.builder()
            .description(Text.of("Show more snapshot info"))
            .permission(PluginPermissions.MANUAL_SYNC_COMMAND)
            .arguments(
                GenericArguments.onlyOne(GenericArguments.user(Text.of("user"))),
                GenericArguments.optional(GenericArguments.string(Text.of("date")))
            )
            .executor(snapshotInfoCommand)
            .build()
        );

        snapshotSubCommands.put(Arrays.asList("list", "l"), CommandSpec.builder()
            .description(Text.of("List available snapshots for user"))
            .permission(PluginPermissions.MANUAL_SYNC_COMMAND)
            .arguments(
                GenericArguments.onlyOne(GenericArguments.user(Text.of("user")))
            )
            .executor(snapshotListCommand)
            .build()
        );

        snapshotSubCommands.put(Arrays.asList("restore", "r", "download", "down"), CommandSpec.builder()
            .description(Text.of("Manually restore snapshot for user from DB. If no date is selected, latest snapshot is restored"))
            .permission(PluginPermissions.MANUAL_SYNC_COMMAND)
            .arguments(
                GenericArguments.onlyOne(GenericArguments.user(Text.of("user"))),
                GenericArguments.optional(GenericArguments.string(Text.of("date")))
            )
            .executor(snapshotRestoreCommand)
            .build()
        );

        subCommands.put(Arrays.asList("snapshot", "snap", "s"), CommandSpec.builder()
            .description(Text.of("Snapshot base command"))
            .permission(PluginPermissions.MANUAL_SYNC_COMMAND)
            .executor(snapshotHelpCommand)
            .children(snapshotSubCommands)
            .build());

        Map<String, String> lockChoices = new HashMap<>();

        lockChoices.put("on", "on");
        lockChoices.put("off", "off");

        subCommands.put(Arrays.asList("lock", "l"), CommandSpec.builder()
            .description(Text.of("Lock / Unlock commands"))
            .permission(PluginPermissions.LOCK_COMMAND)
            .arguments(
                GenericArguments.optional(GenericArguments.choices(Text.of("value"), lockChoices))
            )
            .executor(syncLockCommand)
            .build());

        subCommands.put(Collections.singletonList("reload"), CommandSpec.builder()
            .description(Text.of("Reload config"))
            .permission(PluginPermissions.RELOAD_COMMAND)
            .executor(syncReloadCommand)
            .build());

        subCommands.put(Arrays.asList("upload", "up"), CommandSpec.builder()
            .description(Text.of("Upload all players on server"))
            .permission(PluginPermissions.MANUAL_SYNC_COMMAND)
            .executor(uploadStartCommand)
            .build());


        //Build all commands
        CommandSpec mainCommand = CommandSpec.builder()
            .description(Text.of("Displays all available sync subcommands"))
            .executor(syncHelpCommand)
            .children(subCommands)
            .build();

        //Register commands
        Sponge.getCommandManager().register(plugin, mainCommand, "sync", "msdatasync", "datasync", "synchronize");
        SyncCommandManager.subCommands = subCommands;
        SyncCommandManager.snapshotSubCommands = snapshotSubCommands;
    }
}
