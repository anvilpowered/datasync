package rocks.milspecsg.msdatasync.service.implementation.data;

import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import rocks.milspecsg.msdatasync.model.core.Snapshot;
import rocks.milspecsg.msdatasync.service.data.ApiGameModeSerializer;
import rocks.milspecsg.msdatasync.utils.Utils;

public class ApiSpongeGameModeSerializer<S extends Snapshot> extends ApiGameModeSerializer<S, Player, Key> {

    @Override
    public boolean serialize(S snapshot, Player player) {
        return Utils.serialize(snapshotRepository, snapshot, player, Keys.GAME_MODE);
    }

    @Override
    public boolean deserialize(S snapshot, Player player) {
        return Utils.deserialize(snapshotRepository, snapshot, player, Keys.GAME_MODE);
    }
}