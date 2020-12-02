package org.anvilpowered.datasync.spigot.command;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.anvilpowered.anvil.api.registry.Registry;
import org.anvilpowered.datasync.common.command.CommonSyncCommandNode;
import org.anvilpowered.datasync.spigot.DataSyncSpigot;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Singleton
public class SpigotSyncCommandNode
    extends CommonSyncCommandNode<CommandExecutor, CommandSender> {

    @Inject
    private SpigotSyncTestCommand spigotSyncTestCommand;

    @Inject
    private DataSyncSpigot plugin;

    @Inject
    public SpigotSyncCommandNode(Registry registry) {
        super(registry);
    }

    @Override
    protected void loadCommands() {
        Map<List<String>, CommandExecutor> subCommands = new HashMap<>();
        subCommands.put(TEST_ALIAS, spigotSyncTestCommand);
        
        Objects.requireNonNull(plugin.getCommand("datasync")).setExecutor(
            commandService.generateRoutingCommand(
                commandService.generateRootCommand(HELP_COMMAND), subCommands, false
            )
        );
    }
}
