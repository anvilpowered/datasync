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

package org.anvilpowered.datasync.nukkit.util

import cn.nukkit.Player
import org.anvilpowered.datasync.api.model.snapshot.Snapshot
import org.anvilpowered.datasync.api.snapshot.SnapshotManager
import java.util.Optional

object Utils {
    fun serialize(snapshotManager: SnapshotManager<String>,
                  snapshot: Snapshot<*>, player: Player,
                  key: String): Boolean {
        return when (key) {
            "FOOD_LEVEL" -> snapshotManager.primaryComponent
                .setSnapshotValue(snapshot, key, Optional.of(player.foodData.level))
            "SATURATION" -> snapshotManager.primaryComponent
                .setSnapshotValue(snapshot, key, Optional.of(player.foodData.foodSaturationLevel))
            "HEALTH" -> snapshotManager.primaryComponent
                .setSnapshotValue(snapshot, key, Optional.of(player.health))
            "TOTAL_EXPERIENCE" -> snapshotManager.primaryComponent
                .setSnapshotValue(snapshot, key, Optional.of(player.experience))
            "GAME_MODE" -> snapshotManager.primaryComponent
                .setSnapshotValue(snapshot, key, Optional.of(player.gamemode))
            else -> throw AssertionError("Failed to serialize snapshot for ${player.displayName}")
        }
    }

    fun deserialize(snapshotManager: SnapshotManager<String>,
                    snapshot: Snapshot<*>, player: Player,
                    key: String): Boolean {
        val optionalSnapshot = snapshotManager.primaryComponent
            .getSnapshotValue(snapshot, key)
        return if (!optionalSnapshot.isPresent) {
            false
        } else when (key) {
            "FOOD_LEVEL" -> {
                player.foodData.level = optionalSnapshot.get().toString().toInt()
                true
            }
            "SATURATION" -> {
                player.foodData.foodSaturationLevel = optionalSnapshot.get().toString().toFloat()
                true
            }
            "HEALTH" -> {
                player.health = optionalSnapshot.get().toString().toFloat()
                true
            }
            "TOTAL_EXPERIENCE" -> {
                player.experience = optionalSnapshot.get().toString().toInt()
                true
            }
            "GAME_MODE" -> {
                player.gamemode = optionalSnapshot.get().toString().toInt()
                true
            }
            else -> false
        }
    }
}
