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

package org.anvilpowered.datasync.sponge.commands;

import com.google.inject.Inject;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.anvilpowered.datasync.sponge.misc.CommandUtils;

public class SyncHelpCommand implements CommandExecutor {

    @Inject
    private CommandUtils commandUtils;

    @Override
    public CommandResult execute(CommandSource source, CommandContext context) {
        if (commandUtils.hasPermissionForSubCommands(source)) {
            commandUtils.createHelpPage(source, SyncCommandManager.subCommands, "");
        } else {
            commandUtils.createBasicInfoPage(source, false);
        }
        return CommandResult.success();
    }
}
