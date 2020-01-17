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

package rocks.milspecsg.msdatasync.sponge.commands.optimize;

import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import rocks.milspecsg.msdatasync.api.snapshotoptimization.SnapshotOptimizationManager;

import javax.inject.Inject;

public class OptimizeInfoCommand implements CommandExecutor {

    @Inject
    private SnapshotOptimizationManager<User, Text, CommandSource> snapshotOptimizationManager;

    @Override
    public CommandResult execute(CommandSource source, CommandContext context) {
        snapshotOptimizationManager.info().thenAcceptAsync(source::sendMessage);
        return CommandResult.success();
    }
}
