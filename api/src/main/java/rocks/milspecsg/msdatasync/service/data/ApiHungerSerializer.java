package rocks.milspecsg.msdatasync.service.data;

import rocks.milspecsg.msdatasync.api.data.HungerSerializer;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.model.core.Snapshot;

public abstract class ApiHungerSerializer<S extends Snapshot, K, U> extends ApiSerializer<S, K, U> implements HungerSerializer<S, U> {

    @Override
    public String getName() {
        return "msdatasync:hunger";
    }

}
