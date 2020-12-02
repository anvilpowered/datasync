package org.anvilpowered.datasync.spigot.serializer;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.md_5.bungee.api.chat.TextComponent;
import org.anvilpowered.anvil.api.plugin.PluginInfo;
import org.anvilpowered.anvil.api.registry.Registry;
import org.anvilpowered.datasync.common.serializer.CommonSnapshotSerializer;
import org.anvilpowered.datasync.spigot.DataSyncSpigot;
import org.bukkit.Bukkit;
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
    private DataSyncSpigot dataSyncSpigot;

    @Override
    protected void postLoadedEvent() {
    }

    @Override
    protected void announceEnabled(String name) {
        Bukkit.getServer().getConsoleSender().sendMessage(pluginInfo.getPrefix().getText() + "Enabling " + name + " serializer");
    }
}
