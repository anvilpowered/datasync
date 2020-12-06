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
