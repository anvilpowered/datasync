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

package rocks.milspecsg.msdatasync.sponge.commands.snapshot;

import com.google.inject.Inject;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import rocks.milspecsg.msdatasync.sponge.plugin.MSDataSync;
import rocks.milspecsg.msdatasync.api.serializer.user.UserSerializerManager;
import rocks.milspecsg.msdatasync.sponge.commands.SyncLockCommand;
import rocks.milspecsg.msdatasync.api.model.snapshot.Snapshot;
import rocks.milspecsg.msrepository.api.util.PluginInfo;

import java.util.Optional;

public class SnapshotRestoreCommand implements CommandExecutor {

    @Inject
    private UserSerializerManager<Snapshot<?>, User, Text> userSerializer;

    @Inject
    private PluginInfo<Text> pluginInfo;

    @Override
    public CommandResult execute(CommandSource source, CommandContext context) throws CommandException {
        SyncLockCommand.assertUnlocked(source);
        Optional<User> optionalUser = context.getOne(Text.of("user"));
        if (!optionalUser.isPresent()) {
            throw new CommandException(Text.of(pluginInfo.getPrefix(), "User is required"));
        }
        userSerializer.restore(optionalUser.get().getUniqueId(), context.getOne(Text.of("snapshot")), MSDataSync.plugin)
            .thenAcceptAsync(source::sendMessage);
        return CommandResult.success();
    }
}
