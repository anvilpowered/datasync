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

package org.anvilpowered.datasync.api.snapshot.repository;

import org.anvilpowered.anvil.api.repository.Repository;
import org.anvilpowered.datasync.api.model.serializeditemstack.SerializedItemStack;
import org.anvilpowered.datasync.api.model.snapshot.Snapshot;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface SnapshotRepository<
    TKey,
    TDataKey,
    TDataStore>
    extends Repository<TKey, Snapshot<TKey>, TDataStore> {

    boolean setSnapshotValue(Snapshot<?> snapshot, TDataKey key, Optional<?> optionalValue);

    Optional<?> getSnapshotValue(Snapshot<?> snapshot, TDataKey key);

    CompletableFuture<Boolean> setItemStacks(TKey id, List<SerializedItemStack> itemStacks);

    CompletableFuture<Boolean> parseAndSetItemStacks(Object id, List<SerializedItemStack> itemStacks);
}
