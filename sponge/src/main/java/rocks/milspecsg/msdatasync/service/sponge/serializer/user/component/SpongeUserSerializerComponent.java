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

package rocks.milspecsg.msdatasync.service.sponge.serializer.user.component;

import com.google.inject.Inject;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.scheduler.Task;
import rocks.milspecsg.msdatasync.api.snapshotoptimization.SnapshotOptimizationManager;
import rocks.milspecsg.msdatasync.model.core.member.Member;
import rocks.milspecsg.msdatasync.api.config.ConfigKeys;
import rocks.milspecsg.msdatasync.model.core.snapshot.Snapshot;
import rocks.milspecsg.msdatasync.service.common.serializer.user.component.CommonUserSerializerComponent;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;
import rocks.milspecsg.msrepository.datastore.DataStoreConfig;
import rocks.milspecsg.msrepository.datastore.DataStoreContext;

import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class SpongeUserSerializerComponent<
    TKey,
    TMember extends Member<TKey>,
    TSnapshot extends Snapshot<TKey>,
    TDataStore,
    TDataStoreConfig extends DataStoreConfig>
    extends CommonUserSerializerComponent<TKey, TMember, TSnapshot, User, Key<?>, TDataStore, TDataStoreConfig> {

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private SnapshotOptimizationManager<User, CommandSource> snapshotOptimizationManager;

    @Inject
    public SpongeUserSerializerComponent(DataStoreContext<TKey, TDataStore, TDataStoreConfig> dataStoreContext) {
        super(dataStoreContext);
    }

    @Override
    public CompletableFuture<Optional<TSnapshot>> serialize(User user, String name) {
        TSnapshot snapshot = snapshotRepository.generateEmpty();
        snapshot.setName(name);
        snapshot.setServer(configurationService.getConfigString(ConfigKeys.SERVER_NAME));
        serialize(snapshot, user);
        return snapshotRepository.insertOne(snapshot).thenApplyAsync(optionalSnapshot -> {
            if (!optionalSnapshot.isPresent()) {
                System.err.println("[MSDataSync] Snapshot upload failed for user " + user.getName() + "! Check your DB configuration!");
                return Optional.empty();
            }
            return memberRepository.addSnapshot(user.getUniqueId(), optionalSnapshot.get().getId()).join() ? optionalSnapshot : Optional.empty();
        });
    }

    @Override
    public CompletableFuture<Optional<TSnapshot>> serialize(User user) {
        return serialize(user, "Manual");
    }

    @Override
    public CompletableFuture<Optional<TSnapshot>> deserialize(User user, Object plugin, TSnapshot snapshot) {
        if (snapshot == null) return CompletableFuture.completedFuture(Optional.empty());
        CompletableFuture<Optional<TSnapshot>> result = new CompletableFuture<>();
        if (snapshot.getKeys() == null) snapshot.setKeys(new HashMap<>());
        Task.builder().execute(() -> result.complete(deserialize(snapshot, user) ? Optional.of(snapshot) : Optional.empty())).submit(plugin);
        return result;
    }

    @Override
    public CompletableFuture<Optional<TSnapshot>> deserialize(final User user, final Object plugin) {
        snapshotOptimizationManager.getPrimaryComponent().addLockedPlayer(user.getUniqueId());
        CompletableFuture<Void> waitForSnapshot;
        if (configurationService.getConfigBoolean(ConfigKeys.SERIALIZE_WAIT_FOR_SNAPSHOT_ON_JOIN)) {
            waitForSnapshot = CompletableFuture.runAsync(() -> {
//                while (true){
//                    if (!memberRepository.getNext().map(member -> {
//                        System.out.println(member.userUUID);
//                        return member.userUUID.equals(user.getUniqueId());
//                    }).orElse(false))
//                        break;
//                }
                try {
                    Thread.sleep(7000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        } else {
            waitForSnapshot = CompletableFuture.completedFuture(null);
        }
        return waitForSnapshot.thenApplyAsync(v -> memberRepository.getLatestSnapshot(user.getUniqueId()).thenApplyAsync(optionalSnapshot -> {
            if (!optionalSnapshot.isPresent()) {
                System.err.println("[MSDataSync] Could not find snapshot for user " + user.getName() + "! Check your DB configuration!");
                return Optional.<TSnapshot>empty();
            }
            return deserialize(user, plugin, optionalSnapshot.get()).join();
        }).join()).thenApplyAsync(s -> {
            snapshotOptimizationManager.getPrimaryComponent().removeLockedPlayer(user.getUniqueId());
            return s;
        });
    }

    @Override
    public CompletableFuture<Optional<TSnapshot>> sync(User user, Object plugin) {
        return null;
    }

}
