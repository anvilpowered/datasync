package rocks.milspecsg.msdatasync.commands;

import com.google.inject.Inject;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import rocks.milspecsg.msdatasync.PluginInfo;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;

public class SyncReloadCommand implements CommandExecutor {

    @Inject
    ConfigurationService configurationService;

    @Override
    public CommandResult execute(CommandSource source, CommandContext context) throws CommandException {
        configurationService.load();
        source.sendMessage(Text.of(PluginInfo.PluginPrefix, TextColors.GREEN, "Successfully reloaded!"));
        return CommandResult.success();
    }
}
