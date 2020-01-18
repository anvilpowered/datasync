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

package rocks.milspecsg.msdatasync.common.member.repository;

import com.google.inject.Inject;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.QueryResults;
import org.mongodb.morphia.query.UpdateOperations;
import rocks.milspecsg.msdatasync.api.member.repository.MongoMemberRepository;
import rocks.milspecsg.msdatasync.api.model.member.Member;
import rocks.milspecsg.msdatasync.api.model.snapshot.Snapshot;
import rocks.milspecsg.msrepository.api.datastore.DataStoreContext;
import rocks.milspecsg.msrepository.common.repository.CommonMongoRepository;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class CommonMongoMemberRepository<
    TMember extends Member<ObjectId>,
    TSnapshot extends Snapshot<ObjectId>,
    TUser,
    TDataKey>
    extends CommonMemberRepository<ObjectId, TMember, TSnapshot, TUser, TDataKey, Datastore>
    implements CommonMongoRepository<TMember>,
    MongoMemberRepository<TMember, TSnapshot, TUser> {

    @Inject
    public CommonMongoMemberRepository(DataStoreContext<ObjectId, Datastore> dataStoreContext) {
        super(dataStoreContext);
    }

    @Override
    public CompletableFuture<Optional<TMember>> getOneForUser(UUID userUUID) {
        return CompletableFuture.supplyAsync(() -> Optional.ofNullable(asQuery(userUUID).get()));
    }

    @Override
    public Query<TMember> asQuery(UUID userUUID) {
        return asQuery().field("userUUID").equal(userUUID);
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
        return getSnapshotIds(asQuery(id));
    }

    @Override
    public CompletableFuture<List<Date>> getSnapshotDates(Query<TMember> query) {
        return getSnapshotIds(query).thenApplyAsync(objectIds -> objectIds.stream().map(ObjectId::getDate).collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<List<Date>> getSnapshotDates(ObjectId id) {
        return getSnapshotDates(asQuery(id));
    }

    @Override
    public CompletableFuture<List<Date>> getSnapshotDatesForUser(UUID userUUID) {
        return getSnapshotDates(asQuery(userUUID));
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
        return CompletableFuture.supplyAsync(() -> update(query, createUpdateOperations().removeAll("snapshotIds", snapshotId))).join();
    }

    @Override
    public CompletableFuture<Boolean> deleteSnapshot(ObjectId id, ObjectId snapshotId) {
        return deleteSnapshot(asQuery(id), snapshotId);
    }

    @Override
    public CompletableFuture<Boolean> deleteSnapshot(ObjectId id, Date date) {
        return deleteSnapshot(asQuery(id), date);
    }

    @Override
    public CompletableFuture<Boolean> deleteSnapshotForUser(UUID userUUID, ObjectId snapshotId) {
        return deleteSnapshot(asQuery(userUUID), snapshotId);
    }

    @Override
    public CompletableFuture<Boolean> deleteSnapshotForUser(UUID userUUID, Date date) {
        return deleteSnapshot(asQuery(userUUID), date);
    }

    @Override
    public CompletableFuture<Boolean> addSnapshot(Query<TMember> query, ObjectId snapshotId) {
        return CompletableFuture.supplyAsync(() -> update(query, createUpdateOperations().set("snapshotIds", snapshotId)));
    }

    @Override
    public CompletableFuture<Boolean> addSnapshot(ObjectId id, ObjectId snapshotId) {
        return addSnapshot(asQuery(id), snapshotId);
    }

    @Override
    public CompletableFuture<Boolean> addSnapshotForUser(UUID userUUID, ObjectId snapshotId) {
        return getOneOrGenerateForUser(userUUID).thenApplyAsync(optionalMember -> {
            if (!optionalMember.isPresent()) {
                return false;
            }
            return addSnapshot(asQuery(userUUID), snapshotId).join();
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
        return getSnapshot(asQuery(id), date);
    }

    @Override
    public CompletableFuture<Optional<TSnapshot>> getSnapshotForUser(UUID userUUID, Date date) {
        return getSnapshot(asQuery(userUUID), date);
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
        return getClosestSnapshots(asQuery(id), date);
    }

    @Override
    public CompletableFuture<List<ObjectId>> getClosestSnapshotsForUser(UUID userUUID, Date date) {
        return getClosestSnapshots(asQuery(userUUID), date);
    }
}
