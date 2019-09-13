package rocks.milspecsg.msdatasync.service.implementation.snapshot;

import com.google.inject.Inject;
import org.spongepowered.api.data.key.Key;
import rocks.milspecsg.msdatasync.model.core.Snapshot;
import rocks.milspecsg.msdatasync.service.snapshot.ApiSnapshotRepository;
import rocks.milspecsg.msrepository.db.mongodb.MongoContext;

public abstract class ApiSpongeSnapshotRepository extends ApiSnapshotRepository<Snapshot, Key> {

    @Inject
    public ApiSpongeSnapshotRepository(MongoContext mongoContext) {
        super(mongoContext);
    }
}
