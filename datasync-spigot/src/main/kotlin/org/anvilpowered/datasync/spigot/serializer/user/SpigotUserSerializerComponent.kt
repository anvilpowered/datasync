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

package org.anvilpowered.datasync.spigot.serializer.user

import com.google.inject.Inject
import net.md_5.bungee.api.chat.TextComponent
import org.anvilpowered.datasync.api.model.snapshot.Snapshot
import org.anvilpowered.datasync.api.registry.DataSyncKeys
import org.anvilpowered.datasync.api.snapshotoptimization.SnapshotOptimizationManager
import org.anvilpowered.datasync.common.serializer.user.CommonUserSerializerComponent
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.Optional
import java.util.concurrent.CompletableFuture
import java.util.function.Function

class SpigotUserSerializerComponent<TKey, TDataStore> : CommonUserSerializerComponent<TKey, Player, Player, String, TDataStore>() {
    
    @Inject
    private lateinit var snapshotOptimizationManager: SnapshotOptimizationManager<Player, TextComponent, CommandSender>

    override fun deserialize(snapshot: Snapshot<*>, player: Player): Boolean {
        val result = CompletableFuture<Boolean>()
        CompletableFuture.runAsync { result.complete(snapshotSerializer.deserialize(snapshot, player)) }
        return result.join()
    }

    override fun deserialize(player: Player, waitFuture: CompletableFuture<Boolean>): CompletableFuture<Optional<Snapshot<TKey>>> {
        snapshotOptimizationManager.primaryComponent.addLockedPlayer(player.uniqueId)
        val previousState = snapshotRepository.generateEmpty()
        serialize(previousState, player)
        if (registry.getOrDefault(DataSyncKeys.SERIALIZE_ENABLED_SERIALIZERS).contains("datasync:inventory")) {
            player.inventory.clear()
        }
        return waitFuture.thenApplyAsync { shouldDeserialize: Boolean ->
            if (!shouldDeserialize) {
                return@thenApplyAsync Optional.empty<Snapshot<TKey>>()
            }
            memberRepository.getLatestSnapshotForUser(player.uniqueId)
                .exceptionally { e: Throwable ->
                    e.printStackTrace()
                    Optional.empty()
                }
                .thenApplyAsync { optionalSnapshot: Optional<Snapshot<TKey>> ->
                    // make sure user is still online
                    if (!player.isOnline) {
                        logger.warn("{} has logged off. Skipping deserialization", player.name)
                        return@thenApplyAsync Optional.empty<Snapshot<TKey>>()
                    }
                    if (!optionalSnapshot.isPresent) {
                        logger.warn("Could not find snapshot for {} Check your DB configuration Rolling back user.",
                            player.name)
                        deserialize(previousState, player)
                        return@thenApplyAsync Optional.empty<Snapshot<TKey>>()
                    }
                    if (deserialize(optionalSnapshot.get(), player)) {
                        return@thenApplyAsync optionalSnapshot
                    }
                    Optional.empty<Snapshot<TKey>>()
                }.join()
        }.thenApplyAsync { s: Optional<Snapshot<TKey>> ->
            snapshotOptimizationManager.primaryComponent
                .removeLockedPlayer(player.uniqueId)
            s
        }
    }
}
