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

package org.anvilpowered.datasync.common.serializer.user;

import com.google.inject.Singleton;
import org.anvilpowered.datasync.api.serializer.user.UserTransitCache;

import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

@Singleton
public class CommonUserTransitCache implements UserTransitCache {

    private final Set<UUID> uuids;

    public CommonUserTransitCache() {
        uuids = new TreeSet<>();
    }

    @Override
    public void joinStart(UUID userUUID) {
        synchronized (uuids) {
            uuids.add(userUUID);
        }
    }

    @Override
    public void joinEnd(UUID userUUID) {
        synchronized (uuids) {
            uuids.remove(userUUID);
        }
    }

    @Override
    public boolean isJoining(UUID userUUID) {
        synchronized (uuids) {
            return uuids.contains(userUUID);
        }
    }
}
