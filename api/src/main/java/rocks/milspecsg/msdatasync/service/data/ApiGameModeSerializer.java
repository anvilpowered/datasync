package rocks.milspecsg.msdatasync.service.data;

import rocks.milspecsg.msdatasync.api.data.GameModeSerializer;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.model.core.Snapshot;

public abstract class ApiGameModeSerializer<S extends Snapshot, K, U> extends ApiSerializer<S, K, U> implements GameModeSerializer<S, U> {

    @Override
    public String getName() {
        return "msdatasync:gameMode";
    }

}
