package rocks.milspecsg.msdatasync.service.snapshot;

import com.google.inject.Inject;
import rocks.milspecsg.msdatasync.api.keys.DataKeyService;
import rocks.milspecsg.msdatasync.api.snapshot.SnapshotRepository;
import rocks.milspecsg.msdatasync.model.core.Snapshot;
import rocks.milspecsg.msrepository.db.mongodb.MongoContext;
import rocks.milspecsg.msrepository.service.ApiRepository;

import java.util.HashMap;
import java.util.Optional;

public abstract class ApiSnapshotRepository<S extends Snapshot, K> extends ApiRepository<S> implements SnapshotRepository<S, K> {

    @Inject
    DataKeyService<K> dataKeyService;

    @Inject
    public ApiSnapshotRepository(MongoContext mongoContext) {
        super(mongoContext);
    }

    @Override
    public boolean setSnapshotValue(S snapshot, K key, Optional<?> optionalValue) {
        if (!optionalValue.isPresent()) {
            return false;
        }
        Optional<String> optionalName = dataKeyService.getName(key);
        if (!optionalName.isPresent()) {
            return false;
        }
        if (snapshot.keys == null) {
            snapshot.keys = new HashMap<>();
        }
        snapshot.keys.put(optionalName.get(), optionalValue.get());
        return true;
    }

    @Override
    public Optional<?> getSnapshotValue(S snapshot, K key) {
        Optional<String> optionalName = dataKeyService.getName(key);
        if (!optionalName.isPresent()) {
            return Optional.empty();
        }
        if (snapshot.keys == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(snapshot.keys.get(optionalName.get()));
    }

}
