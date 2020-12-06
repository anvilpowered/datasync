package org.anvilpowered.datasync.nukkit.serializer

import cn.nukkit.Player
import org.anvilpowered.datasync.api.model.snapshot.Snapshot
import org.anvilpowered.datasync.common.serializer.CommonHungerSerializer
import org.anvilpowered.datasync.nukkit.util.Utils

class NukkitHungerSerializer : CommonHungerSerializer<String, Player>() {

    override fun serialize(snapshot: Snapshot<*>, user: Player): Boolean {
        val a = Utils.serialize(snapshotManager, snapshot, user, "FOOD_LEVEL")
        val b = Utils.serialize(snapshotManager, snapshot, user, "SATURATION")
        return a && b
    }

    override fun deserialize(snapshot: Snapshot<*>, user: Player): Boolean {
        val a = Utils.deserialize(snapshotManager, snapshot, user, "FOOD_LEVEL")
        val b = Utils.deserialize(snapshotManager, snapshot, user, "SATURATION")
        return a && b
    }
}
