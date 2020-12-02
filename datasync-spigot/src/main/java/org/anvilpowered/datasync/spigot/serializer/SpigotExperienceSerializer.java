package org.anvilpowered.datasync.spigot.serializer;

import org.anvilpowered.datasync.api.model.snapshot.Snapshot;
import org.anvilpowered.datasync.common.serializer.CommonExperienceSerializer;
import org.anvilpowered.datasync.spigot.util.Utils;
import org.bukkit.entity.Player;

public class SpigotExperienceSerializer extends CommonExperienceSerializer<String, Player> {

    @Override
    public boolean serialize(Snapshot<?> snapshot, Player player) {
        return Utils.serialize(snapshotManager, snapshot, player, "TOTAL_EXPERIENCE");
    }

    @Override
    public boolean deserialize(Snapshot<?> snapshot, Player player) {
        return Utils.deserialize(snapshotManager, snapshot, player, "TOTAL_EXPERIENCE");
    }
}
