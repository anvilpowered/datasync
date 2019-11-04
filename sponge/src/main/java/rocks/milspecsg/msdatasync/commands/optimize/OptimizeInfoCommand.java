/*
 *     MSDataSync - MilSpecSG
 *     Copyright (C) 2019 Cableguy20
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package rocks.milspecsg.msdatasync.commands.optimize;

import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import rocks.milspecsg.msdatasync.MSDataSyncPluginInfo;
import rocks.milspecsg.msdatasync.api.snapshotoptimization.SnapshotOptimizationManager;
import rocks.milspecsg.msdatasync.api.snapshotoptimization.component.SnapshotOptimizationService;

import javax.inject.Inject;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class OptimizeInfoCommand implements CommandExecutor {

    @Inject
    private SnapshotOptimizationManager<User, CommandSource> snapshotOptimizationManager;

    private static NumberFormat formatter = new DecimalFormat("#0.00");

    @Override
    public CommandResult execute(CommandSource source, CommandContext context) {

        int uploaded = snapshotOptimizationManager.getPrimaryComponent().getSnapshotsUploaded();
        int deleted = snapshotOptimizationManager.getPrimaryComponent().getSnapshotsDeleted();
        int completed = snapshotOptimizationManager.getPrimaryComponent().getMembersCompleted();
        int total = snapshotOptimizationManager.getPrimaryComponent().getTotalMembers();

        if (snapshotOptimizationManager.getPrimaryComponent().isOptimizationTaskRunning()) {
            source.sendMessage(
                Text.of(
                    MSDataSyncPluginInfo.pluginPrefix, TextColors.YELLOW,
                    "Optimization task:\n",
                    TextColors.GRAY, "Snapshots uploaded: ", TextColors.YELLOW, uploaded, "\n",
                    TextColors.GRAY, "Snapshots deleted: ", TextColors.YELLOW, deleted, "\n",
                    TextColors.GRAY, "Members processed: ", TextColors.YELLOW, completed, "/", total, "\n",
                    TextColors.GRAY, "Progress: ", TextColors.YELLOW, formatter.format(completed * 100d / total), "%"
                )
            );
        } else {
            source.sendMessage(Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.YELLOW, "There is currently no optimization task running"));
        }

        return CommandResult.success();

    }
}
