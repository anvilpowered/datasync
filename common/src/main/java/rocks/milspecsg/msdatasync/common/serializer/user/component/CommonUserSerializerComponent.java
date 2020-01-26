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

package rocks.milspecsg.msdatasync.common.serializer.user.component;

import com.google.inject.Inject;
import rocks.milspecsg.msdatasync.api.member.repository.MemberRepository;
import rocks.milspecsg.msdatasync.api.model.member.Member;
import rocks.milspecsg.msdatasync.api.model.snapshot.Snapshot;
import rocks.milspecsg.msdatasync.api.serializer.SnapshotSerializer;
import rocks.milspecsg.msdatasync.api.serializer.user.component.UserSerializerComponent;
import rocks.milspecsg.msdatasync.api.snapshot.repository.SnapshotRepository;
import rocks.milspecsg.msrepository.api.data.key.Keys;
import rocks.milspecsg.msrepository.api.data.registry.Registry;
import rocks.milspecsg.msrepository.api.datastore.DataStoreContext;
import rocks.milspecsg.msrepository.api.util.UserService;
import rocks.milspecsg.msrepository.common.component.CommonComponent;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public abstract class CommonUserSerializerComponent<
    TKey,
    TMember extends Member<TKey>,
    TSnapshot extends Snapshot<TKey>,
    TUser,
    TPlayer,
    TDataKey,
    TDataStore>
    extends CommonComponent<TKey, TDataStore>
    implements UserSerializerComponent<TKey, TSnapshot, TUser, TDataStore> {

    @Inject
    protected MemberRepository<TKey, TMember, TSnapshot, TUser, TDataStore> memberRepository;

    @Inject
    protected SnapshotRepository<TKey, TSnapshot, TDataKey, TDataStore> snapshotRepository;

    @Inject
    protected SnapshotSerializer<Snapshot<?>, TUser> snapshotSerializer;

    @Inject
    protected Registry registry;

    @Inject
    protected UserService<TUser, TPlayer> userService;

    protected CommonUserSerializerComponent(DataStoreContext<TKey, TDataStore> dataStoreContext) {
        super(dataStoreContext);
    }

    @Override
    public CompletableFuture<Optional<TSnapshot>> serialize(TUser user, String name) {
        TSnapshot snapshot = snapshotRepository.generateEmpty();
        snapshot.setName(name);
        snapshot.setServer(registry.getOrDefault(Keys.resolveUnsafe("SERVER_NAME")));
        serialize(snapshot, user);
        return snapshotRepository.insertOne(snapshot).thenApplyAsync(optionalSnapshot -> {
            if (!optionalSnapshot.isPresent()) {
                System.err.println("[MSDataSync] Snapshot upload failed for " + userService.getUserName(user) + "! Check your DB configuration!");
                return Optional.empty();
            }
            if (memberRepository.addSnapshotForUser(userService.getUUID(user), optionalSnapshot.get().getId()).join()) {
                return optionalSnapshot;
            } else {
                // remove snapshot from DB because it was not added to the user successfully
                snapshotRepository.deleteOne(optionalSnapshot.get().getId()).join();
                return Optional.empty();
            }
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
