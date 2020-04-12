package org.anvilpowered.datasync.sponge.command.optimize;

import com.google.inject.Inject;
import org.anvilpowered.anvil.api.data.registry.Registry;
import org.anvilpowered.datasync.common.command.optimize.CommonOptimizeCommandNode;
import org.anvilpowered.datasync.common.data.key.DataSyncKeys;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpongeOptimizeCommandNode
    extends CommonOptimizeCommandNode<CommandExecutor, CommandSource> {

    CommandSpec root;

    @Inject
    private OptimizeInfoCommand optimizeInfoCommand;

    @Inject
    private OptimizeStartCommand optimizeStartCommand;

    @Inject
    private OptimizeStopCommand optimizeStopCommand;

    @Inject
    public SpongeOptimizeCommandNode(Registry registry) {
        super(registry);
    }

    @Override
    protected void loadCommands() {

        Map<List<String>, CommandCallable> subCommands = new HashMap<>();

        Map<String, String> optimizeStartChoices = new HashMap<>();

        optimizeStartChoices.put("all", "all");
        optimizeStartChoices.put("user", "user");

        subCommands.put(START_ALIAS, CommandSpec.builder()
            .description(Text.of(START_DESCRIPTION))
            .permission(registry.getOrDefault(DataSyncKeys.MANUAL_OPTIMIZATION_BASE_PERMISSION))
            .arguments(
                GenericArguments.choices(Text.of("mode"), optimizeStartChoices),
                GenericArguments.optional(GenericArguments.user(Text.of("user")))
            )
            .executor(optimizeStartCommand)
            .build());

        subCommands.put(INFO_ALIAS, CommandSpec.builder()
            .description(Text.of(INFO_DESCRIPTION))
            .permission(registry.getOrDefault(DataSyncKeys.MANUAL_OPTIMIZATION_BASE_PERMISSION))
            .executor(optimizeInfoCommand)
            .build());

        subCommands.put(STOP_ALIAS, CommandSpec.builder()
            .description(Text.of(STOP_DESCRIPTION))
            .permission(registry.getOrDefault(DataSyncKeys.MANUAL_OPTIMIZATION_BASE_PERMISSION))
            .executor(optimizeStopCommand)
            .build());

        subCommands.put(HELP_ALIAS, CommandSpec.builder()
            .description(Text.of(HELP_DESCRIPTION))
            .permission(registry.getOrDefault(DataSyncKeys.MANUAL_OPTIMIZATION_BASE_PERMISSION))
            .executor(commandService.generateHelpCommand(this))
            .build());

        root = CommandSpec.builder()
            .description(Text.of(ROOT_DESCRIPTION))
            .permission(registry.getOrDefault(DataSyncKeys.MANUAL_OPTIMIZATION_BASE_PERMISSION))
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
        return "optimize";
    }
}
