package org.anvilpowered.datasync.nukkit.serializer

import cn.nukkit.Player
import cn.nukkit.command.CommandSender
import cn.nukkit.inventory.Inventory
import cn.nukkit.item.Item
import com.google.inject.Inject
import com.google.inject.Singleton
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.anvil.api.util.TextService
import org.anvilpowered.datasync.common.serializer.CommonSnapshotSerializer

@Singleton
class NukkitSnapshotSerializer @Inject constructor(
    registry: Registry
) : CommonSnapshotSerializer<String, Player, Player, Inventory, Item>(registry) {

    @Inject
    private lateinit var textService: TextService<String, CommandSender>

    override fun postLoadedEvent() {
        TODO("Not yet implemented")
    }

    override fun announceEnabled(name: String) {
        textService.builder()
            .appendPrefix()
            .yellow().append("Enabling $name serializer")
            .sendToConsole()
    }
}
