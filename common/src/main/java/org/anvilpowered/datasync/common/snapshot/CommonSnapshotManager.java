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

package org.anvilpowered.datasync.common.snapshot;

import com.google.inject.Inject;
import org.anvilpowered.anvil.api.data.registry.Registry;
import org.anvilpowered.anvil.base.datastore.BaseManager;
import org.anvilpowered.datasync.api.snapshot.SnapshotManager;
import org.anvilpowered.datasync.api.snapshot.repository.SnapshotRepository;

public class CommonSnapshotManager<TDataKey>
    extends BaseManager<SnapshotRepository<?, TDataKey, ?>>
    implements SnapshotManager<TDataKey> {

    @Inject
    public CommonSnapshotManager(Registry registry) {
        super(registry);
    }
}
