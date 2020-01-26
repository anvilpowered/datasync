/*
 *     MSDataSync - MilSpecSG
 *     Copyright (C) 2019 Cableguy20
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

package rocks.milspecsg.msdatasync.api.snapshot;

import rocks.milspecsg.msdatasync.api.model.snapshot.Snapshot;
import rocks.milspecsg.msdatasync.api.snapshot.repository.SnapshotRepository;
import rocks.milspecsg.msrepository.api.manager.Manager;

public interface SnapshotManager<
    TSnapshot extends Snapshot<?>,
    TDataKey> extends Manager<SnapshotRepository<?, TSnapshot, TDataKey, ?>> {

    @Override
    default String getDefaultIdentifierSingularUpper() {
        return "Snapshot";
    }

    @Override
    default String getDefaultIdentifierPluralUpper() {
        return "Snapshots";
    }

    @Override
    default String getDefaultIdentifierSingularLower() {
        return "snapshot";
    }

    @Override
    default String getDefaultIdentifierPluralLower() {
        return "snapshots";
    }
}
