package rocks.milspecsg.msdatasync.service.data;

import rocks.milspecsg.msdatasync.api.data.GameModeSerializer;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.model.core.Snapshot;

public abstract class ApiGameModeSerializer<S extends Snapshot, P, K> extends ApiSerializer<S, P, K> implements GameModeSerializer<S, P> {

    @Override
    public String getName() {
        return "msdatasync:gameMode";
    }

}
