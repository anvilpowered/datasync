package rocks.milspecsg.msdatasync.service.implementation.data;

import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.User;
import rocks.milspecsg.msdatasync.model.core.Snapshot;
import rocks.milspecsg.msdatasync.service.data.ApiHungerSerializer;
import rocks.milspecsg.msdatasync.utils.Utils;

public class ApiSpongeHungerSerializer extends ApiHungerSerializer<Snapshot, Key, User> {

    @Override
    public boolean serialize(Snapshot snapshot, User user) {
        // second statement should still run if first one fails
        boolean a = Utils.serialize(snapshotRepository, snapshot, user, Keys.FOOD_LEVEL);
        boolean b = Utils.serialize(snapshotRepository, snapshot, user, Keys.SATURATION);
        return a && b;
    }

    @Override
    public boolean deserialize(Snapshot snapshot, User user) {
        // second statement should still run if first one fails
        boolean a = Utils.deserialize(snapshotRepository, snapshot, user, Keys.FOOD_LEVEL);
        boolean b = Utils.deserialize(snapshotRepository, snapshot, user, Keys.SATURATION);
        return a && b;
    }
}
