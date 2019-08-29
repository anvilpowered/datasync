package rocks.milspecsg.msdatasync.service.data;

import rocks.milspecsg.msdatasync.api.data.ExperienceSerializer;
import rocks.milspecsg.msdatasync.model.core.Member;

public abstract class ApiExperienceSerializer<M extends Member, P, K, U> extends ApiSerializer<M, P, K, U> implements ExperienceSerializer<M, P> {

    @Override
    public String getName() {
        return "Experience";
    }

}
