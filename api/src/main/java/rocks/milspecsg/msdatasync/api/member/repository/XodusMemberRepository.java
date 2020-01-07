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

package rocks.milspecsg.msdatasync.api.member.repository;

import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityId;
import jetbrains.exodus.entitystore.PersistentEntityStore;
import jetbrains.exodus.entitystore.StoreTransaction;
import rocks.milspecsg.msdatasync.model.core.member.Member;
import rocks.milspecsg.msdatasync.model.core.snapshot.Snapshot;
import rocks.milspecsg.msrepository.api.cache.CacheService;
import rocks.milspecsg.msrepository.api.repository.XodusRepository;
import rocks.milspecsg.msrepository.datastore.xodus.XodusConfig;
import rocks.milspecsg.msrepository.model.data.dbo.Mappable;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public interface XodusMemberRepository<
    TMember extends Member<EntityId> & Mappable<Entity>,
    TSnapshot extends Snapshot<EntityId>,
    TUser>
    extends MemberRepository<EntityId, TMember, TSnapshot, TUser, PersistentEntityStore, XodusConfig>,
    XodusRepository<TMember, CacheService<EntityId, TMember, PersistentEntityStore, XodusConfig>> {

    CompletableFuture<List<EntityId>> getSnapshotIds(Function<? super StoreTransaction, ? extends Iterable<Entity>> query);

    CompletableFuture<List<Date>> getSnapshotDates(Function<? super StoreTransaction, ? extends Iterable<Entity>> query);

    CompletableFuture<Boolean> deleteSnapshot(Function<? super StoreTransaction, ? extends Iterable<Entity>> query, EntityId snapshotId);

    CompletableFuture<Boolean> deleteSnapshot(Function<? super StoreTransaction, ? extends Iterable<Entity>> query, Date date);

    CompletableFuture<Boolean> addSnapshot(Function<? super StoreTransaction, ? extends Iterable<Entity>> query, EntityId snapshotId);

    CompletableFuture<Optional<TSnapshot>> getSnapshot(Function<? super StoreTransaction, ? extends Iterable<Entity>> query, Date date);

    CompletableFuture<List<EntityId>> getClosestSnapshots(Function<? super StoreTransaction, ? extends Iterable<Entity>> query, Date date);

    Function<? super StoreTransaction, ? extends Iterable<Entity>> asQuery(UUID userUUID);
}
