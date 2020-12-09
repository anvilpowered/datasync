/*
 * DataSync - AnvilPowered
 *   Copyright (C) 2020
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
 *     along with this program.  If not, see https://www.gnu.org/licenses/.
 */

package org.anvilpowered.datasync.common.misc;

import com.google.inject.Singleton;
import org.anvilpowered.datasync.api.misc.ListenerUtils;
import org.anvilpowered.datasync.api.model.snapshot.Snapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public class CommonListenerUtils implements ListenerUtils {

    private final Map<UUID, Snapshot<?>> userSnapshotMap = new HashMap<>();
    private final Map<UUID, String> snapshotTargetMap = new HashMap<>();
    private final Map<UUID, Boolean> closeDataMap = new HashMap<>();

    @Override
    public void add(UUID userUUID, Snapshot<?> snapshot, String targetUser) {
        userSnapshotMap.put(userUUID, snapshot);
        snapshotTargetMap.put(userUUID, targetUser);
        closeDataMap.put(userUUID, false);
    }

    @Override
    public void remove(UUID userUUID) {
        userSnapshotMap.remove(userUUID);
        snapshotTargetMap.remove(userUUID);
        closeDataMap.remove(userUUID);
    }

    @Override
    public Snapshot<?> getSnapshot(UUID userUUID) {
        return userSnapshotMap.get(userUUID);
    }

    @Override
    public String getTargetUser(UUID userUUID) {
        return snapshotTargetMap.get(userUUID);
    }

    @Override
    public boolean contains(UUID userUUID) {
        return userSnapshotMap.containsKey(userUUID);
    }

    @Override
    public void setCloseData(UUID userUUID, Boolean closeData) {
        closeDataMap.put(userUUID, closeData);
    }

    @Override
    public boolean getCloseData(UUID userUUID) {
        return closeDataMap.get(userUUID);
    }
}
