package org.anvilpowered.datasync.spigot.util;

import org.anvilpowered.datasync.api.model.snapshot.Snapshot;
import org.anvilpowered.datasync.api.snapshot.SnapshotManager;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.Optional;

public class Utils {

    public static <E> boolean serialize(SnapshotManager<String> snapshotManager,
                                        Snapshot<?> snapshot, Player player,
                                        String key) {
        switch (key) {
            case "FOOD_LEVEL":
                return snapshotManager.getPrimaryComponent()
                    .setSnapshotValue(snapshot, key, Optional.of(player.getFoodLevel()));
            case "SATURATION":
                return snapshotManager.getPrimaryComponent()
                    .setSnapshotValue(snapshot, key, Optional.of(player.getSaturation()));
            case "HEALTH":
                return snapshotManager.getPrimaryComponent()
                    .setSnapshotValue(snapshot, key, Optional.of(player.getHealth()));
            case "TOTAL_EXPERIENCE":
                return snapshotManager.getPrimaryComponent()
                    .setSnapshotValue(snapshot, key, Optional.of(player.getTotalExperience()));
            case "GAME_MODE":
                return snapshotManager.getPrimaryComponent()
                    .setSnapshotValue(snapshot, key, Optional.of(player.getGameMode()));
            default:
                throw new AssertionError("Failed to serialize snapshot for " + player.getDisplayName());
        }
    }

    public static <E> boolean deserialize(SnapshotManager<String> snapshotManager,
                                          Snapshot<?> snapshot, Player player,
                                          String key) {
        Optional<?> optionalSnapshot = snapshotManager.getPrimaryComponent()
            .getSnapshotValue(snapshot, key);
        if (!optionalSnapshot.isPresent()) {
            return false;
        }

        switch (key) {
            case "FOOD_LEVEL":
                player.setFoodLevel(Integer.parseInt(optionalSnapshot.get().toString()));
                return true;
            case "SATURATION":
                player.setSaturation(Float.parseFloat(optionalSnapshot.get().toString()));
                return true;
            case "HEALTH":
                player.setHealth(Double.parseDouble(optionalSnapshot.get().toString()));
                return true;
            case "TOTAL_EXPERIENCE":
                player.setTotalExperience(Integer.parseInt(optionalSnapshot.get().toString()));
                return true;
            case "GAME_MODE":
                player.setGameMode((GameMode) optionalSnapshot.get());
                return true;
            default:
                return false;
        }
    }
}
