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

package rocks.milspecsg.msdatasync.service.common.member.repository;

import com.google.inject.Inject;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.QueryResults;
import org.mongodb.morphia.query.UpdateOperations;
import rocks.milspecsg.msdatasync.api.member.repository.MongoMemberRepository;
import rocks.milspecsg.msdatasync.model.core.member.Member;
import rocks.milspecsg.msdatasync.model.core.snapshot.Snapshot;
import rocks.milspecsg.msrepository.api.cache.CacheService;
import rocks.milspecsg.msrepository.datastore.DataStoreContext;
import rocks.milspecsg.msrepository.datastore.mongodb.MongoConfig;
import rocks.milspecsg.msrepository.service.common.repository.CommonMongoRepository;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class CommonMongoMemberRepository<
    TMember extends Member<ObjectId>,
    TSnapshot extends Snapshot<ObjectId>,
    TUser,
    TDataKey>
    extends CommonMemberRepository<ObjectId, TMember, TSnapshot, TUser, TDataKey, Datastore, MongoConfig>
    implements CommonMongoRepository<TMember, CacheService<ObjectId, TMember, Datastore, MongoConfig>>,
    MongoMemberRepository<TMember, TSnapshot, TUser> {

    @Inject
    public CommonMongoMemberRepository(DataStoreContext<ObjectId, Datastore, MongoConfig> dataStoreContext) {
        super(dataStoreContext);
    }

    @Override
    public CompletableFuture<Optional<TMember>> getOneForUser(UUID userUUID) {
        return CompletableFuture.supplyAsync(() -> asQuery(userUUID).map(QueryResults::get));
    }

    @Override
    public Optional<Query<TMember>> asQuery(UUID userUUID) {
        return asQuery().map(q -> q.field("userUUID").equal(userUUID));
    }

    @Override
    public CompletableFuture<List<ObjectId>> getSnapshotIds(Query<TMember> query) {
        return CompletableFuture.supplyAsync(() -> {
            Member<ObjectId> member = query.project("snapshotIds", true).get();
            if (member == null) {
                return Collections.emptyList();
            }
            return member.getSnapshotIds();
        });
    }

    @Override
    public CompletableFuture<List<ObjectId>> getSnapshotIds(ObjectId id) {
        return asQuery(id).map(this::getSnapshotIds).orElse(CompletableFuture.completedFuture(Collections.emptyList()));
    }

    @Override
    public CompletableFuture<List<Date>> getSnapshotDates(Query<TMember> query) {
        return getSnapshotIds(query).thenApplyAsync(objectIds -> objectIds.stream().map(ObjectId::getDate).collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<List<Date>> getSnapshotDates(ObjectId id) {
        return asQuery(id).map(this::getSnapshotDates).orElse(CompletableFuture.completedFuture(Collections.emptyList()));
    }

    @Override
    public CompletableFuture<List<Date>> getSnapshotDatesForUser(UUID userUUID) {
        return asQuery(userUUID).map(this::getSnapshotDates).orElse(CompletableFuture.completedFuture(Collections.emptyList()));
    }

    @Override
    public CompletableFuture<Boolean> deleteSnapshot(Query<TMember> query, ObjectId snapshotId) {
        return CompletableFuture.supplyAsync(() -> removeSnapshotId(query, snapshotId) && snapshotRepository.deleteOne(snapshotId).join());
    }

    @Override
    public CompletableFuture<Boolean> deleteSnapshot(Query<TMember> query, Date date) {
        return getSnapshotIds(query)
            .thenApplyAsync(objectIds -> objectIds.stream()
                .filter(objectId -> objectId.getDate().equals(date))
                .findFirst()
                .map(snapshotId -> removeSnapshotId(query, snapshotId) && snapshotRepository.deleteOne(snapshotId).join())
                .orElse(false)
            );
    }

    private boolean removeSnapshotId(Query<TMember> query, ObjectId snapshotId) {
        Optional<UpdateOperations<TMember>> updateOperations = createUpdateOperations().map(u -> u.removeAll("snapshotIds", snapshotId));
        return updateOperations.isPresent() && getDataStoreContext().getDataStore().map(dataStore -> dataStore.update(query, updateOperations.get()).getUpdatedCount() > 0).orElse(false);
    }

    @Override
    public CompletableFuture<Boolean> deleteSnapshot(ObjectId id, ObjectId snapshotId) {
        return asQuery(id).map(q -> deleteSnapshot(q, snapshotId)).orElse(CompletableFuture.completedFuture(false));
    }

    @Override
    public CompletableFuture<Boolean> deleteSnapshot(ObjectId id, Date date) {
        return asQuery(id).map(q -> deleteSnapshot(id, date)).orElse(CompletableFuture.completedFuture(false));
    }

    @Override
    public CompletableFuture<Boolean> deleteSnapshotForUser(UUID userUUID, ObjectId snapshotId) {
        return asQuery(userUUID).map(q -> deleteSnapshot(q, snapshotId)).orElse(CompletableFuture.completedFuture(false));
    }

    @Override
    public CompletableFuture<Boolean> deleteSnapshotForUser(UUID userUUID, Date date) {
        return asQuery(userUUID).map(q -> deleteSnapshot(q, date)).orElse(CompletableFuture.completedFuture(false));
    }

    @Override
    public CompletableFuture<Boolean> addSnapshot(Query<TMember> query, ObjectId snapshotId) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<UpdateOperations<TMember>> updateOperations = createUpdateOperations().map(u -> u.addToSet("snapshotIds", snapshotId));
            return updateOperations
                .map(memberUpdateOperations -> getDataStoreContext().getDataStore()
                    .map(dataStore -> dataStore.update(query, memberUpdateOperations).getUpdatedCount() > 0).orElse(false)
                ).orElse(false);
        });
    }

    @Override
    public CompletableFuture<Boolean> addSnapshot(ObjectId id, ObjectId snapshotId) {
        return asQuery(id).map(q -> addSnapshot(q, snapshotId)).orElse(CompletableFuture.completedFuture(false));
    }

    @Override
    public CompletableFuture<Boolean> addSnapshotForUser(UUID userUUID, ObjectId snapshotId) {
        return getOneOrGenerateForUser(userUUID).thenApplyAsync(optionalMember -> {
            if (!optionalMember.isPresent()) {
                return false;
            }
            return asQuery(userUUID).map(q -> addSnapshot(q, snapshotId).join()).orElse(false);
        });
    }

    @Override
    public CompletableFuture<Optional<TSnapshot>> getSnapshot(Query<TMember> query, Date date) {
        return getSnapshotIds(query)
            .thenApplyAsync(objectIds -> objectIds.stream()
                .filter(objectId -> objectId.getDate().equals(date))
                .findFirst()
                .flatMap(objectId -> snapshotRepository.getOne(objectId).join())
            );
    }

    @Override
    public CompletableFuture<Optional<TSnapshot>> getSnapshot(ObjectId id, Date date) {
        return asQuery(id).map(q -> getSnapshot(q, date)).orElse(CompletableFuture.completedFuture(Optional.empty()));
    }

    @Override
    public CompletableFuture<Optional<TSnapshot>> getSnapshotForUser(UUID userUUID, Date date) {
        return asQuery(userUUID).map(q -> getSnapshot(q, date)).orElse(CompletableFuture.completedFuture(Optional.empty()));
    }

    @Override
    public CompletableFuture<List<ObjectId>> getClosestSnapshots(Query<TMember> query, Date date) {
        return getSnapshotIds(query).thenApplyAsync(objectIds -> {
            Optional<ObjectId> closestBefore = Optional.empty();
            Optional<ObjectId> closestAfter = Optional.empty();
            Optional<ObjectId> same = Optional.empty();

            for (ObjectId objectId : objectIds) {
                Date toTest = objectId.getDate();

                if (toTest.equals(date)) {
                    same = Optional.of(objectId);
                } else if (toTest.before(date) && (!closestBefore.isPresent() || toTest.after(closestBefore.get().getDate()))) {
                    closestBefore = Optional.of(objectId);
                } else if (toTest.after(date) && (!closestAfter.isPresent() || toTest.before(closestAfter.get().getDate()))) {
                    closestAfter = Optional.of(objectId);
                }
            }

            List<ObjectId> toReturn = new ArrayList<>();
            closestBefore.ifPresent(toReturn::add);
            same.ifPresent(toReturn::add);
            closestAfter.ifPresent(toReturn::add);
            return toReturn;
        });
    }

    @Override
    public CompletableFuture<List<ObjectId>> getClosestSnapshots(ObjectId id, Date date) {
        return asQuery(id).map(q -> getClosestSnapshots(q, date)).orElse(CompletableFuture.completedFuture(Collections.emptyList()));
    }

    @Override
    public CompletableFuture<List<ObjectId>> getClosestSnapshotsForUser(UUID userUUID, Date date) {
        return asQuery(userUUID).map(q -> getClosestSnapshots(q, date)).orElse(CompletableFuture.completedFuture(Collections.emptyList()));
    }
}
