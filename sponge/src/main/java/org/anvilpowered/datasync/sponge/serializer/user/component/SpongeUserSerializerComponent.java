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

package org.anvilpowered.datasync.sponge.serializer.user.component;

import com.google.inject.Inject;
import org.anvilpowered.anvil.api.datastore.DataStoreContext;
import org.anvilpowered.datasync.api.model.member.Member;
import org.anvilpowered.datasync.api.model.snapshot.Snapshot;
import org.anvilpowered.datasync.api.snapshotoptimization.SnapshotOptimizationManager;
import org.anvilpowered.datasync.common.data.key.DataSyncKeys;
import org.anvilpowered.datasync.common.serializer.user.component.CommonUserSerializerComponent;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class SpongeUserSerializerComponent<
    TKey,
    TMember extends Member<TKey>,
    TSnapshot extends Snapshot<TKey>,
    TDataStore>
    extends CommonUserSerializerComponent<TKey, TMember, TSnapshot, User, Player, Key<?>, TDataStore> {

    @Inject
    private PluginContainer pluginContainer;

    @Inject
    private SnapshotOptimizationManager<User, Text, CommandSource> snapshotOptimizationManager;

    @Inject
    public SpongeUserSerializerComponent(DataStoreContext<TKey, TDataStore> dataStoreContext) {
        super(dataStoreContext);
    }

    @Override
    public CompletableFuture<Optional<TSnapshot>> deserialize(User user, TSnapshot snapshot) {
        if (snapshot == null) return CompletableFuture.completedFuture(Optional.empty());
        CompletableFuture<Optional<TSnapshot>> result = new CompletableFuture<>();
        Task.builder().execute(() -> result.complete(
            deserialize(snapshot, user)
                ? Optional.of(snapshot)
                : Optional.empty())
        ).submit(pluginContainer);
        return result;
    }

    @Override
    public CompletableFuture<Optional<TSnapshot>> deserialize(final User user) {
        snapshotOptimizationManager.getPrimaryComponent().addLockedPlayer(user.getUniqueId());
        CompletableFuture<Void> waitForSnapshot;
        if (registry.getOrDefault(DataSyncKeys.SERIALIZE_WAIT_FOR_SNAPSHOT_ON_JOIN)) {
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
        return waitForSnapshot.thenApplyAsync(v -> memberRepository.getLatestSnapshotForUser(user.getUniqueId()).thenApplyAsync(optionalSnapshot -> {
            if (!optionalSnapshot.isPresent()) {
                System.err.println("[MSDataSync] Could not find snapshot for " + user.getName() + "! Check your DB configuration!");
                return Optional.<TSnapshot>empty();
            }
            return deserialize(user, optionalSnapshot.get()).join();
        }).join()).thenApplyAsync(s -> {
            snapshotOptimizationManager.getPrimaryComponent().removeLockedPlayer(user.getUniqueId());
            return s;
        });
    }
}
