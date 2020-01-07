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

package rocks.milspecsg.msdatasync.service.common.snapshot.repository;

import com.google.inject.Inject;
import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityId;
import jetbrains.exodus.entitystore.PersistentEntityStore;
import rocks.milspecsg.msdatasync.model.core.snapshot.Snapshot;
import rocks.milspecsg.msrepository.api.cache.CacheService;
import rocks.milspecsg.msrepository.datastore.DataStoreContext;
import rocks.milspecsg.msrepository.datastore.xodus.XodusConfig;
import rocks.milspecsg.msrepository.model.data.dbo.Mappable;
import rocks.milspecsg.msrepository.service.common.repository.CommonXodusRepository;

public class CommonXodusSnapshotRepository<
    TSnapshot extends Snapshot<EntityId> & Mappable<Entity>,
    TDataKey>
    extends CommonSnapshotRepository<EntityId, TSnapshot, TDataKey, PersistentEntityStore, XodusConfig>
    implements CommonXodusRepository<TSnapshot, CacheService<EntityId, TSnapshot, PersistentEntityStore, XodusConfig>> {

    @Inject
    public CommonXodusSnapshotRepository(DataStoreContext<EntityId, PersistentEntityStore, XodusConfig> dataStoreContext) {
        super(dataStoreContext);
    }
}
