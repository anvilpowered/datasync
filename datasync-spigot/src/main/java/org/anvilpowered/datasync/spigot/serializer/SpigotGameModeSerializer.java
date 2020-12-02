package org.anvilpowered.datasync.spigot.serializer;

import org.anvilpowered.datasync.api.model.snapshot.Snapshot;
import org.anvilpowered.datasync.common.serializer.CommonGameModeSerializer;
import org.anvilpowered.datasync.spigot.util.Utils;
import org.bukkit.entity.Player;

public class SpigotGameModeSerializer extends CommonGameModeSerializer<String, Player> {

    @Override
    public boolean serialize(Snapshot<?> snapshot, Player player) {
        return Utils.serialize(snapshotManager, snapshot, player, "GAME_MODE");
    }

    @Override
    public boolean deserialize(Snapshot<?> snapshot, Player player) {
        return Utils.deserialize(snapshotManager, snapshot, player, "GAME_MODE");
    }
}
