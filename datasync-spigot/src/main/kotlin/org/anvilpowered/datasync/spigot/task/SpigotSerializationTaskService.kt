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
package org.anvilpowered.datasync.spigot.task

import com.google.inject.Inject
import com.google.inject.Singleton
import net.md_5.bungee.api.chat.TextComponent
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.anvil.api.util.TextService
import org.anvilpowered.datasync.common.task.CommonSerializationTaskService
import org.anvilpowered.datasync.spigot.DataSyncSpigot
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Singleton
class SpigotSerializationTaskService @Inject constructor(registry: Registry) : CommonSerializationTaskService<Player, TextComponent, CommandSender>(registry) {

    @Inject
    private lateinit var plugin: DataSyncSpigot

    @Inject
    private lateinit var textService: TextService<TextComponent, CommandSender>

    private var task: Runnable? = null

    override fun startSerializationTask() {
        if (baseInterval > 0) {
            textService.builder()
                .appendPrefix()
                .green().append("Submitting sync task! Upload interval: ")
                .gold().append(baseInterval)
                .green().append(" minutes")
                .sendToConsole()
            task = serializationTask
            Bukkit.getScheduler().runTaskTimer(plugin, task!!, 0L, baseInterval * 60 * 20.toLong())
        }
    }

    override fun stopSerializationTask() {
        if (task != null) {
            task = null
        }
    }

    override fun getSerializationTask(): Runnable {
        return Runnable {
            if (snapshotOptimizationManager.primaryComponent.isOptimizationTaskRunning) {
                textService.builder()
                    .appendPrefix()
                    .yellow().append("Optimization task already running! Task will skip")
                    .sendToConsole()
            } else {
                snapshotOptimizationManager.primaryComponent
                    .optimize(Bukkit.getOnlinePlayers(), Bukkit.getConsoleSender(), "Auto")
            }
        }
    }
}
