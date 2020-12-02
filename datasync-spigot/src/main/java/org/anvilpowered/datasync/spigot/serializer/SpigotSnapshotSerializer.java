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

package org.anvilpowered.datasync.spigot.serializer;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.md_5.bungee.api.chat.TextComponent;
import org.anvilpowered.anvil.api.plugin.PluginInfo;
import org.anvilpowered.anvil.api.registry.Registry;
import org.anvilpowered.anvil.api.util.TextService;
import org.anvilpowered.datasync.common.serializer.CommonSnapshotSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Singleton
public class SpigotSnapshotSerializer
    extends CommonSnapshotSerializer<String, Player, Player, Inventory, ItemStack> {

    @Inject
    public SpigotSnapshotSerializer(Registry registry) {
        super(registry);
    }

    @Inject
    private PluginInfo<TextComponent> pluginInfo;

    @Inject
    private TextService<TextComponent, CommandSender> textService;

    @Override
    protected void postLoadedEvent() {
    }

    @Override
    protected void announceEnabled(String name) {
        textService.builder()
            .append(pluginInfo.getPrefix())
            .yellow().append("Enabling " + name + " serializer")
            .sendToConsole();
    }
}
