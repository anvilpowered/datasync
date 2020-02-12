package org.anvilpowered.datasync.sponge.command.snapshot;

import com.google.inject.Inject;
import org.anvilpowered.anvil.api.data.registry.Registry;
import org.anvilpowered.datasync.api.data.key.DataSyncKeys;
import org.anvilpowered.datasync.common.command.CommonSyncCommandNode;
import org.anvilpowered.datasync.common.command.snapshot.CommonSnapshotCommandNode;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpongeSnapshotCommandNode
    extends CommonSnapshotCommandNode<CommandExecutor, CommandSource> {

    @Inject
    private SpongeSnapshotCreateCommand snapshotCreateCommand;

    @Inject
    private SpongeSnapshotDeleteCommand snapshotDeleteCommand;

    @Inject
    private SpongeSnapshotInfoCommand snapshotInfoCommand;

    @Inject
    private SpongeSnapshotListCommand snapshotListCommand;

    @Inject
    private SpongeSnapshotRestoreCommand snapshotRestoreCommand;

    @Inject
    private SpongeSnapshotViewCommand snapshotViewCommand;

    private CommandSpec root;

    @Inject
    public SpongeSnapshotCommandNode(Registry registry) {
        super(registry);
    }

    protected void loadCommands() {

        Map<List<String>, CommandCallable> subCommands = new HashMap<>();

        subCommands.put(CREATE_ALIAS, CommandSpec.builder()
            .description(Text.of(CREATE_DESCRIPTION))
            .permission(registry.getOrDefault(DataSyncKeys.SNAPSHOT_CREATE_PERMISSION))
            .arguments(
                GenericArguments.onlyOne(GenericArguments.user(Text.of("user")))
            )
            .executor(snapshotCreateCommand)
            .build()
        );

        subCommands.put(DELETE_ALIAS, CommandSpec.builder()
            .description(Text.of(DELETE_DESCRIPTION))
            .permission(registry.getOrDefault(DataSyncKeys.SNAPSHOT_DELETE_PERMISSION))
            .arguments(
                GenericArguments.onlyOne(GenericArguments.user(Text.of("user"))),
                GenericArguments.string(Text.of("snapshot"))
            )
            .executor(snapshotDeleteCommand)
            .build()
        );

        subCommands.put(VIEW_ALIAS, CommandSpec.builder()
            .description(Text.of(VIEW_DESCRIPTION))
            .permission(registry.getOrDefault(DataSyncKeys.SNAPSHOT_VIEW_BASE_PERMISSION))
            .arguments(
                GenericArguments.onlyOne(GenericArguments.user(Text.of("user"))),
                GenericArguments.optional(GenericArguments.string(Text.of("snapshot")))
            )
            .executor(snapshotViewCommand)
            .build()
        );


        subCommands.put(INFO_ALIAS, CommandSpec.builder()
            .description(Text.of(INFO_DESCRIPTION))
            .permission(registry.getOrDefault(DataSyncKeys.SNAPSHOT_BASE_PERMISSION))
            .arguments(
                GenericArguments.onlyOne(GenericArguments.user(Text.of("user"))),
                GenericArguments.optional(GenericArguments.string(Text.of("snapshot")))
            )
            .executor(snapshotInfoCommand)
            .build()
        );

        subCommands.put(RESTORE_ALIAS, CommandSpec.builder()
            .description(Text.of(RESTORE_DESCRIPTION))
            .permission(registry.getOrDefault(DataSyncKeys.SNAPSHOT_RESTORE_PERMISSION))
            .arguments(
                GenericArguments.onlyOne(GenericArguments.user(Text.of("user"))),
                GenericArguments.optional(GenericArguments.string(Text.of("snapshot")))
            )
            .executor(snapshotRestoreCommand)
            .build()
        );

        subCommands.put(LIST_ALIAS, CommandSpec.builder()
            .description(Text.of(LIST_DESCRIPTION))
            .permission(registry.getOrDefault(DataSyncKeys.SNAPSHOT_BASE_PERMISSION))
            .arguments(
                GenericArguments.onlyOne(GenericArguments.user(Text.of("user")))
            )
            .executor(snapshotListCommand)
            .build()
        );

        subCommands.put(HELP_ALIAS, CommandSpec.builder()
            .description(Text.of(HELP_DESCRIPTION))
            .permission(registry.getOrDefault(DataSyncKeys.SNAPSHOT_BASE_PERMISSION))
            .executor(commandService.generateHelpCommand(this))
            .build()
        );

        root = CommandSpec.builder()
            .description(Text.of(ROOT_DESCRIPTION))
            .permission(DataSyncKeys.SNAPSHOT_BASE_PERMISSION.getFallbackValue())
            .executor(commandService.generateRootCommand(HELP_COMMAND))
            .children(subCommands)
            .build();
    }

    public CommandSpec getRoot() {
        if (root == null) {
            loadCommands();
        }
        return root;
    }

    @Override
    public String getName() {
        return "snapshot";
    }

    @Override
    public String[] getPath() {
        return CommonSyncCommandNode.SYNC_PATH;
    }
}
