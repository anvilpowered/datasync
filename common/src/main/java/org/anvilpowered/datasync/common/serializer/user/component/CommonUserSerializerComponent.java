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

package org.anvilpowered.datasync.common.serializer.user.component;

import com.google.inject.Inject;
import org.anvilpowered.anvil.api.data.key.Keys;
import org.anvilpowered.anvil.api.data.registry.Registry;
import org.anvilpowered.anvil.api.datastore.DataStoreContext;
import org.anvilpowered.anvil.api.util.UserService;
import org.anvilpowered.anvil.base.datastore.BaseComponent;
import org.anvilpowered.datasync.api.member.repository.MemberRepository;
import org.anvilpowered.datasync.api.model.snapshot.Snapshot;
import org.anvilpowered.datasync.api.serializer.SnapshotSerializer;
import org.anvilpowered.datasync.api.serializer.user.component.UserSerializerComponent;
import org.anvilpowered.datasync.api.snapshot.repository.SnapshotRepository;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public abstract class CommonUserSerializerComponent<
    TKey,
    TUser,
    TPlayer,
    TDataKey,
    TDataStore>
    extends BaseComponent<TKey, TDataStore>
    implements UserSerializerComponent<TKey, TUser, TDataStore> {

    @Inject
    protected MemberRepository<TKey, TDataStore> memberRepository;

    @Inject
    protected SnapshotRepository<TKey, TDataKey, TDataStore> snapshotRepository;

    @Inject
    protected SnapshotSerializer<TUser> snapshotSerializer;

    @Inject
    protected Registry registry;

    @Inject
    protected UserService<TUser, TPlayer> userService;

    protected CommonUserSerializerComponent(DataStoreContext<TKey, TDataStore> dataStoreContext) {
        super(dataStoreContext);
    }

    @Override
    public CompletableFuture<Optional<Snapshot<TKey>>> serialize(TUser user, String name) {
        Snapshot<TKey> snapshot = snapshotRepository.generateEmpty();
        snapshot.setName(name);
        snapshot.setServer(registry.getOrDefault(Keys.SERVER_NAME));
        serialize(snapshot, user);
        return snapshotRepository.insertOne(snapshot).thenApplyAsync(optionalSnapshot -> {
            if (!optionalSnapshot.isPresent()) {
                System.err.println("[DataSync] Snapshot upload failed for " + userService.getUserName(user) + "! Check your DB configuration!");
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
    public String getName() {
        return "datasync:player";
    }

    @Override
    public boolean serialize(Snapshot<?> snapshot, TUser user) {
        return snapshotSerializer.serialize(snapshot, user);
    }
}
