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

import com.google.inject.Inject;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import rocks.milspecsg.msdatasync.MSDataSync;
import rocks.milspecsg.msdatasync.MSDataSyncPluginInfo;
import rocks.milspecsg.msdatasync.PluginPermissions;
import rocks.milspecsg.msdatasync.api.snapshotoptimization.SnapshotOptimizationManager;
import rocks.milspecsg.msdatasync.api.snapshotoptimization.component.SnapshotOptimizationService;
import rocks.milspecsg.msdatasync.commands.SyncLockCommand;

import java.util.Collection;
import java.util.Optional;

public class OptimizeStartCommand implements CommandExecutor {

    @Inject
    private SnapshotOptimizationManager<User, CommandSource> snapshotOptimizationManager;

    @Override
    public CommandResult execute(CommandSource source, CommandContext context) throws CommandException {

        SyncLockCommand.assertUnlocked(source);

        Optional<String> optionalMode = context.getOne(Text.of("mode"));
        Collection<User> users = context.getAll(Text.of("user"));

        if (!optionalMode.isPresent()) {
            throw new CommandException(Text.of(MSDataSyncPluginInfo.pluginPrefix, "Mode is required"));
        }

        if (optionalMode.get().equals("all")) {
            if (!source.hasPermission(PluginPermissions.MANUAL_OPTIMIZATION_ALL)) {
                throw new CommandException(Text.of(MSDataSyncPluginInfo.pluginPrefix, "You do not have permission to start optimization task: all"));
            } else if (snapshotOptimizationManager.getPrimaryComponent().optimize(source, MSDataSync.plugin)) {
                source.sendMessage(Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.YELLOW, "Successfully started optimization task: all"));
            } else {
                throw new CommandException(Text.of(MSDataSyncPluginInfo.pluginPrefix, "Optimizer already running! Use /sync optimize info"));
            }
            snapshotOptimizationManager.getPrimaryComponent().optimize(source, MSDataSync.plugin);
        } else {
            if (users.isEmpty()) {
                throw new CommandException(Text.of(MSDataSyncPluginInfo.pluginPrefix, "No users were selected by your query"));
            } else if (snapshotOptimizationManager.getPrimaryComponent().optimize(users, source, "Manual", MSDataSync.plugin)) {
                source.sendMessage(Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.YELLOW, "Successfully started optimization task: user"));
            } else {
                throw new CommandException(Text.of(MSDataSyncPluginInfo.pluginPrefix, "Optimizer already running! Use /sync optimize info"));
            }
        }

        return CommandResult.success();
    }
}
