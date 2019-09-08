package rocks.milspecsg.msdatasync.service.data;

import rocks.milspecsg.msdatasync.api.data.HealthSerializer;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.model.core.Snapshot;

public abstract class ApiHealthSerializer<S extends Snapshot, K, U> extends ApiSerializer<S, K, U> implements HealthSerializer<S, U> {

    @Override
    public String getName() {
        return "msdatasync:health";
    }

}
