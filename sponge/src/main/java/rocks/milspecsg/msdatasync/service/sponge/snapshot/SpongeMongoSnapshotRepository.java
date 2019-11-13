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

package rocks.milspecsg.msdatasync.service.sponge.snapshot;

import org.spongepowered.api.data.key.Key;
import rocks.milspecsg.msdatasync.model.core.snapshot.MongoSnapshot;
import rocks.milspecsg.msdatasync.service.common.snapshot.repository.CommonMongoSnapshotRepository;
import rocks.milspecsg.msrepository.datastore.mongodb.MongoContext;

public abstract class SpongeMongoSnapshotRepository extends CommonMongoSnapshotRepository<MongoSnapshot, Key<?>> {

    protected SpongeMongoSnapshotRepository(MongoContext mongoContext) {
        super(mongoContext);
    }
}
