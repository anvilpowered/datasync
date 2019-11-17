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

package rocks.milspecsg.msdatasync.service.common.member.repository;

import com.google.inject.Inject;
import org.bson.types.ObjectId;
import rocks.milspecsg.msdatasync.api.member.repository.MemberRepository;
import rocks.milspecsg.msdatasync.api.misc.DateFormatService;
import rocks.milspecsg.msdatasync.api.snapshot.repository.SnapshotRepository;
import rocks.milspecsg.msdatasync.model.core.member.Member;
import rocks.milspecsg.msdatasync.model.core.snapshot.Snapshot;
import rocks.milspecsg.msrepository.api.cache.RepositoryCacheService;
import rocks.milspecsg.msrepository.datastore.DataStoreConfig;
import rocks.milspecsg.msrepository.datastore.DataStoreContext;
import rocks.milspecsg.msrepository.model.data.dbo.ObjectWithId;
import rocks.milspecsg.msrepository.service.common.repository.CommonRepository;

import java.text.ParseException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

public abstract class CommonMemberRepository<
    TKey,
    TMember extends Member<TKey>,
    TSnapshot extends Snapshot<TKey>,
    TUser,
    TDataKey,
    TDataStore,
    TDataStoreConfig extends DataStoreConfig>
    extends CommonRepository<TKey, TMember, RepositoryCacheService<TKey, TMember, TDataStore, TDataStoreConfig>, TDataStore, TDataStoreConfig>
    implements MemberRepository<TKey, TMember, TSnapshot, TUser, TDataStore, TDataStoreConfig> {

    @Inject
    protected SnapshotRepository<TKey, TSnapshot, TDataKey, TDataStore, TDataStoreConfig> snapshotRepository;

    @Inject
    protected DateFormatService dateFormatService;

    public CommonMemberRepository(DataStoreContext<TKey, TDataStore, TDataStoreConfig> dataStoreContext) {
        super(dataStoreContext);
    }

    @Override
    public CompletableFuture<Optional<TMember>> getOneOrGenerate(UUID userUUID) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<TMember> optionalMember = getOne(userUUID).join();
            if (optionalMember.isPresent()) return optionalMember;
            // if there isn't one already, create a new one
            TMember member = generateEmpty();
            member.setUserUUID(userUUID);
            return insertOne(member).join();
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<TMember> getTClass() {
        return (Class<TMember>) getDataStoreContext().getEntityClassUnsafe("member");
    }

    @Override
    public CompletableFuture<Optional<TKey>> getId(UUID userUUID) {
        return CompletableFuture.supplyAsync(() -> getOneOrGenerate(userUUID).join().map(ObjectWithId::getId));
    }

    @Override
    public CompletableFuture<Optional<UUID>> getUUID(TKey id) {
        return CompletableFuture.supplyAsync(() -> getOne(id).join().map(Member::getUserUUID));
    }

    @Override
    public CompletableFuture<Optional<TSnapshot>> getSnapshot(TKey id, Optional<String> optionalString) {
        return CompletableFuture.supplyAsync(() -> {
            if (optionalString.isPresent()) {

                Optional<TKey> optionalId = parse(optionalString);
                if (optionalId.isPresent()) {
                    return snapshotRepository.getOne(optionalId.get()).join();
                }

                Optional<Date> date = dateFormatService.parse(optionalString.get());
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
    public CompletableFuture<Optional<TSnapshot>> getSnapshot(UUID userUUID, Optional<String> optionalString) {
        return getId(userUUID).thenApplyAsync(o -> o.flatMap(id -> getSnapshot(id, optionalString).join()));
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
    public CompletableFuture<Optional<TSnapshot>> getLatestSnapshot(UUID userUUID) {
        return getOne(userUUID).thenApplyAsync(optionalMember -> optionalMember.flatMap(member -> snapshotRepository.getOne(member.getSnapshotIds().get(member.getSnapshotIds().size() - 1)).join()));
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
    public CompletableFuture<Optional<TSnapshot>> getPrevious(UUID userUUID, TKey snapshotId) {
        return getId(userUUID).thenApplyAsync(o -> o.flatMap(id -> getPrevious(id, snapshotId).join()));
    }

    @Override
    public CompletableFuture<Optional<TSnapshot>> getPrevious(UUID userUUID, Date date) {
        return getSnapshot(userUUID, date).thenApplyAsync(o -> o.flatMap(s -> getPrevious(userUUID, s.getId()).join()));
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
    public CompletableFuture<Optional<TSnapshot>> getNext(UUID userUUID, TKey snapshotId) {
        return getId(userUUID).thenApplyAsync(o -> o.flatMap(id -> getNext(id, snapshotId).join()));
    }

    @Override
    public CompletableFuture<Optional<TSnapshot>> getNext(UUID userUUID, Date date) {
        return getSnapshot(userUUID, date).thenApplyAsync(o -> o.flatMap(s -> getNext(userUUID, s.getId()).join()));
    }
}
