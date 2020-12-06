package org.anvilpowered.datasync.nukkit.serializer

import cn.nukkit.Player
import org.anvilpowered.datasync.api.model.snapshot.Snapshot
import org.anvilpowered.datasync.common.serializer.CommonExperienceSerializer
import org.anvilpowered.datasync.nukkit.util.Utils

class NukkitExperienceSerializer : CommonExperienceSerializer<String, Player>() {

    override fun serialize(snapshot: Snapshot<*>, user: Player): Boolean = Utils.serialize(
        snapshotManager,
        snapshot,
        user,
        "TOTAL_EXPERIENCE"
    )

    override fun deserialize(snapshot: Snapshot<*>, user: Player): Boolean = Utils.deserialize(
        snapshotManager,
        snapshot,
        user,
        "TOTAL_EXPERIENCE"
    )
}
