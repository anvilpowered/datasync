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

package org.anvilpowered.datasync.nukkit.task

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.command.CommandSender
import cn.nukkit.plugin.PluginManager
import com.google.inject.Inject
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.anvil.api.util.TextService
import org.anvilpowered.datasync.common.task.CommonSerializationTaskService
import org.anvilpowered.datasync.nukkit.DataSyncNukkit

class NukkitSerializationTaskService @Inject constructor(
    registry: Registry
) : CommonSerializationTaskService<Player, String, CommandSender>(registry) {

    @Inject
    private lateinit var plugin: DataSyncNukkit

    @Inject
    private lateinit var textService: TextService<String, CommandSender>

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
            if (Server.getInstance().pluginManager.isPluginEnabled(plugin)) {
                Server.getInstance().scheduler.scheduleRepeatingTask(plugin, task!!, baseInterval * 60 * 20, false)
            }
        }
    }

    override fun stopSerializationTask() {
        if (task != null) {
            task = null
            Server.getInstance().scheduler.cancelTask(plugin)
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
                    .optimize(Server.getInstance().onlinePlayers.values, Server.getInstance().consoleSender, "Auto")
            }
        }
    }
}
