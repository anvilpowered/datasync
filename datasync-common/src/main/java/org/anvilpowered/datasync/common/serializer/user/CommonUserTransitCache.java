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

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Singleton
public class CommonUserTransitCache implements UserTransitCache {

    private final ConcurrentMap<UUID, CompletableFuture<Boolean>> uuids;

    public CommonUserTransitCache() {
        uuids = new ConcurrentHashMap<>();
    }

    @Override
    public void joinStart(UUID userUUID, CompletableFuture<Boolean> waitFuture) {
        uuids.put(userUUID, waitFuture);
    }

    @Override
    public void joinEnd(UUID userUUID) {
        CompletableFuture<Boolean> waitFuture = uuids.remove(userUUID);
        // mark the wait future as invalid
        // in normal cases, this will be called after deserialization is already complete which will have no effect.
        // However, if the player leaves before waitFuture is done, mark it as invalid which prevents
        // premature deserialization
        if (waitFuture != null) {
            waitFuture.complete(false);
        }
    }

    @Override
    public boolean isJoining(UUID userUUID) {
        return uuids.containsKey(userUUID);
    }
}
