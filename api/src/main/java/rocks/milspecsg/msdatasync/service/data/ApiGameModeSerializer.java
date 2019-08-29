package rocks.milspecsg.msdatasync.service.data;

import rocks.milspecsg.msdatasync.api.data.GameModeSerializer;
import rocks.milspecsg.msdatasync.model.core.Member;

public abstract class ApiGameModeSerializer<M extends Member, P, K, U> extends ApiSerializer<M, P, K, U> implements GameModeSerializer<M, P> {

    @Override
    public String getName() {
        return "GameMode";
    }

}
