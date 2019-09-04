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

public class ApiSpongeHealthSerializer<S extends Snapshot> extends ApiHealthSerializer<S, Player, Key> {

    @Override
    public boolean serialize(S snapshot, Player player) {
        return Utils.serialize(snapshotRepository, snapshot, player, Keys.HEALTH);
    }

    @Override
    public boolean deserialize(S snapshot, Player player) {
        return Utils.deserialize(snapshotRepository, snapshot, player, Keys.HEALTH);
    }
}
