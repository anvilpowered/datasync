package rocks.milspecsg.msdatasync.service.data;

import rocks.milspecsg.msdatasync.api.data.ExperienceSerializer;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.model.core.Snapshot;

public abstract class ApiExperienceSerializer<S extends Snapshot, P, K> extends ApiSerializer<S, P, K> implements ExperienceSerializer<S, P> {

    @Override
    public String getName() {
        return "msdatasync:experience";
    }

}
