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

package org.anvilpowered.datasync.spigot.serializer.user;

import com.google.inject.Inject;
import net.md_5.bungee.api.chat.TextComponent;
import org.anvilpowered.datasync.api.model.snapshot.Snapshot;
import org.anvilpowered.datasync.api.registry.DataSyncKeys;
import org.anvilpowered.datasync.api.snapshotoptimization.SnapshotOptimizationManager;
import org.anvilpowered.datasync.common.serializer.user.CommonUserSerializerComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class SpigotUserSerializerComponent<
    TKey,
    TDataStore>
    extends CommonUserSerializerComponent<TKey, Player, Player, String, TDataStore> {

    @Inject
    private SnapshotOptimizationManager<Player, TextComponent, CommandSender> snapshotOptimizationManager;

    @Override
    public boolean deserialize(Snapshot<?> snapshot, Player player) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        CompletableFuture.runAsync(()
            -> result.complete(snapshotSerializer.deserialize(snapshot, player)));
        return result.join();
    }

    @Override
    public CompletableFuture<Optional<Snapshot<TKey>>> deserialize(Player player, CompletableFuture<Boolean> waitFuture) {
        snapshotOptimizationManager.getPrimaryComponent().addLockedPlayer(player.getUniqueId());

        Snapshot<TKey> previousState = snapshotRepository.generateEmpty();
        serialize(previousState, player);
        if (registry.getOrDefault(DataSyncKeys.SERIALIZE_ENABLED_SERIALIZERS).contains("datasync:inventory")) {
            player.getInventory().clear();
        }

        return waitFuture.thenApplyAsync(shouldDeserialize -> {
            if (!shouldDeserialize) {
                return Optional.<Snapshot<TKey>>empty();
            }
            return memberRepository.getLatestSnapshotForUser(player.getUniqueId())
                .exceptionally(e -> {
                    e.printStackTrace();
                    return Optional.empty();
                })
                .thenApplyAsync(optionalSnapshot -> {
                    // make sure user is still online
                    if (!player.isOnline()) {
                        logger.warn("{} has logged off. Skipping deserialization!", player.getName());
                        return Optional.<Snapshot<TKey>>empty();
                    }
                    if (!optionalSnapshot.isPresent()) {
                        logger.warn("Could not find snapshot for {}! Check your DB configuration! Rolling back user.",
                            player.getName());
                        deserialize(previousState, player);
                        return Optional.<Snapshot<TKey>>empty();
                    }
                    if (deserialize(optionalSnapshot.get(), player)) {
                        return optionalSnapshot;
                    }
                    return Optional.<Snapshot<TKey>>empty();
                }).join();
        }).thenApplyAsync(s -> {
            snapshotOptimizationManager.getPrimaryComponent()
                .removeLockedPlayer(player.getUniqueId());
            return s;
        });
    }
}
