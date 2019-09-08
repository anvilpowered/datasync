package rocks.milspecsg.msdatasync.commands.snapshot;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import rocks.milspecsg.msdatasync.commands.SyncCommandManager;
import rocks.milspecsg.msdatasync.misc.CommandUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SnapshotHelpCommand implements CommandExecutor {

    @Inject
    CommandUtils commandUtils;


    @Override
    public CommandResult execute(CommandSource source, CommandContext context) throws CommandException {
        commandUtils.createHelpPage(source, SyncCommandManager.snapshotSubCommands, "snapshot");
        return CommandResult.success();
    }
}
