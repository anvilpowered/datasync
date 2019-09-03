package rocks.milspecsg.msdatasync.service.implementation.keys;

import com.google.inject.Singleton;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import rocks.milspecsg.msdatasync.service.keys.ApiDataKeyService;

@Singleton
public class MSDataKeyService extends ApiDataKeyService<Key> {

    @Override
    public void initializeDefaultMappings() {
        addMapping(Keys.FOOD_LEVEL, "food_level");
        addMapping(Keys.SATURATION, "saturation");
        addMapping(Keys.HEALTH, "health");
        addMapping(Keys.TOTAL_EXPERIENCE, "total_experience");
        addMapping(Keys.GAME_MODE, "game_mode");
    }
}
