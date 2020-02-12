/*
 *   DataSync - AnvilPowered
 *   Copyright (C) 2020 Cableguy20
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

package org.anvilpowered.datasync.sponge.command;

import com.google.inject.Inject;
import org.anvilpowered.datasync.api.serializer.user.UserSerializerManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

public class SpongeSyncUploadCommand implements CommandExecutor {

    @Inject
    private UserSerializerManager<User, Text> userSerializer;

    @Override
    public CommandResult execute(CommandSource source, CommandContext context) throws CommandException {
        SpongeSyncLockCommand.assertUnlocked(source);
        // serialize everyone on the server
        userSerializer.serialize(Sponge.getServer().getOnlinePlayers()).thenAcceptAsync(source::sendMessage);
        return CommandResult.success();
    }
}
