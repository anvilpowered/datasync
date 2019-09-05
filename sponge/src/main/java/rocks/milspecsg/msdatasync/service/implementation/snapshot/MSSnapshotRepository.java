package rocks.milspecsg.msdatasync.service.implementation.snapshot;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.spongepowered.api.data.key.Key;
import rocks.milspecsg.msdatasync.model.core.Snapshot;

import java.util.Optional;

public class MSSnapshotRepository extends ApiSpongeSnapshotRepository {

    @Override
    public Snapshot generateEmpty() {
        return new Snapshot();
    }

    @Override
    public UpdateOperations<Snapshot> createUpdateOperations() {
        Optional<Datastore> optionalDatastore = mongoContext.getDataStore();
        return optionalDatastore.map(datastore -> datastore.createUpdateOperations(Snapshot.class)).orElse(null);
    }

    @Override
    public Query<Snapshot> asQuery() {
        Optional<Datastore> optionalDatastore = mongoContext.getDataStore();
        return optionalDatastore.map(datastore -> datastore.createQuery(Snapshot.class)).orElse(null);
    }
}
