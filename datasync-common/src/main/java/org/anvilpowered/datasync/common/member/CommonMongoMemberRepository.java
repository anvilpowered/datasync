/*
 *   DataSync - AnvilPowered
 *   Copyright (C) 2020 Cableguy20
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.datasync.common.member;

import org.anvilpowered.anvil.base.datastore.BaseMongoRepository;
import org.anvilpowered.datasync.api.member.MongoMemberRepository;
import org.anvilpowered.datasync.api.model.member.Member;
import org.anvilpowered.datasync.api.model.snapshot.Snapshot;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class CommonMongoMemberRepository<TDataKey>
    extends CommonMemberRepository<ObjectId, TDataKey, Datastore>
    implements BaseMongoRepository<Member<ObjectId>>,
    MongoMemberRepository {

    @Override
    public CompletableFuture<Optional<Member<ObjectId>>> getOneForUser(UUID userUUID) {
        return CompletableFuture.supplyAsync(() -> Optional.ofNullable(asQuery(userUUID).get()));
    }

    @Override
    public Query<Member<ObjectId>> asQuery(UUID userUUID) {
        return asQuery().field("userUUID").equal(userUUID);
    }

    @Override
    public CompletableFuture<List<ObjectId>> getSnapshotIds(Query<Member<ObjectId>> query) {
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
    public CompletableFuture<List<Instant>> getSnapshotCreationTimes(
        Query<Member<ObjectId>> query) {
        return getSnapshotIds(query).thenApplyAsync(objectIds -> objectIds.stream()
            .map(o -> Instant.ofEpochSecond(o.getTimestamp())).collect(Collectors.toList())
        );
    }

    @Override
    public CompletableFuture<List<Instant>> getSnapshotCreationTimes(ObjectId id) {
        return getSnapshotCreationTimes(asQuery(id));
    }

    @Override
    public CompletableFuture<List<Instant>> getSnapshotCreationTimesForUser(UUID userUUID) {
        return getSnapshotCreationTimes(asQuery(userUUID));
    }

    @Override
    public CompletableFuture<Boolean> deleteSnapshot(Query<Member<ObjectId>> query,
                                                     ObjectId snapshotId) {
        return CompletableFuture.supplyAsync(() -> removeSnapshotId(query, snapshotId)
                && snapshotRepository.deleteOne(snapshotId).join());
    }

    @Override
    public CompletableFuture<Boolean> deleteSnapshot(Query<Member<ObjectId>> query,
                                                     Instant createdUtc) {
        return getSnapshotIds(query)
            .thenApplyAsync(objectIds -> objectIds.stream()
                .filter(id -> Instant.ofEpochSecond(id.getTimestamp()).equals(createdUtc))
                .findFirst()
                .map(snapshotId -> removeSnapshotId(query, snapshotId)
                    && snapshotRepository.deleteOne(snapshotId).join()
                )
                .orElse(false)
            );
    }

    private boolean removeSnapshotId(Query<Member<ObjectId>> query, ObjectId snapshotId) {
        return getDataStoreContext().getDataStore()
            .update(query, createUpdateOperations().removeAll("snapshotIds", snapshotId))
            .getUpdatedCount() > 0;
    }

    @Override
    public CompletableFuture<Boolean> deleteSnapshot(ObjectId id, ObjectId snapshotId) {
        return deleteSnapshot(asQuery(id), snapshotId);
    }

    @Override
    public CompletableFuture<Boolean> deleteSnapshot(ObjectId id, Instant createdUtc) {
        return deleteSnapshot(asQuery(id), createdUtc);
    }

    @Override
    public CompletableFuture<Boolean> deleteSnapshotForUser(UUID userUUID, ObjectId snapshotId) {
        return deleteSnapshot(asQuery(userUUID), snapshotId);
    }

    @Override
    public CompletableFuture<Boolean> deleteSnapshotForUser(UUID userUUID, Instant createdUtc) {
        return deleteSnapshot(asQuery(userUUID), createdUtc);
    }

    @Override
    public CompletableFuture<Boolean> addSnapshot(Query<Member<ObjectId>> query,
                                                  ObjectId snapshotId) {
        return update(query, createUpdateOperations().addToSet("snapshotIds", snapshotId));
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
    public CompletableFuture<Optional<Snapshot<ObjectId>>> getSnapshot(
        Query<Member<ObjectId>> query, Instant createdUtc) {
        return getSnapshotIds(query)
            .thenApplyAsync(objectIds -> objectIds.stream()
                .filter(id -> Instant.ofEpochSecond(id.getTimestamp()).equals(createdUtc))
                .findFirst()
                .flatMap(objectId -> snapshotRepository.getOne(objectId).join())
            );
    }

    @Override
    public CompletableFuture<Optional<Snapshot<ObjectId>>> getSnapshot(ObjectId id,
                                                                       Instant createdUtc) {
        return getSnapshot(asQuery(id), createdUtc);
    }

    @Override
    public CompletableFuture<Optional<Snapshot<ObjectId>>> getSnapshotForUser(UUID userUUID,
                                                                              Instant createdUtc) {
        return getSnapshot(asQuery(userUUID), createdUtc);
    }

    @Override
    public CompletableFuture<List<ObjectId>> getClosestSnapshots(Query<Member<ObjectId>> query,
                                                                 Instant createdUtc) {
        final long seconds = createdUtc.getEpochSecond();
        return getSnapshotIds(query).thenApplyAsync(objectIds -> {
            Optional<ObjectId> closestBefore = Optional.empty();
            Optional<ObjectId> closestAfter = Optional.empty();
            Optional<ObjectId> same = Optional.empty();

            for (ObjectId objectId : objectIds) {
                long toTest = objectId.getTimestamp();

                if (toTest == seconds) {
                    same = Optional.of(objectId);
                } else if (toTest < seconds && (!closestBefore.isPresent()
                    || toTest > closestBefore.get().getTimestamp())) {
                    closestBefore = Optional.of(objectId);
                } else if (toTest > seconds && (!closestAfter.isPresent()
                    || toTest < closestAfter.get().getTimestamp())) {
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
    public CompletableFuture<List<ObjectId>> getClosestSnapshots(ObjectId id,
                                                                 Instant createdUtc) {
        return getClosestSnapshots(asQuery(id), createdUtc);
    }

    @Override
    public CompletableFuture<List<ObjectId>> getClosestSnapshotsForUser(UUID userUUID,
                                                                        Instant createdUtc) {
        return getClosestSnapshots(asQuery(userUUID), createdUtc);
    }

    @Override
    public CompletableFuture<Boolean> setSkipDeserialization(ObjectId id,
                                                             boolean skipDeserialization) {
        if (skipDeserialization) {
            return update(asQuery(id), set("skipDeserialization", true));
        }
        return update(asQuery(id), createUpdateOperations().unset("skipDeserialization"));
    }
}
