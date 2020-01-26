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

package rocks.milspecsg.msdatasync.common.snapshot.repository;

import com.google.inject.Inject;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import rocks.milspecsg.msdatasync.api.model.serializeditemstack.SerializedItemStack;
import rocks.milspecsg.msdatasync.api.model.snapshot.Snapshot;
import rocks.milspecsg.msrepository.api.datastore.DataStoreContext;
import rocks.milspecsg.msrepository.common.repository.CommonMongoRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CommonMongoSnapshotRepository<
    TSnapshot extends Snapshot<ObjectId>,
    TDataKey>
    extends CommonSnapshotRepository<ObjectId, TSnapshot, TDataKey, Datastore>
    implements CommonMongoRepository<TSnapshot> {

    @Inject
    public CommonMongoSnapshotRepository(DataStoreContext<ObjectId, Datastore> dataStoreContext) {
        super(dataStoreContext);
    }

    @Override
    public CompletableFuture<Boolean> setItemStacks(ObjectId id, List<SerializedItemStack> itemStacks) {
        return update(asQuery(id), set("itemStacks", itemStacks));
    }
}
