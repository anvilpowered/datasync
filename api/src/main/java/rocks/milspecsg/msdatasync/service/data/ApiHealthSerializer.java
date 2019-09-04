package rocks.milspecsg.msdatasync.service.data;

import rocks.milspecsg.msdatasync.api.data.HealthSerializer;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.model.core.Snapshot;

public abstract class ApiHealthSerializer<S extends Snapshot, P, K> extends ApiSerializer<S, P, K> implements HealthSerializer<S, P> {

    @Override
    public String getName() {
        return "msdatasync:health";
    }

}
