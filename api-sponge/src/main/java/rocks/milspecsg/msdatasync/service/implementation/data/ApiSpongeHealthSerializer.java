package rocks.milspecsg.msdatasync.service.implementation.data;

import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.model.core.Snapshot;
import rocks.milspecsg.msdatasync.service.data.ApiHealthSerializer;
import rocks.milspecsg.msdatasync.utils.Utils;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ApiSpongeHealthSerializer extends ApiHealthSerializer<Snapshot, Key, User> {

    @Override
    public boolean serialize(Snapshot snapshot, User user) {
        return Utils.serialize(snapshotRepository, snapshot, user, Keys.HEALTH);
    }

    @Override
    public boolean deserialize(Snapshot snapshot, User user) {
        return Utils.deserialize(snapshotRepository, snapshot, user, Keys.HEALTH);
    }
}
