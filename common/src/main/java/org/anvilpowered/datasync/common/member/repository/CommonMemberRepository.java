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

package org.anvilpowered.datasync.common.member.repository;

import com.google.inject.Inject;
import org.anvilpowered.anvil.api.datastore.DataStoreContext;
import org.anvilpowered.anvil.api.model.ObjectWithId;
import org.anvilpowered.anvil.api.util.TimeFormatService;
import org.anvilpowered.anvil.base.repository.BaseRepository;
import org.anvilpowered.datasync.api.member.repository.MemberRepository;
import org.anvilpowered.datasync.api.model.member.Member;
import org.anvilpowered.datasync.api.model.snapshot.Snapshot;
import org.anvilpowered.datasync.api.snapshot.repository.SnapshotRepository;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class CommonMemberRepository<
    TKey,
    TMember extends Member<TKey>,
    TSnapshot extends Snapshot<TKey>,
    TUser,
    TDataKey,
    TDataStore>
    extends BaseRepository<TKey, TMember, TDataStore>
    implements MemberRepository<TKey, TMember, TSnapshot, TUser, TDataStore> {

    @Inject
    protected SnapshotRepository<TKey, TSnapshot, TDataKey, TDataStore> snapshotRepository;

    @Inject
    protected TimeFormatService timeFormatService;

    public CommonMemberRepository(DataStoreContext<TKey, TDataStore> dataStoreContext) {
        super(dataStoreContext);
    }

    @Override
    public CompletableFuture<Optional<TMember>> getOneOrGenerateForUser(UUID userUUID) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<TMember> optionalMember = getOneForUser(userUUID).join();
                if (optionalMember.isPresent()) return optionalMember;
                // if there isn't one already, create a new one
                TMember member = generateEmpty();
                member.setUserUUID(userUUID);
                return insertOne(member).join();
            } catch (Exception e) {
                e.printStackTrace();
                return Optional.empty();
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<TMember> getTClass() {
        return (Class<TMember>) getDataStoreContext().getEntityClassUnsafe("member");
    }

    @Override
    public CompletableFuture<Optional<TKey>> getIdForUser(UUID userUUID) {
        return CompletableFuture.supplyAsync(() -> getOneOrGenerateForUser(userUUID).join().map(ObjectWithId::getId));
    }

    @Override
    public CompletableFuture<Optional<UUID>> getUUID(TKey id) {
        return CompletableFuture.supplyAsync(() -> getOne(id).join().map(Member::getUserUUID));
    }

    @Override
    public CompletableFuture<List<TKey>> getSnapshotIdsForUser(UUID userUUID) {
        return getOneOrGenerateForUser(userUUID).thenApplyAsync(o -> o.map(Member::getSnapshotIds).orElse(Collections.emptyList()));
    }

    @Override
    public CompletableFuture<Optional<TSnapshot>> getSnapshot(TKey id, Optional<String> optionalString) {
        return CompletableFuture.supplyAsync(() -> {
            if (optionalString.isPresent()) {

                Optional<TKey> optionalId = parse(optionalString);
                if (optionalId.isPresent()) {
                    return snapshotRepository.getOne(optionalId.get()).join();
                }

                Optional<Instant> date = timeFormatService.parseInstant(optionalString.get());
                if (date.isPresent()) {
                    return getSnapshot(id, date.get()).join();
                }

                return Optional.empty();
            } else {
                return getLatestSnapshot(id).join();
            }
        });
    }

    @Override
    public CompletableFuture<Optional<TSnapshot>> getSnapshotForUser(UUID userUUID, Optional<String> optionalString) {
        return getIdForUser(userUUID).thenApplyAsync(o -> o.flatMap(id -> getSnapshot(id, optionalString).join()));
    }

    @Override
    public CompletableFuture<Optional<TSnapshot>> getLatestSnapshot(TKey id) {
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
    public CompletableFuture<Optional<TSnapshot>> getLatestSnapshotForUser(UUID userUUID) {
        return getOneOrGenerateForUser(userUUID).thenApplyAsync(optionalMember -> optionalMember.flatMap(member -> snapshotRepository.getOne(member.getSnapshotIds().get(member.getSnapshotIds().size() - 1)).join()));
    }

    @Override
    public CompletableFuture<Optional<TSnapshot>> getPrevious(TKey id, TKey snapshotId) {
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
    public CompletableFuture<Optional<TSnapshot>> getPreviousForUser(UUID userUUID, TKey snapshotId) {
        return getIdForUser(userUUID).thenApplyAsync(o -> o.flatMap(id -> getPrevious(id, snapshotId).join()));
    }

    @Override
    public CompletableFuture<Optional<TSnapshot>> getPreviousForUser(UUID userUUID, Instant createdUtc) {
        return getSnapshotForUser(userUUID, createdUtc).thenApplyAsync(o -> o.flatMap(s -> getPreviousForUser(userUUID, s.getId()).join()));
    }

    @Override
    public CompletableFuture<Optional<TSnapshot>> getNext(TKey id, TKey snapshotId) {
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
    public CompletableFuture<Optional<TSnapshot>> getNextForUser(UUID userUUID, TKey snapshotId) {
        return getIdForUser(userUUID).thenApplyAsync(o -> o.flatMap(id -> getNext(id, snapshotId).join()));
    }

    @Override
    public CompletableFuture<Optional<TSnapshot>> getNextForUser(UUID userUUID, Instant createdUtc) {
        return getSnapshotForUser(userUUID, createdUtc).thenApplyAsync(o -> o.flatMap(s -> getNextForUser(userUUID, s.getId()).join()));
    }
}
