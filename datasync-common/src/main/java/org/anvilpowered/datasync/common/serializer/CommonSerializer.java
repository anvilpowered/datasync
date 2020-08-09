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

package org.anvilpowered.datasync.common.serializer;

import com.google.inject.Inject;
import org.anvilpowered.datasync.api.serializer.Serializer;
import org.anvilpowered.datasync.api.snapshot.SnapshotManager;

public abstract class CommonSerializer<
    TDataKey,
    TUser>
    implements Serializer<TUser> {

    @Inject
    protected SnapshotManager<TDataKey> snapshotManager;
}