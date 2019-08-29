package rocks.milspecsg.msdatasync.commands;

import com.google.inject.Inject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import rocks.milspecsg.msdatasync.PluginPermissions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SyncCommandManager implements CommandManager {

    @Inject
    SyncHelpCommand syncHelpCommand;

    @Inject
    SyncStartCommand syncStartCommand;

    public static Map<List<String>, CommandSpec> subCommands = new HashMap<>();

    @Override
    public void register(Object plugin) {
        Map<List<String>, CommandSpec> subCommands = new HashMap<>();

        subCommands.put(Arrays.asList("start", "s"), CommandSpec.builder()
            .description(Text.of("Upload current player inventory to DB. If no player selected, all online are uploaded"))
            .permission(PluginPermissions.START_COMMAND)
            .arguments(
                GenericArguments.optional(GenericArguments.player(Text.of("player")))
            )
            .executor(syncStartCommand)
            .build());


        //Build all commands
        CommandSpec mainCommand = CommandSpec.builder()
            .description(Text.of("Displays all available party subcommands"))
            .executor(syncHelpCommand)
            .children(subCommands)
            .build();

        //Register commands
        Sponge.getCommandManager().register(plugin, mainCommand, "sync", "msdatasync", "datasync", "synchronize");
        SyncCommandManager.subCommands = subCommands;
    }
}
