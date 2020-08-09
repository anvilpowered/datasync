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

package org.anvilpowered.datasync.api.serializer.user;

import java.util.UUID;

/**
 * All methods are thread-safe
 */
public interface UserTransitCache {

    /**
     * Declares that the user with the provided {@link UUID} has started the joining process.
     *
     * @param userUUID The {@link UUID} of the joining user
     */
    void joinStart(UUID userUUID);

    /**
     * Declares that the user with the provided {@link UUID} has ended the joining process.
     *
     * @param userUUID The {@link UUID} of the joining user
     */
    void joinEnd(UUID userUUID);

    /**
     * Check whether the user with the provided {@link UUID} is in the process of joining.
     *
     * @param userUUID The {@link UUID} of the user to check
     * @return Whether the user with the provided {@link UUID} is in the process of joining
     */
    boolean isJoining(UUID userUUID);
}
