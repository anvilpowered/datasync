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

package org.anvilpowered.datasync.spigot.util;

import org.anvilpowered.datasync.api.model.snapshot.Snapshot;
import org.anvilpowered.datasync.api.snapshot.SnapshotManager;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.Optional;

public class Utils {

    public static boolean serialize(SnapshotManager<String> snapshotManager,
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

    public static boolean deserialize(SnapshotManager<String> snapshotManager,
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
