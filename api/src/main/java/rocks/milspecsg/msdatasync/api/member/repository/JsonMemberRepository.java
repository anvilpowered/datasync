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

import io.jsondb.JsonDBOperations;
import rocks.milspecsg.msdatasync.model.core.member.Member;
import rocks.milspecsg.msdatasync.model.core.snapshot.Snapshot;
import rocks.milspecsg.msrepository.api.cache.CacheService;
import rocks.milspecsg.msrepository.api.repository.JsonRepository;
import rocks.milspecsg.msrepository.datastore.json.JsonConfig;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface JsonMemberRepository<
    TMember extends Member<UUID>,
    TSnapshot extends Snapshot<UUID>,
    TUser>
    extends MemberRepository<UUID, TMember, TSnapshot, TUser, JsonDBOperations, JsonConfig>,
    JsonRepository<TMember, CacheService<UUID, TMember, JsonDBOperations, JsonConfig>> {

    CompletableFuture<List<UUID>> getSnapshotIds(String query);

    CompletableFuture<Boolean> deleteSnapshot(String query, UUID snapshotId);

    CompletableFuture<Boolean> deleteSnapshot(String query, Date date);

    CompletableFuture<Boolean> addSnapshot(String query, UUID snapshotId);

    CompletableFuture<Optional<TSnapshot>> getSnapshot(String query, Date date);

    CompletableFuture<List<UUID>> getClosestSnapshots(String query, Date date);

    String asQueryForUser(UUID userUUID);
}
