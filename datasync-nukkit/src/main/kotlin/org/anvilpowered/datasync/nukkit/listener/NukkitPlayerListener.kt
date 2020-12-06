package org.anvilpowered.datasync.nukkit.listener

import cn.nukkit.Player
import cn.nukkit.command.CommandSender
import cn.nukkit.event.EventHandler
import cn.nukkit.event.EventPriority
import cn.nukkit.event.Listener
import cn.nukkit.event.player.PlayerDeathEvent
import cn.nukkit.event.player.PlayerJoinEvent
import cn.nukkit.event.player.PlayerQuitEvent
import com.google.inject.Inject
import com.google.inject.Singleton
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.anvil.api.util.TextService
import org.anvilpowered.datasync.api.registry.DataSyncKeys
import org.anvilpowered.datasync.api.serializer.user.UserSerializerManager

@Singleton
class NukkitPlayerListener @Inject constructor(
    private val registry: Registry
) : Listener {

    @Inject
    private lateinit var textService: TextService<String, CommandSender>

    @Inject
    private lateinit var userSerializerManager: UserSerializerManager<Player, String>

    private var joinSerializationEnabled = false
    private var disconnectSerializationEnabled = false
    private var deathSerializationEnabled = false

    init {
        registry.whenLoaded { registryLoaded() }.register()
    }

    private fun registryLoaded() {
        joinSerializationEnabled = registry.getOrDefault(DataSyncKeys.DESERIALIZE_ON_JOIN)
        if (!joinSerializationEnabled) {
            sendWarning("serialize.deserializeOnJoin")
        }
        disconnectSerializationEnabled = registry.getOrDefault(DataSyncKeys.SERIALIZE_ON_DISCONNECT)
        if (!disconnectSerializationEnabled) {
            sendWarning("serialize.serializeOnDisconnect")
        }
        deathSerializationEnabled = registry.getOrDefault(DataSyncKeys.SERIALIZE_ON_DEATH)
        if (!deathSerializationEnabled) {
            sendWarning("serialize.serializeOnDeath")
        }
    }

    private fun sendWarning(name: String) {
        textService.builder()
            .appendPrefix()
            .red().append("Attention! You have opted to disable", name, ".\n",
                "If you would like to disable this, set`", name,
                "=true` in the config and restart your server or run /sync reload"
            ).sendToConsole()
    }

    @EventHandler(priority = EventPriority.NORMAL)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        if (joinSerializationEnabled) {
            print("Attempting to deserialize")
            userSerializerManager.deserialize(event.player)
                .thenAcceptAsync { msg: String -> textService.sendToConsole(msg) }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    fun onPlayerDisconnect(event: PlayerQuitEvent) {
        if (disconnectSerializationEnabled) {
            userSerializerManager.serializeSafe(event.player, "Disconnect")
                .thenAcceptAsync { msg: String -> textService.sendToConsole(msg) }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    fun onPlayerDeath(event: PlayerDeathEvent) {
        if (deathSerializationEnabled) {
            userSerializerManager.serializeSafe(event.entity.player, "Death")
                .thenAcceptAsync { msg: String -> textService.sendToConsole(msg) }
        }
    }
}
