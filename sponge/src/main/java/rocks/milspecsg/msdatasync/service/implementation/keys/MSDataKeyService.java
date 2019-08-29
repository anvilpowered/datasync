package rocks.milspecsg.msdatasync.service.implementation.keys;

import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import rocks.milspecsg.msdatasync.service.keys.ApiDataKeyService;

public class MSDataKeyService extends ApiDataKeyService<Key> {


    @Override
    public void initializeDefaultMappings() {
        addMapping(Keys.FOOD_LEVEL, "food_level");
        addMapping(Keys.SATURATION, "saturation");
    }
}
