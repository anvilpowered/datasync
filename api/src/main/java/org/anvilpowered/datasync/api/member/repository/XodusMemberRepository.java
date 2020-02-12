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

package org.anvilpowered.datasync.api.member.repository;

import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityId;
import jetbrains.exodus.entitystore.PersistentEntityStore;
import jetbrains.exodus.entitystore.StoreTransaction;
import org.anvilpowered.anvil.api.model.Mappable;
import org.anvilpowered.anvil.api.repository.XodusRepository;
import org.anvilpowered.datasync.api.model.member.Member;
import org.anvilpowered.datasync.api.model.snapshot.Snapshot;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public interface XodusMemberRepository<
    TMember extends Member<EntityId> & Mappable<Entity>,
    TSnapshot extends Snapshot<EntityId>,
    TUser>
    extends MemberRepository<EntityId, TMember, TSnapshot, TUser, PersistentEntityStore>,
    XodusRepository<TMember> {

    CompletableFuture<List<EntityId>> getSnapshotIds(Function<? super StoreTransaction, ? extends Iterable<Entity>> query);

    CompletableFuture<List<Instant>> getSnapshotCreationTimes(Function<? super StoreTransaction, ? extends Iterable<Entity>> query);

    CompletableFuture<Boolean> deleteSnapshot(Function<? super StoreTransaction, ? extends Iterable<Entity>> query, EntityId snapshotId);

    CompletableFuture<Boolean> deleteSnapshot(Function<? super StoreTransaction, ? extends Iterable<Entity>> query, Instant createdUtc);

    CompletableFuture<Boolean> addSnapshot(Function<? super StoreTransaction, ? extends Iterable<Entity>> query, EntityId snapshotId);

    CompletableFuture<Optional<TSnapshot>> getSnapshot(Function<? super StoreTransaction, ? extends Iterable<Entity>> query, Instant createdUtc);

    CompletableFuture<List<EntityId>> getClosestSnapshots(Function<? super StoreTransaction, ? extends Iterable<Entity>> query, Instant createdUtc);

    Function<? super StoreTransaction, ? extends Iterable<Entity>> asQuery(UUID userUUID);
}
