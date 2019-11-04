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

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import rocks.milspecsg.msdatasync.model.core.member.Member;
import rocks.milspecsg.msdatasync.model.core.snapshot.Snapshot;
import rocks.milspecsg.msrepository.datastore.mongodb.MongoConfig;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface MongoMemberRepository<
    TMember extends Member<ObjectId>,
    TSnapshot extends Snapshot<ObjectId>,
    TUser>
    extends MemberRepository<ObjectId, TMember, TSnapshot, TUser, Datastore, MongoConfig> {

    CompletableFuture<List<ObjectId>> getSnapshotIds(Query<TMember> query);

    CompletableFuture<List<Date>> getSnapshotDates(Query<TMember> query);

    CompletableFuture<Boolean> deleteSnapshot(Query<TMember> query, ObjectId snapshotId);

    CompletableFuture<Boolean> deleteSnapshot(Query<TMember> query, Date date);

    CompletableFuture<Boolean> addSnapshot(Query<TMember> query, ObjectId snapshotId);

    CompletableFuture<Optional<TSnapshot>> getSnapshot(Query<TMember> query, Date date);

    CompletableFuture<List<ObjectId>> getClosestSnapshots(Query<TMember> query, Date date);

    Optional<Query<TMember>> asQuery(UUID userUUID);
}
