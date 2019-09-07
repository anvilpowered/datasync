package rocks.milspecsg.msdatasync.utils;

import com.google.common.reflect.TypeToken;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import rocks.milspecsg.msdatasync.api.snapshot.SnapshotRepository;
import rocks.milspecsg.msdatasync.model.core.Snapshot;

import java.util.Optional;

public class Utils {

    public static <E, S extends Snapshot> boolean serialize(SnapshotRepository<S, Key> snapshotRepository, S snapshot, User user, Key<? extends BaseValue<E>> key) {
        return snapshotRepository.setSnapshotValue(snapshot, key, user.get(key));
    }

    public static <E, S extends Snapshot> boolean deserialize(SnapshotRepository<S, Key> snapshotRepository, S snapshot, User user, Key<? extends BaseValue<E>> key) {

        Optional<?> optionalSnapshot = snapshotRepository.getSnapshotValue(snapshot, key);
        if (!optionalSnapshot.isPresent()) {
            return false;
        }

        try {
            user.offer(key, (E) decode(optionalSnapshot.get(), key));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static Object decode(Object value, Key<? extends BaseValue<?>> key) {
        TypeToken<?> typeToken = key.getElementToken();

        if (typeToken.isSubtypeOf(GameMode.class)) {
            switch (value.toString()) {
                case "ADVENTURE":
                    return GameModes.ADVENTURE;
                case "CREATIVE":
                    return GameModes.CREATIVE;
                case "SPECTATOR":
                    return GameModes.SPECTATOR;
                case "SURVIVAL":
                    return GameModes.SURVIVAL;
                // "NOT_SET"
                default:
                    return GameModes.NOT_SET;
            }
        }
        return value;
    }
}
