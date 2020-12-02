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
import com.google.inject.Singleton;
import org.anvilpowered.anvil.api.registry.Registry;
import org.anvilpowered.datasync.common.command.CommonSyncCommandNode;
import org.anvilpowered.datasync.spigot.DataSyncSpigot;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Singleton
public class SpigotSyncCommandNode
    extends CommonSyncCommandNode<CommandExecutor, CommandSender> {

    @Inject
    private SpigotSyncTestCommand spigotSyncTestCommand;

    @Inject
    private DataSyncSpigot plugin;

    @Inject
    public SpigotSyncCommandNode(Registry registry) {
        super(registry);
    }

    @Override
    protected void loadCommands() {
        Map<List<String>, CommandExecutor> subCommands = new HashMap<>();
        subCommands.put(TEST_ALIAS, spigotSyncTestCommand);
        
        Objects.requireNonNull(plugin.getCommand("datasync")).setExecutor(
            commandService.generateRoutingCommand(
                commandService.generateRootCommand(HELP_COMMAND), subCommands, false
            )
        );
    }
}
