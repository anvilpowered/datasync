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
import org.anvilpowered.anvil.api.datastore.DataStoreContext;
import org.anvilpowered.anvil.base.repository.BaseRepository;
import org.anvilpowered.datasync.api.keys.DataKeyService;
import org.anvilpowered.datasync.api.model.serializeditemstack.SerializedItemStack;
import org.anvilpowered.datasync.api.model.snapshot.Snapshot;
import org.anvilpowered.datasync.api.snapshot.repository.SnapshotRepository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public abstract class CommonSnapshotRepository<
    TKey,
    TSnapshot extends Snapshot<TKey>,
    TDataKey,
    TDataStore>
    extends BaseRepository<TKey, TSnapshot, TDataStore>
    implements SnapshotRepository<TKey, TSnapshot, TDataKey, TDataStore> {

    @Inject
    DataKeyService<TDataKey> dataKeyService;

    protected CommonSnapshotRepository(DataStoreContext<TKey, TDataStore> dataStoreContext) {
        super(dataStoreContext);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<TSnapshot> getTClass() {
        return (Class<TSnapshot>) getDataStoreContext().getEntityClassUnsafe("snapshot");
    }

    @Override
    public boolean setSnapshotValue(TSnapshot snapshot, TDataKey key, Optional<?> optionalValue) {
        if (!optionalValue.isPresent()) {
            return false;
        }
        Optional<String> optionalName = dataKeyService.getName(key);
        if (!optionalName.isPresent()) {
            return false;
        }
        snapshot.getKeys().put(optionalName.get(), optionalValue.get());
        return true;
    }

    @Override
    public Optional<?> getSnapshotValue(TSnapshot snapshot, TDataKey key) {
        Optional<String> optionalName = dataKeyService.getName(key);
        return optionalName.map(s -> snapshot.getKeys().get(s));
    }

    @Override
    public CompletableFuture<Boolean> parseAndSetItemStacks(Object id, List<SerializedItemStack> itemStacks) {
        return parse(id).map(i -> setItemStacks(i, itemStacks)).orElse(CompletableFuture.completedFuture(false));
    }
}
