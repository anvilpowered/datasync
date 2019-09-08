package rocks.milspecsg.msdatasync.commands.optimize;

import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import rocks.milspecsg.msdatasync.MSDataSyncPluginInfo;
import rocks.milspecsg.msdatasync.misc.SnapshotOptimizationService;

import javax.inject.Inject;

public class OptimizeInfoCommand implements CommandExecutor {

    @Inject
    SnapshotOptimizationService snapshotOptimizationService;

    @Override
    public CommandResult execute(CommandSource source, CommandContext context) {

        if (snapshotOptimizationService.isOptimizationTaskRunning()) {
            int completed = snapshotOptimizationService.getCompleted();
            int total = snapshotOptimizationService.getTotal();
            source.sendMessage(Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.YELLOW, "Optimization task: Completed ", completed, " out of ", total, " ", TextColors.GOLD, (completed * 100) / total, "%"));
        } else {
            source.sendMessage(Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.YELLOW, "There is currently no optimization task running"));
        }

        return CommandResult.success();

    }
}
