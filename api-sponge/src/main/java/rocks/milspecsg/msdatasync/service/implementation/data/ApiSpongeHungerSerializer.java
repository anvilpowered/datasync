package rocks.milspecsg.msdatasync.service.implementation.data;

import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.model.core.Snapshot;
import rocks.milspecsg.msdatasync.service.data.ApiHungerSerializer;
import rocks.milspecsg.msdatasync.utils.Utils;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ApiSpongeHungerSerializer<S extends Snapshot> extends ApiHungerSerializer<S, Player, Key> {

    @Override
    public boolean serialize(S snapshot, Player player) {
        // second statement should still run if first one fails
        boolean a = Utils.serialize(snapshotRepository, snapshot, player, Keys.FOOD_LEVEL);
        boolean b = Utils.serialize(snapshotRepository, snapshot, player, Keys.SATURATION);
        return a && b;
    }

    @Override
    public boolean deserialize(S snapshot, Player player) {
        // second statement should still run if first one fails
        boolean a = Utils.deserialize(snapshotRepository, snapshot, player, Keys.FOOD_LEVEL);
        boolean b = Utils.deserialize(snapshotRepository, snapshot, player, Keys.SATURATION);
        return a && b;
    }
}
