package org.anvilpowered.datasync.nukkit.serializer

import cn.nukkit.Player
import org.anvilpowered.datasync.api.model.snapshot.Snapshot
import org.anvilpowered.datasync.common.serializer.CommonGameModeSerializer
import org.anvilpowered.datasync.nukkit.util.Utils

class NukkitGameModeSerializer : CommonGameModeSerializer<String, Player>() {

    override fun serialize(snapshot: Snapshot<*>, user: Player): Boolean = Utils.serialize(
        snapshotManager,
        snapshot,
        user,
        "GAME_MODE"
    )

    override fun deserialize(snapshot: Snapshot<*>, user: Player): Boolean = Utils.serialize(
        snapshotManager,
        snapshot,
        user,
        "GAME_MODE"
    )
}
