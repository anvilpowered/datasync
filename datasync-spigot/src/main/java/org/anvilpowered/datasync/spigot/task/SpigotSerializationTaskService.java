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

package org.anvilpowered.datasync.spigot.task;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.md_5.bungee.api.chat.TextComponent;
import org.anvilpowered.anvil.api.plugin.PluginInfo;
import org.anvilpowered.anvil.api.registry.Registry;
import org.anvilpowered.anvil.api.util.TextService;
import org.anvilpowered.datasync.common.task.CommonSerializationTaskService;
import org.anvilpowered.datasync.spigot.DataSyncSpigot;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Singleton
public class SpigotSerializationTaskService extends CommonSerializationTaskService<Player, TextComponent, CommandSender> {

    @Inject
    private DataSyncSpigot plugin;

    @Inject
    private PluginInfo<TextComponent> pluginInfo;

    @Inject
    private TextService<TextComponent, CommandSender> textService;

    private Runnable task = null;

    @Inject
    public SpigotSerializationTaskService(Registry registry) {
        super(registry);
    }

    @Override
    public void startSerializationTask() {
        if (baseInterval > 0) {
            textService.builder()
                .append(pluginInfo.getPrefix())
                .green().append("Submitting sync task! Upload interval: ")
                .gold().append(baseInterval)
                .green().append(" minutes")
                .sendToConsole();
            task = getSerializationTask();
            Bukkit.getScheduler().runTaskTimer(plugin, task, 0L, 1200);
        }
    }

    @Override
    public void stopSerializationTask() {
        if (task != null) {
            task = null;
        }
    }

    @Override
    public Runnable getSerializationTask() {
        return () -> {
            if (snapshotOptimizationManager.getPrimaryComponent().isOptimizationTaskRunning()) {
                textService.builder()
                    .append(pluginInfo.getPrefix())
                    .yellow().append("Optimization task already running! Task will skip")
                    .sendToConsole();
            } else {
                snapshotOptimizationManager.getPrimaryComponent()
                    .optimize(
                        Bukkit.getOnlinePlayers(),
                        Bukkit.getConsoleSender(),
                        "Auto"
                    );
            }
        };
    }
}
