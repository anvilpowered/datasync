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

import org.anvilpowered.anvil.api.repository.MongoRepository;
import org.anvilpowered.datasync.api.model.member.Member;
import org.anvilpowered.datasync.api.model.snapshot.Snapshot;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface MongoMemberRepository<
    TMember extends Member<ObjectId>,
    TSnapshot extends Snapshot<ObjectId>,
    TUser>
    extends MemberRepository<ObjectId, TMember, TSnapshot, TUser, Datastore>,
    MongoRepository<TMember> {

    CompletableFuture<List<ObjectId>> getSnapshotIds(Query<TMember> query);

    CompletableFuture<List<Instant>> getSnapshotCreationTimes(Query<TMember> query);

    CompletableFuture<Boolean> deleteSnapshot(Query<TMember> query, ObjectId snapshotId);

    CompletableFuture<Boolean> deleteSnapshot(Query<TMember> query, Instant createdUtc);

    CompletableFuture<Boolean> addSnapshot(Query<TMember> query, ObjectId snapshotId);

    CompletableFuture<Optional<TSnapshot>> getSnapshot(Query<TMember> query, Instant createdUtc);

    CompletableFuture<List<ObjectId>> getClosestSnapshots(Query<TMember> query, Instant createdUtc);

    Query<TMember> asQuery(UUID userUUID);
}