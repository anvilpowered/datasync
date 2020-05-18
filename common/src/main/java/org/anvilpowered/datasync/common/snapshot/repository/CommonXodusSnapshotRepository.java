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

package org.anvilpowered.datasync.common.snapshot.repository;

import com.google.inject.Inject;
import jetbrains.exodus.entitystore.EntityId;
import jetbrains.exodus.entitystore.PersistentEntityStore;
import jetbrains.exodus.util.ByteArraySizedInputStream;
import org.anvilpowered.anvil.api.datastore.DataStoreContext;
import org.anvilpowered.anvil.api.model.Mappable;
import org.anvilpowered.anvil.base.datastore.BaseXodusRepository;
import org.anvilpowered.datasync.api.model.snapshot.Snapshot;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CommonXodusSnapshotRepository<TDataKey>
    extends CommonSnapshotRepository<EntityId, TDataKey, PersistentEntityStore>
    implements BaseXodusRepository<Snapshot<EntityId>> {

    @Inject
    public CommonXodusSnapshotRepository(DataStoreContext<EntityId, PersistentEntityStore> dataStoreContext) {
        super(dataStoreContext);
    }

    @Override
    public CompletableFuture<Boolean> setItemStacks(EntityId id, List<String> itemStacks) {
        return update(asQuery(id), entity -> {
            try {
                entity.setBlob("itemStacks", new ByteArraySizedInputStream(Mappable.serializeUnsafe(itemStacks)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
