package rocks.milspecsg.msdatasync.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import rocks.milspecsg.msdatasync.MSDataSyncPluginInfo;
import rocks.milspecsg.msdatasync.misc.CommandUtils;

import javax.inject.Inject;
import java.net.MalformedURLException;
import java.net.URL;

public class SyncInfoCommand implements CommandExecutor {

    @Inject
    CommandUtils commandUtils;
    @Override
    public CommandResult execute(CommandSource source, CommandContext context) throws CommandException {
        commandUtils.createInfoPage(source);
        return CommandResult.success();
    }
}
