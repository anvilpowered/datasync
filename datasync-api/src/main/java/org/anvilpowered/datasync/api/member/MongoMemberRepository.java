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

package org.anvilpowered.datasync.api.member;

import dev.morphia.Datastore;
import dev.morphia.query.Query;
import org.anvilpowered.anvil.api.datastore.MongoRepository;
import org.anvilpowered.datasync.api.model.member.Member;
import org.anvilpowered.datasync.api.model.snapshot.Snapshot;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface MongoMemberRepository
    extends MemberRepository<ObjectId, Datastore>,
    MongoRepository<Member<ObjectId>> {

    CompletableFuture<List<ObjectId>> getSnapshotIds(Query<Member<ObjectId>> query);

    CompletableFuture<List<Instant>> getSnapshotCreationTimes(Query<Member<ObjectId>> query);

    CompletableFuture<Boolean> deleteSnapshot(Query<Member<ObjectId>> query, ObjectId snapshotId);

    CompletableFuture<Boolean> deleteSnapshot(Query<Member<ObjectId>> query, Instant createdUtc);

    CompletableFuture<Boolean> addSnapshot(Query<Member<ObjectId>> query, ObjectId snapshotId);

    CompletableFuture<Optional<Snapshot<ObjectId>>> getSnapshot(Query<Member<ObjectId>> query, Instant createdUtc);

    CompletableFuture<List<ObjectId>> getClosestSnapshots(Query<Member<ObjectId>> query, Instant createdUtc);

    Query<Member<ObjectId>> asQuery(UUID userUUID);
}
