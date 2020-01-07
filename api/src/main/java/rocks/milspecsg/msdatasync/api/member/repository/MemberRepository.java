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

import rocks.milspecsg.msdatasync.model.core.member.Member;
import rocks.milspecsg.msdatasync.model.core.snapshot.Snapshot;
import rocks.milspecsg.msrepository.api.cache.CacheService;
import rocks.milspecsg.msrepository.api.repository.Repository;
import rocks.milspecsg.msrepository.datastore.DataStoreConfig;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface MemberRepository<
    TKey,
    TMember extends Member<TKey>,
    TSnapshot extends Snapshot<TKey>,
    TUser,
    TDataStore,
    TDataStoreConfig extends DataStoreConfig>
    extends Repository<TKey, TMember, CacheService<TKey, TMember, TDataStore, TDataStoreConfig>, TDataStore, TDataStoreConfig>  {

    /**
     * Gets the corresponding {@code Member} from the database.
     * If not present, creates a new one and saves it to the database
     *
     * @param userUUID Mojang issued {@code uuid} of {@code User} to getRequiredRankIndex corresponding {@code Member}
     * @return a ready-to-use {@code Member} that corresponds with the given {@code uuid}
     */
    CompletableFuture<Optional<TMember>> getOneOrGenerateForUser(UUID userUUID);

    CompletableFuture<Optional<TMember>> getOneForUser(UUID userUUID);

    CompletableFuture<Optional<TKey>> getIdForUser(UUID userUUID);

    CompletableFuture<Optional<UUID>> getUUID(TKey id);

    CompletableFuture<List<TKey>> getSnapshotIds(TKey id);

    CompletableFuture<List<TKey>> getSnapshotIdsForUser(UUID userUUID);

    CompletableFuture<List<Date>> getSnapshotDates(TKey id);

    CompletableFuture<List<Date>> getSnapshotDatesForUser(UUID userUUID);

    CompletableFuture<Boolean> deleteSnapshot(TKey id, TKey snapshotId);

    CompletableFuture<Boolean> deleteSnapshot(TKey id, Date date);

    CompletableFuture<Boolean> deleteSnapshotForUser(UUID userUUID, TKey snapshotId);

    CompletableFuture<Boolean> deleteSnapshotForUser(UUID userUUID, Date date);

    CompletableFuture<Boolean> addSnapshot(TKey id, TKey snapshotId);

    CompletableFuture<Boolean> addSnapshotForUser(UUID userUUID, TKey snapshotId);

    CompletableFuture<Optional<TSnapshot>> getSnapshot(TKey id, Date date);

    CompletableFuture<Optional<TSnapshot>> getSnapshotForUser(UUID userUUID, Date date);

    CompletableFuture<Optional<TSnapshot>> getSnapshot(TKey id, Optional<String> optionalString);

    CompletableFuture<Optional<TSnapshot>> getSnapshotForUser(UUID userUUID, Optional<String> optionalString);

    CompletableFuture<List<TKey>> getClosestSnapshots(TKey id, Date date);

    CompletableFuture<List<TKey>> getClosestSnapshotsForUser(UUID userUUID, Date date);

    CompletableFuture<Optional<TSnapshot>> getLatestSnapshot(TKey id);

    CompletableFuture<Optional<TSnapshot>> getLatestSnapshotForUser(UUID userUUID);

    CompletableFuture<Optional<TSnapshot>> getPrevious(TKey id, TKey snapshotId);

    CompletableFuture<Optional<TSnapshot>> getPreviousForUser(UUID userUUID, TKey snapshotId);

    CompletableFuture<Optional<TSnapshot>> getPreviousForUser(UUID userUUID, Date date);

    CompletableFuture<Optional<TSnapshot>> getNext(TKey id, TKey snapshotId);

    CompletableFuture<Optional<TSnapshot>> getNextForUser(UUID userUUID, TKey snapshotId);

    CompletableFuture<Optional<TSnapshot>> getNextForUser(UUID userUUID, Date date);

    //TODO: move this method declaration to ApiRepository in repo MSRepository
//    Optional<Member> getNext();

}