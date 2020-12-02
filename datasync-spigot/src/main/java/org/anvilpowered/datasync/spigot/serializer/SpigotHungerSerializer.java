package org.anvilpowered.datasync.spigot.serializer;

import org.anvilpowered.datasync.api.model.snapshot.Snapshot;
import org.anvilpowered.datasync.common.serializer.CommonHungerSerializer;
import org.anvilpowered.datasync.spigot.util.Utils;
import org.bukkit.entity.Player;

public class SpigotHungerSerializer extends CommonHungerSerializer<String, Player> {

    @Override
    public boolean serialize(Snapshot<?> snapshot, Player player) {
        // second statement should still run if first one fails
        boolean a = Utils.serialize(snapshotManager, snapshot, player, "FOOD_LEVEL");
        boolean b = Utils.serialize(snapshotManager, snapshot, player, "SATURATION");
        return a && b;
    }

    @Override
    public boolean deserialize(Snapshot<?> snapshot, Player player) {
        // second statement should still run if first one fails
        boolean a = Utils.deserialize(snapshotManager, snapshot, player, "FOOD_LEVEL");
        boolean b = Utils.deserialize(snapshotManager, snapshot, player, "SATURATION");
        return a && b;
    }
}
