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

package org.anvilpowered.datasync.spigot.command;

import com.google.inject.Inject;
import net.md_5.bungee.api.chat.TextComponent;
import org.anvilpowered.anvil.api.util.TextService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpigotSyncLockCommand implements CommandExecutor {

    @Inject
    private TextService<TextComponent, CommandSender> textService;

    private static final List<UUID> unlockedPlayers = new ArrayList<>();

    public static boolean assertUnlocked(CommandSender sender) {
        if (sender instanceof Player && !unlockedPlayers.contains(((Player) sender).getUniqueId())) {
            sender.sendMessage("You must first unlock this command with /sync lock off");
            return false;
        }
        return true;
    }

    public static void lockPlayer(CommandSender sender) {
        if (sender instanceof Player) {
            unlockedPlayers.remove(((Player) sender).getUniqueId());
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            textService.builder()
                .appendPrefix()
                .red().append("Console is always unlocked")
                .sendTo(sender);
            return true;
        }

        Player player = (Player) sender;
        int index = unlockedPlayers.indexOf(player.getUniqueId());
        String status = index >= 0 ? "unlocked" : "locked";
        System.out.println(args.length);
        if (args.length < 1) {
            textService.builder()
                .appendPrefix()
                .yellow().append("Currently " + status)
                .sendTo(sender);
            return true;
        }
        switch (args[0]) {
            case "on":
                if (index >= 0) {
                    unlockedPlayers.remove(index);
                    textService.builder()
                        .appendPrefix()
                        .yellow().append("Lock enabled")
                        .sendTo(sender);
                } else {
                    textService.builder()
                        .appendPrefix()
                        .yellow().append("Lock already enabled")
                        .sendTo(sender);
                }
                break;
            case "off":
                if (index < 0) {
                    unlockedPlayers.add(player.getUniqueId());
                    textService.builder()
                        .appendPrefix()
                        .yellow().append("Lock disabled")
                        .red().append(" (be careful)")
                        .sendTo(sender);
                } else {
                    textService.builder()
                        .appendPrefix()
                        .yellow().append("Lock already disabled")
                        .sendTo(sender);
                }
                break;
            default:
                textService.builder()
                    .appendPrefix()
                    .red().append("Unrecognized option: \"" + args[0] + "\". Lock is ")
                    .yellow().append(status)
                    .sendTo(sender);
        }
        return true;
    }
}
