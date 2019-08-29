package rocks.milspecsg.msdatasync.service.data;

import rocks.milspecsg.msdatasync.api.data.HealthSerializer;
import rocks.milspecsg.msdatasync.model.core.Member;

public abstract class ApiHealthSerializer<M extends Member, P, K, U> extends ApiSerializer<M, P, K, U> implements HealthSerializer<M, P> {

    @Override
    public String getName() {
        return "Health";
    }

}
