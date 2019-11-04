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
import rocks.milspecsg.msdatasync.api.snapshot.repository.SnapshotRepository;
import rocks.milspecsg.msdatasync.model.core.member.Member;
import rocks.milspecsg.msdatasync.model.core.snapshot.Snapshot;
import rocks.milspecsg.msrepository.api.cache.RepositoryCacheService;
import rocks.milspecsg.msrepository.datastore.DataStoreConfig;
import rocks.milspecsg.msrepository.datastore.DataStoreContext;
import rocks.milspecsg.msrepository.model.data.dbo.ObjectWithId;
import rocks.milspecsg.msrepository.service.common.repository.CommonRepository;

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
    TDataStore,
    TDataStoreConfig extends DataStoreConfig>
    extends CommonRepository<TKey, TMember, RepositoryCacheService<TKey, TMember, TDataStore, TDataStoreConfig>, TDataStore, TDataStoreConfig>
    implements MemberRepository<TKey, TMember, TSnapshot, TUser, TDataStore, TDataStoreConfig> {

    @Inject
    protected SnapshotRepository<TKey, TSnapshot, TDataKey, TDataStore, TDataStoreConfig> snapshotRepository;

    public CommonMemberRepository(DataStoreContext<TKey, TDataStore, TDataStoreConfig> dataStoreContext) {
        super(dataStoreContext);
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
    public CompletableFuture<Optional<TSnapshot>> getLatestSnapshot(TKey id) {
        return getOne(id).thenApplyAsync(optionalMember -> optionalMember.flatMap(member -> snapshotRepository.getOne(member.getSnapshotIds().get(member.getSnapshotIds().size() - 1)).join()));
    }

    @Override
    public CompletableFuture<Optional<TSnapshot>> getLatestSnapshot(UUID userUUID) {
        return getOne(userUUID).thenApplyAsync(optionalMember -> optionalMember.flatMap(member -> snapshotRepository.getOne(member.getSnapshotIds().get(member.getSnapshotIds().size() - 1)).join()));
    }
}
