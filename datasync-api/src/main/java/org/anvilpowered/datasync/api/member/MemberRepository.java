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

import org.anvilpowered.anvil.api.datastore.Repository;
import org.anvilpowered.datasync.api.model.member.Member;
import org.anvilpowered.datasync.api.model.snapshot.Snapshot;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface MemberRepository<
    TKey,
    TDataStore>
    extends Repository<TKey, Member<TKey>, TDataStore> {

    /**
     * Gets the corresponding {@code Member} from the database.
     * If not present, creates a new one and saves it to the database
     *
     * @param userUUID Mojang issued {@code uuid} of {@code User} to getRequiredRankIndex corresponding {@code Member}
     * @return a ready-to-use {@code Member} that corresponds with the given {@code uuid}
     */
    CompletableFuture<Optional<Member<TKey>>> getOneOrGenerateForUser(UUID userUUID);

    CompletableFuture<Optional<Member<TKey>>> getOneForUser(UUID userUUID);

    CompletableFuture<Optional<TKey>> getIdForUser(UUID userUUID);

    CompletableFuture<Optional<UUID>> getUUID(TKey id);

    CompletableFuture<List<TKey>> getSnapshotIds(TKey id);

    CompletableFuture<List<TKey>> getSnapshotIdsForUser(UUID userUUID);

    CompletableFuture<List<Instant>> getSnapshotCreationTimes(TKey id);

    CompletableFuture<List<Instant>> getSnapshotCreationTimesForUser(UUID userUUID);

    CompletableFuture<Boolean> deleteSnapshot(TKey id, TKey snapshotId);

    CompletableFuture<Boolean> deleteSnapshot(TKey id, Instant createdUtc);

    CompletableFuture<Boolean> deleteSnapshotForUser(UUID userUUID, TKey snapshotId);

    CompletableFuture<Boolean> deleteSnapshotForUser(UUID userUUID, Instant createdUtc);

    CompletableFuture<Boolean> addSnapshot(TKey id, TKey snapshotId);

    CompletableFuture<Boolean> addSnapshotForUser(UUID userUUID, TKey snapshotId);

    CompletableFuture<Optional<Snapshot<TKey>>> getSnapshot(TKey id, Instant createdUtc);

    CompletableFuture<Optional<Snapshot<TKey>>> getSnapshotForUser(UUID userUUID, Instant createdUtc);

    CompletableFuture<Optional<Snapshot<TKey>>> getSnapshot(TKey id, @Nullable String snapshot);

    CompletableFuture<Optional<Snapshot<TKey>>> getSnapshotForUser(UUID userUUID,
                                                                   @Nullable String snapshot);

    CompletableFuture<List<TKey>> getClosestSnapshots(TKey id, Instant createdUtc);

    CompletableFuture<List<TKey>> getClosestSnapshotsForUser(UUID userUUID, Instant createdUtc);

    CompletableFuture<Optional<Snapshot<TKey>>> getLatestSnapshot(TKey id);

    CompletableFuture<Optional<Snapshot<TKey>>> getLatestSnapshotForUser(UUID userUUID);

    CompletableFuture<Optional<Snapshot<TKey>>> getPrevious(TKey id, TKey snapshotId);

    CompletableFuture<Optional<Snapshot<TKey>>> getPreviousForUser(UUID userUUID, TKey snapshotId);

    CompletableFuture<Optional<Snapshot<TKey>>> getPreviousForUser(UUID userUUID, Instant createdUtc);

    CompletableFuture<Optional<Snapshot<TKey>>> getNext(TKey id, TKey snapshotId);

    CompletableFuture<Optional<Snapshot<TKey>>> getNextForUser(UUID userUUID, TKey snapshotId);

    CompletableFuture<Optional<Snapshot<TKey>>> getNextForUser(UUID userUUID, Instant createdUtc);

    CompletableFuture<Boolean> setSkipDeserialization(TKey id, boolean skipDeserialization);
}
