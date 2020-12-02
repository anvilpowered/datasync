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

package org.anvilpowered.datasync.spigot.command.snapshot;

import com.google.inject.Inject;
import net.md_5.bungee.api.chat.TextComponent;
import org.anvilpowered.anvil.api.util.TextService;
import org.anvilpowered.datasync.api.registry.DataSyncKeys;
import org.anvilpowered.datasync.api.serializer.user.UserSerializerManager;
import org.anvilpowered.datasync.spigot.command.SpigotSyncLockCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class SpigotSnapshotCreateCommand implements CommandExecutor {

    @Inject
    private TextService<TextComponent, CommandSender> textService;

    @Inject
    private UserSerializerManager<Player, TextComponent> userSerializer;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.hasPermission(DataSyncKeys.SNAPSHOT_CREATE_PERMISSION.getFallbackValue())) {
            textService.builder()
            .appendPrefix()
            .red().append("Insufficient Permissions!")
            .sendTo(sender);
            return false;
        }

        if (!SpigotSyncLockCommand.assertUnlocked(sender)) {
            return false;
        }

        if (args.length < 1) {
            textService.builder()
                .appendPrefix()
                .red().append("User is required")
                .sendTo(sender);
            return false;
        }

        Optional<Player> optionalPlayer = Optional.ofNullable(Bukkit.getPlayer(args[0]));
        if (!optionalPlayer.isPresent()) {
            textService.builder()
            .appendPrefix()
            .red().append("Invalid player!")
            .sendTo(sender);
            return false;
        }

        userSerializer.serialize(optionalPlayer.get()).thenAcceptAsync(msg -> textService.send(msg, sender));
        return true;
    }
}
