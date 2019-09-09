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
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class OptimizeInfoCommand implements CommandExecutor {

    @Inject
    SnapshotOptimizationService snapshotOptimizationService;

    private static NumberFormat formatter = new DecimalFormat("#0.00");

    @Override
    public CommandResult execute(CommandSource source, CommandContext context) {

        int deleted = snapshotOptimizationService.getSnapshotsDeleted();
        int completed = snapshotOptimizationService.getMembersCompleted();
        int total = snapshotOptimizationService.getTotalMembers();

        if (snapshotOptimizationService.isOptimizationTaskRunning()) {

            source.sendMessage(
                Text.of(
                    MSDataSyncPluginInfo.pluginPrefix, TextColors.YELLOW,
                    "Optimization task:\n",
                    TextColors.GRAY, "Snapshots deleted: ", TextColors.YELLOW, deleted, "\n",
                    TextColors.GRAY, "Members processed: ", TextColors.YELLOW, completed, "/", total, "\n",
                    TextColors.GRAY, "Progress: ", TextColors.YELLOW,  formatter.format((double) completed * 100d / (double) total), "%"
                )
            );
        } else {
            source.sendMessage(Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.YELLOW, "There is currently no optimization task running"));
        }

        return CommandResult.success();

    }
}
