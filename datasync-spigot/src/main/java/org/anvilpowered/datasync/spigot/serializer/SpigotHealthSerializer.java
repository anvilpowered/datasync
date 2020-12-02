package org.anvilpowered.datasync.spigot.serializer;

import org.anvilpowered.datasync.api.model.snapshot.Snapshot;
import org.anvilpowered.datasync.common.serializer.CommonHealthSerializer;
import org.anvilpowered.datasync.spigot.util.Utils;
import org.bukkit.entity.Player;

public class SpigotHealthSerializer extends CommonHealthSerializer<String, Player> {

    @Override
    public boolean serialize(Snapshot<?> snapshot, Player player) {
        return Utils.serialize(snapshotManager, snapshot, player, "HEALTH");
    }

    @Override
    public boolean deserialize(Snapshot<?> snapshot, Player player) {
        return Utils.serialize(snapshotManager, snapshot, player, "HEALTH");
    }
}
