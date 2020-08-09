/*
 *   DataSync - AnvilPowered
 *   Copyright (C) 2020 Cableguy20
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.datasync.sponge.command;

import com.google.inject.Inject;
import org.anvilpowered.anvil.api.plugin.PluginInfo;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpongeSyncLockCommand implements CommandExecutor {

    @Inject
    PluginInfo<Text> pluginInfo;

    private static final List<UUID> unlockedPlayers = new ArrayList<>();

    public static void assertUnlocked(CommandSource source) throws CommandException {
        if (source instanceof Player && !unlockedPlayers.contains(((Player) source).getUniqueId())) {
            throw new CommandException(Text.of("You must first unlock this command with /sync lock off"));
        }
    }

    public static void lockPlayer(CommandSource source) {
        if (source instanceof Player) {
            unlockedPlayers.remove(((Player) source).getUniqueId());
        }
    }

    @Override
    public CommandResult execute(CommandSource source, CommandContext context) {
        if (source instanceof Player) {
            Player player = (Player) source;
            Optional<String> optionalValue = context.getOne(Text.of("value"));

            int index = unlockedPlayers.indexOf((player.getUniqueId()));

            if (!optionalValue.isPresent()) {
                source.sendMessage(Text.of(pluginInfo.getPrefix(), TextColors.YELLOW, "Currently ", index >= 0 ? "unlocked" : "locked"));
                return CommandResult.success();
            }

            String value = optionalValue.get();

            switch (value) {
                case "on":
                    if (index >= 0) {
                        unlockedPlayers.remove(index);
                        source.sendMessage(Text.of(pluginInfo.getPrefix(), TextColors.YELLOW, "Lock enabled"));
                    } else {
                        source.sendMessage(Text.of(pluginInfo.getPrefix(), TextColors.YELLOW, "Lock already enabled"));
                    }
                    break;
                case "off":
                    if (index < 0) {
                        unlockedPlayers.add(player.getUniqueId());
                        source.sendMessage(Text.of(pluginInfo.getPrefix(), TextColors.YELLOW, "Lock disabled", TextColors.RED, " (be careful)"));
                    } else {
                        source.sendMessage(Text.of(pluginInfo.getPrefix(), TextColors.YELLOW, "Lock already disabled"));
                    }
                    break;
                default:
                    source.sendMessage(Text.of(pluginInfo.getPrefix(), TextColors.RED, "Unrecognized option: \"", value, "\". Lock is ", TextColors.YELLOW, index >= 0 ? "disabled" : "enabled"));
                    break;
            }


        } else {
            // console is always unlocked
            source.sendMessage(Text.of(pluginInfo.getPrefix(), TextColors.RED, "Console is always unlocked"));
        }

        return CommandResult.success();
    }
}
