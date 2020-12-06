package org.anvilpowered.datasync.nukkit.keys

import com.google.inject.Singleton
import org.anvilpowered.datasync.common.key.CommonDataKeyService

@Singleton
class NukkitDataKeyService : CommonDataKeyService<String>() {
    init {
        addMapping("FOOD_LEVEL", "food_level")
        addMapping("SATURATION", "saturation")
        addMapping("HEALTH", "health")
        addMapping("TOTAL_EXPERIENCE", "total_experience")
        addMapping("GAME_MODE", "game_mode")
    }
}
