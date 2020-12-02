package org.anvilpowered.datasync.spigot.task;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.md_5.bungee.api.chat.TextComponent;
import org.anvilpowered.anvil.api.plugin.PluginInfo;
import org.anvilpowered.anvil.api.registry.Registry;
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

    private Runnable task = null;

    @Inject
    public SpigotSerializationTaskService(Registry registry) {
        super(registry);
    }

    @Override
    public void startSerializationTask() {
        if (baseInterval > 0) {
            Bukkit.getConsoleSender()
                .sendMessage(pluginInfo.getPrefix() + "Submitting sync task! Upload interval: " + baseInterval + " minutes");
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
                Bukkit.getConsoleSender().sendMessage(
                    pluginInfo.getPrefix() + "Optimization task already running! Task will skip"
                );
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
