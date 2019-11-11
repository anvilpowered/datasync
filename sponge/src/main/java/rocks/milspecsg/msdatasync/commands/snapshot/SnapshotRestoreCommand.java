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

package rocks.milspecsg.msdatasync.commands.snapshot;

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
import rocks.milspecsg.msdatasync.api.serializer.user.UserSerializerManager;
import rocks.milspecsg.msdatasync.api.misc.DateFormatService;
import rocks.milspecsg.msdatasync.commands.SyncLockCommand;
import rocks.milspecsg.msdatasync.misc.CommandUtils;
import rocks.milspecsg.msdatasync.model.core.snapshot.Snapshot;

import java.util.Optional;
import java.util.function.Consumer;

public class SnapshotRestoreCommand implements CommandExecutor {

    @Inject
    private UserSerializerManager<Snapshot<?>, User, Text> userSerializer;

    @Inject
    private CommandUtils commandUtils;

    @Inject
    DateFormatService dateFormatService;

    @Override
    public CommandResult execute(CommandSource source, CommandContext context) throws CommandException {

        SyncLockCommand.assertUnlocked(source);

        Optional<User> optionalUser = context.getOne(Text.of("user"));

        if (!optionalUser.isPresent()) {
            throw new CommandException(Text.of(MSDataSyncPluginInfo.pluginPrefix, "User is required"));
        }

        User targetUser = optionalUser.get();

        Consumer<Optional<Snapshot<?>>> afterFound = optionalSnapshot -> {
            if (!optionalSnapshot.isPresent()) {
                source.sendMessage(Text.of(MSDataSyncPluginInfo.pluginPrefix, "Could not find snapshot for " + targetUser.getName()));
                return;
            }
            Snapshot<?> snapshot = optionalSnapshot.get();
            source.sendMessage(Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.YELLOW, "Restoring snapshot " + dateFormatService.format(snapshot.getCreatedUtcDate()), " for user ", targetUser.getName()));
            userSerializer.getPrimaryComponent().deserialize(targetUser, MSDataSync.plugin, snapshot);
        };

        commandUtils.parseDateOrGetLatest(source, context, targetUser, afterFound);

        return CommandResult.success();
    }
}
