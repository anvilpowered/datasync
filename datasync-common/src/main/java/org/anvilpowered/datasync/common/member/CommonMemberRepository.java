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

import com.google.inject.Inject;
import org.anvilpowered.anvil.api.datastore.DataStoreContext;
import org.anvilpowered.anvil.api.model.ObjectWithId;
import org.anvilpowered.anvil.api.util.TimeFormatService;
import org.anvilpowered.anvil.base.datastore.BaseRepository;
import org.anvilpowered.datasync.api.member.MemberRepository;
import org.anvilpowered.datasync.api.model.member.Member;
import org.anvilpowered.datasync.api.model.snapshot.Snapshot;
import org.anvilpowered.datasync.api.snapshot.SnapshotRepository;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class CommonMemberRepository<
    TKey,
    TDataKey,
    TDataStore>
    extends BaseRepository<TKey, Member<TKey>, TDataStore>
    implements MemberRepository<TKey, TDataStore> {

    @Inject
    private Logger logger;
    @Inject
    protected SnapshotRepository<TKey, TDataKey, TDataStore> snapshotRepository;

    @Inject
    protected TimeFormatService timeFormatService;

    public CommonMemberRepository(DataStoreContext<TKey, TDataStore> dataStoreContext) {
        super(dataStoreContext);
    }

    @Override
    public CompletableFuture<Optional<Member<TKey>>> getOneOrGenerateForUser(UUID userUUID) {
        return getOneForUser(userUUID).thenApplyAsync(optionalMember -> {
            if (optionalMember.isPresent()) return optionalMember;
            // if there isn't one already, create a new one
            Member<TKey> member = generateEmpty();
            member.setUserUUID(userUUID);
            return insertOne(member).join();
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<Member<TKey>> getTClass() {
        return (Class<Member<TKey>>) getDataStoreContext().getEntityClassUnsafe("member");
    }

    @Override
    public CompletableFuture<Optional<TKey>> getIdForUser(UUID userUUID) {
        return CompletableFuture.supplyAsync(() ->
            getOneOrGenerateForUser(userUUID).join().map(ObjectWithId::getId));
    }

    @Override
    public CompletableFuture<Optional<UUID>> getUUID(TKey id) {
        return CompletableFuture.supplyAsync(() -> getOne(id).join().map(Member::getUserUUID));
    }

    @Override
    public CompletableFuture<List<TKey>> getSnapshotIdsForUser(UUID userUUID) {
        return getOneOrGenerateForUser(userUUID).thenApplyAsync(o ->
            o.map(Member::getSnapshotIds).orElse(Collections.emptyList()));
    }

    @Override
    public CompletableFuture<Optional<Snapshot<TKey>>> getSnapshot(TKey id,
                                                                   @Nullable String snapshot) {
        return CompletableFuture.supplyAsync(() -> {
            if (snapshot == null) {
                return getLatestSnapshot(id).join();
            }
            Optional<TKey> optionalId = parse(snapshot);
            if (optionalId.isPresent()) {
                return snapshotRepository.getOne(optionalId.get()).join();
            }
            Optional<Instant> date = timeFormatService.parseInstant(snapshot);
            if (date.isPresent()) {
                return getSnapshot(id, date.get()).join();
            }
            return Optional.empty();
        });
    }

    @Override
    public CompletableFuture<Optional<Snapshot<TKey>>> getSnapshotForUser(
        UUID userUUID, @Nullable String snapshot) {
        return getIdForUser(userUUID).thenApplyAsync(o ->
            o.flatMap(id -> getSnapshot(id, snapshot).join()));
    }

    @Override
    public CompletableFuture<Optional<Snapshot<TKey>>> getLatestSnapshot(TKey id) {
        return getOne(id).thenApplyAsync(optionalMember ->
            optionalMember.flatMap(member -> {
                int size = member.getSnapshotIds().size();
                if (size == 0) {
                    return Optional.empty();
                }
                return snapshotRepository.getOne(member.getSnapshotIds().get(size - 1)).join();
            })
        );
    }

    @Override
    public CompletableFuture<Optional<Snapshot<TKey>>> getLatestSnapshotForUser(UUID userUUID) {
        return getOneOrGenerateForUser(userUUID).thenApplyAsync(om -> om.flatMap(member -> {
            if (member.isSkipDeserialization()) {
                logger.info("Skipping deserialization for user " + userUUID + " because it was " +
                    "set to true in the DB. It will now be set to false.");
                setSkipDeserialization(member.getId(), false).join();
                return Optional.empty();
            }
            final int snapshotCount = member.getSnapshotIds().size();
            if (snapshotCount == 0) {
                return Optional.empty();
            }
            return snapshotRepository.getOne(member.getSnapshotIds().get(snapshotCount - 1))
                .join();
        }));
    }

    @Override
    public CompletableFuture<Optional<Snapshot<TKey>>> getPrevious(TKey id, TKey snapshotId) {
        return getOne(id).thenApplyAsync(optionalMember -> optionalMember.flatMap(member -> {
            int size = member.getSnapshotIds().size();
            for (int i = 0; i < size; i++) {
                if (member.getSnapshotIds().get(i).equals(snapshotId)) {
                    if (i == 0) {
                        return Optional.empty();
                    }
                    return snapshotRepository.getOne(member.getSnapshotIds().get(i - 1)).join();
                }
            }
            return Optional.empty();
        }));
    }

    @Override
    public CompletableFuture<Optional<Snapshot<TKey>>> getPreviousForUser(UUID userUUID,
                                                                          TKey snapshotId) {
        return getIdForUser(userUUID)
            .thenApplyAsync(o ->o.flatMap(id -> getPrevious(id, snapshotId).join()));
    }

    @Override
    public CompletableFuture<Optional<Snapshot<TKey>>> getPreviousForUser(UUID userUUID,
                                                                          Instant createdUtc) {
        return getSnapshotForUser(userUUID, createdUtc)
            .thenApplyAsync(o -> o.flatMap(s -> getPreviousForUser(userUUID, s.getId()).join()));
    }

    @Override
    public CompletableFuture<Optional<Snapshot<TKey>>> getNext(TKey id, TKey snapshotId) {
        return getOne(id).thenApplyAsync(optionalMember -> optionalMember.flatMap(member -> {
            int size = member.getSnapshotIds().size();
            for (int i = 0; i < size; i++) {
                if (member.getSnapshotIds().get(i).equals(snapshotId)) {
                    if (i == size - 1) {
                        return Optional.empty();
                    }
                    return snapshotRepository.getOne(member.getSnapshotIds().get(i + 1)).join();
                }
            }
            return Optional.empty();
        }));
    }

    @Override
    public CompletableFuture<Optional<Snapshot<TKey>>> getNextForUser(UUID userUUID,
                                                                      TKey snapshotId) {
        return getIdForUser(userUUID)
            .thenApplyAsync(o -> o.flatMap(id -> getNext(id, snapshotId).join()));
    }

    @Override
    public CompletableFuture<Optional<Snapshot<TKey>>> getNextForUser(UUID userUUID,
                                                                      Instant createdUtc) {
        return getSnapshotForUser(userUUID, createdUtc)
            .thenApplyAsync(o -> o.flatMap(s -> getNextForUser(userUUID, s.getId()).join()));
    }
}
