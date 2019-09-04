package rocks.milspecsg.msdatasync.service.data;

import rocks.milspecsg.msdatasync.api.data.HungerSerializer;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.model.core.Snapshot;

public abstract class ApiHungerSerializer<S extends Snapshot, P, K> extends ApiSerializer<S, P, K> implements HungerSerializer<S, P> {

    @Override
    public String getName() {
        return "msdatasync:hunger";
    }

}
