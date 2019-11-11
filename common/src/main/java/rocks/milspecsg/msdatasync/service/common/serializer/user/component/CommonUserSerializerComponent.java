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

package rocks.milspecsg.msdatasync.service.common.serializer.user.component;

import com.google.inject.Inject;
import rocks.milspecsg.msdatasync.api.config.ConfigKeys;
import rocks.milspecsg.msdatasync.api.member.repository.MemberRepository;
import rocks.milspecsg.msdatasync.api.serializer.SnapshotSerializer;
import rocks.milspecsg.msdatasync.api.serializer.user.component.UserSerializerComponent;
import rocks.milspecsg.msdatasync.api.snapshot.repository.SnapshotRepository;
import rocks.milspecsg.msdatasync.model.core.member.Member;
import rocks.milspecsg.msdatasync.model.core.snapshot.Snapshot;
import rocks.milspecsg.msrepository.api.UserService;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;
import rocks.milspecsg.msrepository.datastore.DataStoreConfig;
import rocks.milspecsg.msrepository.datastore.DataStoreContext;
import rocks.milspecsg.msrepository.service.common.component.CommonComponent;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public abstract class CommonUserSerializerComponent<
    TKey,
    TMember extends Member<TKey>,
    TSnapshot extends Snapshot<TKey>,
    TUser,
    TDataKey,
    TDataStore,
    TDataStoreConfig extends DataStoreConfig>
    extends CommonComponent<TKey, TDataStore, TDataStoreConfig>
    implements UserSerializerComponent<TKey, TSnapshot, TUser, TDataStore, TDataStoreConfig> {

    @Inject
    protected MemberRepository<TKey, TMember, TSnapshot, TUser, TDataStore, TDataStoreConfig> memberRepository;

    @Inject
    protected SnapshotRepository<TKey, TSnapshot, TDataKey, TDataStore, TDataStoreConfig> snapshotRepository;

    @Inject
    protected SnapshotSerializer<Snapshot<?>, TUser> snapshotSerializer;

    @Inject
    protected ConfigurationService configurationService;

    @Inject
    protected UserService<TUser> userService;

    protected CommonUserSerializerComponent(DataStoreContext<TKey, TDataStore, TDataStoreConfig> dataStoreContext) {
        super(dataStoreContext);
    }

    @Override
    public CompletableFuture<Optional<TSnapshot>> serialize(TUser user, String name) {
        TSnapshot snapshot = snapshotRepository.generateEmpty();
        snapshot.setName(name);
        snapshot.setServer(configurationService.getConfigString(ConfigKeys.SERVER_NAME));
        serialize(snapshot, user);
        return snapshotRepository.insertOne(snapshot).thenApplyAsync(optionalSnapshot -> {
            if (!optionalSnapshot.isPresent()) {
                System.err.println("[MSDataSync] Snapshot upload failed for user " + userService.getUserName(user) + "! Check your DB configuration!");
                return Optional.empty();
            }
            return memberRepository.addSnapshot(userService.getUUID(user), optionalSnapshot.get().getId()).join() ? optionalSnapshot : Optional.empty();
        });
    }

    @Override
    public CompletableFuture<Optional<TSnapshot>> serialize(TUser user) {
        return serialize(user, "Manual");
    }

    @Override
    public String getName() {
        return "msdatasync:player";
    }

    @Override
    public boolean serialize(TSnapshot snapshot, TUser user) {
        return snapshotSerializer.serialize(snapshot, user);
    }

    @Override
    public boolean deserialize(TSnapshot snapshot, TUser user) {
        return snapshotSerializer.deserialize(snapshot, user);
    }
}
