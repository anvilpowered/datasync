package rocks.milspecsg.msdatasync.service.tasks;

import com.google.inject.Inject;
import rocks.milspecsg.msdatasync.api.data.PlayerSerializer;
import rocks.milspecsg.msdatasync.api.tasks.SerializationTaskService;
import rocks.milspecsg.msdatasync.model.core.Snapshot;

public abstract class ApiSerializationTaskService<S extends Snapshot, P> implements SerializationTaskService {

    @Inject
    protected PlayerSerializer<S, P> playerSerializer;

}
