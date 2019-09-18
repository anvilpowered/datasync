package rocks.milspecsg.msdatasync.commands.optimize;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import rocks.milspecsg.msdatasync.commands.SyncCommandManager;
import rocks.milspecsg.msdatasync.misc.CommandUtils;

import javax.inject.Inject;

public class OptimizeHelpCommand implements CommandExecutor {

    @Inject
    CommandUtils commandUtils;

    @Override
    public CommandResult execute(CommandSource source, CommandContext context) throws CommandException {
        commandUtils.createHelpPage(source, SyncCommandManager.optimizeSubCommands, "optimize");
        return CommandResult.success();
    }

}
