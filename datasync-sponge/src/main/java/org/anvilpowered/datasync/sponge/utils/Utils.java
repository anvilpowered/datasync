/*
 *   DataSync - AnvilPowered
 *   Copyright (C) 2020 Cableguy20
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.datasync.sponge.utils;

import com.google.common.reflect.TypeToken;
import org.anvilpowered.datasync.api.model.snapshot.Snapshot;
import org.anvilpowered.datasync.api.snapshot.SnapshotManager;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;

import java.util.Optional;

public class Utils {

    public static <E> boolean serialize(SnapshotManager<Key<?>> snapshotManager,
                                        Snapshot<?> snapshot, User user,
                                        Key<? extends BaseValue<E>> key) {
        return snapshotManager.getPrimaryComponent()
            .setSnapshotValue(snapshot, key, user.get(key));
    }

    public static <E> boolean deserialize(SnapshotManager< Key<?>> snapshotManager,
                                          Snapshot<?> snapshot, User user,
                                          Key<? extends BaseValue<E>> key) {
        Optional<?> optionalSnapshot = snapshotManager.getPrimaryComponent()
            .getSnapshotValue(snapshot, key);
        if (!optionalSnapshot.isPresent()) {
            return false;
        }

        try {
            user.offer(key, (E) decode(optionalSnapshot.get(), key));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static Object decode(Object value, Key<? extends BaseValue<?>> key) {
        TypeToken<?> typeToken = key.getElementToken();

        if (typeToken.isSubtypeOf(GameMode.class)) {
            switch (value.toString()) {
                case "ADVENTURE":
                    return GameModes.ADVENTURE;
                case "CREATIVE":
                    return GameModes.CREATIVE;
                case "SPECTATOR":
                    return GameModes.SPECTATOR;
                case "SURVIVAL":
                    return GameModes.SURVIVAL;
                // "NOT_SET"
                default:
                    return GameModes.NOT_SET;
            }
        }
        return value;
    }
}
