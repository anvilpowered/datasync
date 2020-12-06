package org.anvilpowered.datasync.nukkit.serializer.user

import cn.nukkit.Player
import cn.nukkit.command.CommandSender
import com.google.inject.Inject
import org.anvilpowered.datasync.api.model.snapshot.Snapshot
import org.anvilpowered.datasync.api.registry.DataSyncKeys
import org.anvilpowered.datasync.api.snapshotoptimization.SnapshotOptimizationManager
import org.anvilpowered.datasync.common.serializer.user.CommonUserSerializerComponent
import java.util.Optional
import java.util.concurrent.CompletableFuture

class NukkitUserSerializerComponent<TKey, TDataStore> : CommonUserSerializerComponent<TKey, Player, Player, String, TDataStore>() {

    @Inject
    private lateinit var snapshotOptimizationManager: SnapshotOptimizationManager<Player, String, CommandSender>

    override fun deserialize(user: Player, waitFuture: CompletableFuture<Boolean>): CompletableFuture<Optional<Snapshot<TKey>>> {
        snapshotOptimizationManager.primaryComponent.addLockedPlayer(user.uniqueId)
        val previousState = snapshotRepository.generateEmpty()
        serialize(previousState, user)
        if (registry.getOrDefault(DataSyncKeys.SERIALIZE_ENABLED_SERIALIZERS).contains("datasync:inventory")) {
            user.inventory.clearAll()
        }
        return waitFuture.thenApplyAsync { shouldDeserialize: Boolean ->
            if (!shouldDeserialize) {
                return@thenApplyAsync Optional.empty<Snapshot<TKey>>()
            }
            memberRepository.getLatestSnapshotForUser(user.uniqueId)
                .exceptionally { e: Throwable ->
                    e.printStackTrace()
                    Optional.empty()
                }
                .thenApplyAsync { optionalSnapshot: Optional<Snapshot<TKey>> ->
                    if (!user.isOnline) {
                        logger.warn("{} has logged off. Skipping deserialization", user.name)
                        return@thenApplyAsync Optional.empty<Snapshot<TKey>>()
                    }
                    if (!optionalSnapshot.isPresent) {
                        logger.warn("Could not find snapshot for {} Check your DB configuration. Rolling back user.",
                            user.name)
                        deserialize(previousState, user)
                        return@thenApplyAsync Optional.empty<Snapshot<TKey>>()
                    }
                    if (deserialize(optionalSnapshot.get(), user)) {
                        return@thenApplyAsync optionalSnapshot
                    }
                    Optional.empty<Snapshot<TKey>>()
                }.join()
        }.thenApplyAsync { s: Optional<Snapshot<TKey>> ->
            snapshotOptimizationManager.primaryComponent
                .removeLockedPlayer(user.uniqueId)
            s
        }
    }

    override fun deserialize(snapshot: Snapshot<*>, user: Player): Boolean {
        val result = CompletableFuture<Boolean>()
        CompletableFuture.runAsync { result.complete(snapshotSerializer.deserialize(snapshot, user)) }
        return result.join()
    }
}
