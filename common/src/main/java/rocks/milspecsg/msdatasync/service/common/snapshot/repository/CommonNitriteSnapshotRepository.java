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
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteId;
import rocks.milspecsg.msdatasync.model.core.snapshot.Snapshot;
import rocks.milspecsg.msrepository.api.cache.RepositoryCacheService;
import rocks.milspecsg.msrepository.datastore.DataStoreContext;
import rocks.milspecsg.msrepository.datastore.nitrite.NitriteConfig;
import rocks.milspecsg.msrepository.service.common.repository.CommonNitriteRepository;

public class CommonNitriteSnapshotRepository<
    TSnapshot extends Snapshot<NitriteId>,
    TDataKey>
    extends CommonSnapshotRepository<NitriteId, TSnapshot, TDataKey, Nitrite, NitriteConfig>
    implements CommonNitriteRepository<TSnapshot, RepositoryCacheService<NitriteId, TSnapshot, Nitrite, NitriteConfig>> {

    @Inject
    public CommonNitriteSnapshotRepository(DataStoreContext<NitriteId, Nitrite, NitriteConfig> dataStoreContext) {
        super(dataStoreContext);
    }
}
