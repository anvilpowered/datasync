package org.anvilpowered.datasync.spigot.keys;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.anvilpowered.datasync.common.key.CommonDataKeyService;

@Singleton
public class SpigotDataKeyService extends CommonDataKeyService<String> {

    @Inject
    public SpigotDataKeyService() {
        addMapping("FOOD_LEVEL", "food_level");
        addMapping("SATURATION", "saturation");
        addMapping("HEALTH", "health");
        addMapping("TOTAL_EXPERIENCE", "total_experience");
        addMapping("GAME_MODE", "game_mode");
    }
}
