package rocks.milspecsg.msdatasync.commands.optimize;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import rocks.milspecsg.msdatasync.MSDataSyncPluginInfo;
import rocks.milspecsg.msdatasync.api.snapshot.SnapshotOptimizationService;
import rocks.milspecsg.msdatasync.commands.SyncLockCommand;

import javax.inject.Inject;

public class OptimizeStopCommand implements CommandExecutor {

    @Inject
    SnapshotOptimizationService<User, CommandSource> snapshotOptimizationService;

    @Override
    public CommandResult execute(CommandSource source, CommandContext context) throws CommandException {

        SyncLockCommand.assertUnlocked(source);

        if (snapshotOptimizationService.stopOptimizationTask()) {
            source.sendMessage(Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.YELLOW, "Successfully stopped optimization task"));
        } else {
            source.sendMessage(Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.YELLOW, "There is currently no optimization task running"));
        }

        return CommandResult.success();
    }
}
