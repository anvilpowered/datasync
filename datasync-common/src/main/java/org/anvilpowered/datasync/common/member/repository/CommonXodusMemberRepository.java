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

package org.anvilpowered.datasync.common.member.repository;

import com.google.inject.Inject;
import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityId;
import jetbrains.exodus.entitystore.PersistentEntityStore;
import jetbrains.exodus.entitystore.StoreTransaction;
import jetbrains.exodus.util.ByteArraySizedInputStream;
import org.anvilpowered.anvil.api.datastore.DataStoreContext;
import org.anvilpowered.anvil.api.model.Mappable;
import org.anvilpowered.anvil.base.datastore.BaseXodusRepository;
import org.anvilpowered.datasync.api.member.repository.XodusMemberRepository;
import org.anvilpowered.datasync.api.model.member.Member;
import org.anvilpowered.datasync.api.model.snapshot.Snapshot;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CommonXodusMemberRepository<TDataKey>
    extends CommonMemberRepository<EntityId, TDataKey, PersistentEntityStore>
    implements BaseXodusRepository<Member<EntityId>>,
    XodusMemberRepository {

    @Inject
    public CommonXodusMemberRepository(DataStoreContext<EntityId, PersistentEntityStore> dataStoreContext) {
        super(dataStoreContext);
    }

    @Override
    public CompletableFuture<Optional<Member<EntityId>>> getOneForUser(UUID userUUID) {
        return getOne(asQuery(userUUID));
    }

    @Override
    public CompletableFuture<List<EntityId>> getSnapshotIds(Function<? super StoreTransaction, ? extends Iterable<Entity>> query) {
        return CompletableFuture.supplyAsync(() ->
            getDataStoreContext().getDataStore().computeInExclusiveTransaction(txn -> {
                Iterator<Entity> iterator = query.apply(txn).iterator();
                if (iterator.hasNext()) {
                    Optional<List<EntityId>> optionalList = Mappable.deserialize(iterator.next().getBlob("snapshotIds"));
                    if (optionalList.isPresent()) {
                        return optionalList.get();
                    }
                }
                return Collections.emptyList();
            })
        );
    }

    @Override
    public CompletableFuture<List<EntityId>> getSnapshotIds(EntityId id) {
        return getSnapshotIds(asQuery(id));
    }

    @Override
    public CompletableFuture<List<Instant>> getSnapshotCreationTimes(Function<? super StoreTransaction, ? extends Iterable<Entity>> query) {
        return getSnapshotIds(query).thenApplyAsync(ids -> ids.stream()
            .map(id -> snapshotRepository.getCreatedUtc(id).join().orElse(null))
            .filter(Objects::nonNull)
            .collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<List<Instant>> getSnapshotCreationTimes(EntityId id) {
        return getSnapshotCreationTimes(asQuery(id));
    }

    @Override
    public CompletableFuture<List<Instant>> getSnapshotCreationTimesForUser(UUID userUUID) {
        return getSnapshotCreationTimes(asQuery(userUUID));
    }

    private CompletableFuture<Boolean> deleteSnapshot(Function<? super StoreTransaction, ? extends Iterable<Entity>> query, Predicate<? super EntityId> idPredicate) {
        return CompletableFuture.supplyAsync(() ->
            getDataStoreContext().getDataStore().computeInTransaction(txn -> {
                Iterator<Entity> iterator = query.apply(txn).iterator();
                if (!iterator.hasNext()) {
                    return false;
                }
                Entity toEdit = iterator.next();
                Optional<List<EntityId>> optionalList = Mappable.deserialize(toEdit.getBlob("snapshotIds"));
                if (!optionalList.isPresent()) {
                    return false;
                }
                List<EntityId> ids = optionalList.get();
                EntityId toRemove = null;
                int indexToRemove = -1;
                int size = ids.size();
                for (int i = 0; i < size; i++) {
                    EntityId id = ids.get(i);
                    if (idPredicate.test(id)) {
                        indexToRemove = i;
                        toRemove = id;
                        break;
                    }
                }
                if (indexToRemove < 0) {
                    txn.abort();
                    return false;
                }
                ids.remove(indexToRemove);
                try {
                    toEdit.setBlob("snapshotIds", new ByteArraySizedInputStream(Mappable.serializeUnsafe(ids)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return txn.commit() && snapshotRepository.deleteOne(toRemove).join();
            })
        );
    }

    @Override
    public CompletableFuture<Boolean> deleteSnapshot(Function<? super StoreTransaction, ? extends Iterable<Entity>> query, EntityId snapshotId) {
        return deleteSnapshot(query, snapshotId::equals);
    }

    @Override
    public CompletableFuture<Boolean> deleteSnapshot(Function<? super StoreTransaction, ? extends Iterable<Entity>> query, Instant createdUtc) {
        return deleteSnapshot(query, id -> getSnapshot(query, createdUtc).join().map(s -> s.getId().equals(id)).orElse(false));
    }

    @Override
    public CompletableFuture<Boolean> deleteSnapshot(EntityId id, EntityId snapshotId) {
        return deleteSnapshot(asQuery(id), snapshotId);
    }

    @Override
    public CompletableFuture<Boolean> deleteSnapshot(EntityId id, Instant createdUtc) {
        return deleteSnapshot(asQuery(id), createdUtc);
    }

    @Override
    public CompletableFuture<Boolean> deleteSnapshotForUser(UUID userUUID, EntityId snapshotId) {
        return deleteSnapshot(asQuery(userUUID), snapshotId);
    }

    @Override
    public CompletableFuture<Boolean> deleteSnapshotForUser(UUID userUUID, Instant createdUtc) {
        return deleteSnapshot(asQuery(userUUID), createdUtc);
    }

    @Override
    public CompletableFuture<Boolean> addSnapshot(Function<? super StoreTransaction, ? extends Iterable<Entity>> query, EntityId snapshotId) {
        return CompletableFuture.supplyAsync(() ->
            getDataStoreContext().getDataStore().computeInTransaction(txn -> {
                Iterator<Entity> iterator = query.apply(txn).iterator();
                if (!iterator.hasNext()) {
                    return false;
                }
                Entity toEdit = iterator.next();
                Optional<List<EntityId>> optionalList = Mappable.deserialize(toEdit.getBlob("snapshotIds"));
                if (!optionalList.isPresent()) {
                    return false;
                }
                List<EntityId> ids = optionalList.get();
                ids.add(snapshotId);
                try {
                    toEdit.setBlob("snapshotIds", new ByteArraySizedInputStream(Mappable.serializeUnsafe(ids)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return txn.commit();
            })
        );
    }

    @Override
    public CompletableFuture<Boolean> addSnapshot(EntityId id, EntityId snapshotId) {
        return addSnapshot(asQuery(id), snapshotId);
    }

    @Override
    public CompletableFuture<Boolean> addSnapshotForUser(UUID userUUID, EntityId snapshotId) {
        return addSnapshot(asQuery(userUUID), snapshotId);
    }

    @Override
    public CompletableFuture<Optional<Snapshot<EntityId>>> getSnapshot(Function<? super StoreTransaction, ? extends Iterable<Entity>> query, Instant createdUtc) {
        return CompletableFuture.supplyAsync(() ->
            getDataStoreContext().getDataStore().computeInReadonlyTransaction(txn -> {
                Iterator<Entity> iterator = query.apply(txn).iterator();
                if (!iterator.hasNext()) {
                    return Optional.empty();
                }
                Optional<List<EntityId>> optionalList = Mappable.deserialize(iterator.next().getBlob("snapshotIds"));
                if (!optionalList.isPresent()) {
                    return Optional.empty();
                }
                List<EntityId> ids = optionalList.get();
                for (EntityId id : ids) {
                    Optional<Snapshot<EntityId>> optionalSnapshot = snapshotRepository.getOne(id).join()
                        .filter(s -> checkInstant(s.getCreatedUtc(), createdUtc));
                    if (optionalSnapshot.isPresent()) {
                        return optionalSnapshot;
                    }
                }
                return Optional.empty();
            })
        );
    }

    @Override
    public CompletableFuture<Optional<Snapshot<EntityId>>> getSnapshot(EntityId id, Instant createdUtc) {
        return getSnapshot(asQuery(id), createdUtc);
    }

    @Override
    public CompletableFuture<Optional<Snapshot<EntityId>>> getSnapshotForUser(UUID userUUID, Instant createdUtc) {
        return getSnapshot(asQuery(userUUID), createdUtc);
    }

    @Override
    public CompletableFuture<List<EntityId>> getClosestSnapshots(Function<? super StoreTransaction, ? extends Iterable<Entity>> query, Instant createdUtc) {
        return null;
    }

    @Override
    public CompletableFuture<List<EntityId>> getClosestSnapshots(EntityId id, Instant createdUtc) {
        return null;
    }

    @Override
    public CompletableFuture<List<EntityId>> getClosestSnapshotsForUser(UUID userUUID, Instant createdUtc) {
        return null;
    }

    @Override
    public Function<? super StoreTransaction, ? extends Iterable<Entity>> asQuery(UUID userUUID) {
        return txn -> txn.find(getTClass().getSimpleName(), "userUUID", userUUID.toString());
    }

    @Override
    public CompletableFuture<Boolean> setSkipDeserialization(EntityId id, boolean skipDeserialization) {
        if (skipDeserialization) {
            return update(asQuery(id), e -> e.setProperty("skipDeserialization", true));
        }
        return update(asQuery(id), e -> e.deleteProperty("skipDeserialization"));
    }

    private static boolean checkInstant(Instant a, Instant b) {
        long aSeconds = a.getEpochSecond();
        long bSeconds = b.getEpochSecond();

        long aNanos = a.getNano();
        long bNanos = b.getNano();

        if (aSeconds == bSeconds && aNanos == bNanos) {
            return true;
        }

        // check if nanos is 0 for either
        // at least one input is already rounded
        // if neither input was rounded to begin with
        // dont round them
        return (aNanos == 0 || bNanos == 0) && aSeconds == bSeconds;
    }
}
