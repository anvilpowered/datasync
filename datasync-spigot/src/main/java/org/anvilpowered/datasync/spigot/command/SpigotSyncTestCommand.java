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
import org.anvilpowered.anvil.api.plugin.PluginInfo;
import org.anvilpowered.anvil.api.util.TextService;
import org.anvilpowered.datasync.api.serializer.user.UserSerializerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpigotSyncTestCommand implements CommandExecutor {

    @Inject
    private PluginInfo<TextComponent> pluginInfo;

    @Inject
    private TextService<TextComponent, CommandSender> textService;

    @Inject
    private UserSerializerManager<Player, TextComponent> userSerializerManager;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;
        userSerializerManager.serialize(player)
            .exceptionally(e -> {
                e.printStackTrace();
                sender.sendMessage("An error occurred!");
                return null;
            })
            .thenAcceptAsync(text -> {
                if (text == null) {
                    return;
                }
                textService.send(text, sender);
                textService.builder()
                    .append(pluginInfo.getPrefix())
                    .green().append("Deserializing in 5 seconds")
                    .sendTo(sender);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                userSerializerManager.restore(player.getUniqueId(), null)
                    .thenAcceptAsync(msg -> textService.send(msg, sender));
            });
        return true;
    }
}
