package rocks.milspecsg.msdatasync.service.data;

import rocks.milspecsg.msdatasync.api.data.ExperienceSerializer;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.model.core.Snapshot;

public abstract class ApiExperienceSerializer<S extends Snapshot, K, U> extends ApiSerializer<S, K, U> implements ExperienceSerializer<S, U> {

    @Override
    public String getName() {
        return "msdatasync:experience";
    }

}
