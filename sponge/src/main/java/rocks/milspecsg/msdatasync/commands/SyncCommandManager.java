package rocks.milspecsg.msdatasync.commands;

import com.google.inject.Inject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import rocks.milspecsg.msdatasync.PluginPermissions;

import java.util.*;

public class SyncCommandManager implements CommandManager {

    @Inject
    SyncHelpCommand syncHelpCommand;

    @Inject
    UploadStartCommand uploadStartCommand;

    @Inject
    DownloadStartCommand downloadStartCommand;

    @Inject
    SyncLockCommand syncLockCommand;

    @Inject
    SyncReloadCommand syncReloadCommand;

    public static Map<List<String>, CommandSpec> subCommands = new HashMap<>();

    @Override
    public void register(Object plugin) {
        Map<List<String>, CommandSpec> subCommands = new HashMap<>();

        subCommands.put(Arrays.asList("upload", "up", "u"), CommandSpec.builder()
            .description(Text.of("Upload current player inventory to DB. If no player selected, all online are uploaded"))
            .permission(PluginPermissions.START_COMMAND)
            .arguments(
                GenericArguments.optional(GenericArguments.player(Text.of("player")))
            )
            .executor(uploadStartCommand)
            .build());

        subCommands.put(Arrays.asList("download", "down", "d"), CommandSpec.builder()
            .description(Text.of("Download current player inventory from DB. If no player selected, all online are downloaded"))
            .permission(PluginPermissions.START_COMMAND)
            .arguments(
                GenericArguments.optional(GenericArguments.player(Text.of("player")))
            )
            .executor(downloadStartCommand)
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
