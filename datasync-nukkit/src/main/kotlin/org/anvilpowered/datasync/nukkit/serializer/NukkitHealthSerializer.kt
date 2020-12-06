package org.anvilpowered.datasync.nukkit.serializer

import cn.nukkit.Player
import org.anvilpowered.datasync.api.model.snapshot.Snapshot
import org.anvilpowered.datasync.common.serializer.CommonHealthSerializer
import org.anvilpowered.datasync.nukkit.util.Utils

class NukkitHealthSerializer : CommonHealthSerializer<String, Player>() {
    override fun serialize(snapshot: Snapshot<*>, user: Player): Boolean = Utils.serialize(
        snapshotManager,
        snapshot,
        user,
        "HEALTH"
    )

    override fun deserialize(snapshot: Snapshot<*>, user: Player): Boolean = Utils.deserialize(
        snapshotManager,
        snapshot,
        user,
        "HEALTH"
    )
}
