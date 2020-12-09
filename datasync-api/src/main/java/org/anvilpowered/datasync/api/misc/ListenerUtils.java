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

package org.anvilpowered.datasync.api.misc;

import org.anvilpowered.datasync.api.model.snapshot.Snapshot;

import java.util.UUID;

public interface ListenerUtils {

    void add(UUID userUUID, Snapshot<?> snapshot, String targetUser);

    void remove(UUID userUUID);

    Snapshot<?> getSnapshot(UUID userUUID);

    String getTargetUser(UUID userUUID);

    boolean contains(UUID userUUID);

    void setCloseData(UUID userUUID, Boolean closeData);

    boolean getCloseData(UUID userUUID);
}
