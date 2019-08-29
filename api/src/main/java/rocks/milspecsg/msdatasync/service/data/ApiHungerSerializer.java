package rocks.milspecsg.msdatasync.service.data;

import rocks.milspecsg.msdatasync.api.data.HungerSerializer;
import rocks.milspecsg.msdatasync.model.core.Member;

public abstract class ApiHungerSerializer<M extends Member, P, K, U> extends ApiSerializer<M, P, K, U> implements HungerSerializer<M, P> {

    @Override
    public String getName() {
        return "Hunger";
    }

}
