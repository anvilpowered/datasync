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

package org.anvilpowered.datasync.spigot.command.optimize;

import com.google.inject.Inject;
import net.md_5.bungee.api.chat.TextComponent;
import org.anvilpowered.anvil.api.util.TextService;
import org.anvilpowered.datasync.api.registry.DataSyncKeys;
import org.anvilpowered.datasync.api.snapshotoptimization.SnapshotOptimizationManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpigotOptimizeStartCommand implements CommandExecutor {

    @Inject
    private SnapshotOptimizationManager<Player, TextComponent, CommandSender> snapshotOptimizationManager;

    @Inject
    private TextService<TextComponent, CommandSender> textService;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.hasPermission(DataSyncKeys.MANUAL_OPTIMIZATION_BASE_PERMISSION.getFallbackValue())) {
            textService.builder()
                .appendPrefix()
                .red().append("Insufficient Permissions!")
                .sendTo(sender);
            return false;
        }

        if (args.length == 0) {
            textService.builder()
                .appendPrefix()
                .yellow().append("Mode is required")
                .sendTo(sender);
            return false;
        }
        String mode = args[0];

        if (args.length < 2) {
            textService.builder()
                .appendPrefix()
                .yellow().append("No users were selected by your query")
                .sendTo(sender);
        }
        args[0] = "";
        List<String> playerNames = Arrays.asList(args.clone());

        if (mode.equals("all")) {
            if (!sender.hasPermission(DataSyncKeys.MANUAL_OPTIMIZATION_ALL_PERMISSION.getFallbackValue())) {
                textService.builder()
                    .appendPrefix()
                    .red().append("You do not have permission to start optimization task: all")
                    .sendTo(sender);
            } else if (snapshotOptimizationManager.getPrimaryComponent().optimize(sender)) {
                textService.builder()
                    .appendPrefix()
                    .yellow().append("Successfully started optimization task: all")
                    .sendTo(sender);
            } else {
                textService.builder()
                    .appendPrefix()
                    .yellow().append("Optimizer already running! Use /sync optimize info");
            }
        } else {
            if (playerNames.isEmpty()) {
                textService.builder()
                    .appendPrefix()
                    .yellow().append("No users were selected by your query")
                    .sendTo(sender);
                return true;
            } else {
                List<Player> players = new ArrayList<>();
                for (String name : playerNames) {
                    players.add(Bukkit.getPlayer(name));
                }
                if (snapshotOptimizationManager.getPrimaryComponent().optimize(players, sender, "Manual")) {
                    textService.builder()
                        .appendPrefix()
                        .yellow().append("Successfully started optimization task: user")
                        .sendTo(sender);
                } else {
                    textService.builder()
                        .appendPrefix()
                        .yellow().append("Optimizer already running! Use /sync optimize info")
                        .sendTo(sender);
                }
            }
        }
        return true;
    }
}
