package rocks.milspecsg.msdatasync.api.snapshot;

import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.model.core.Snapshot;
import rocks.milspecsg.msrepository.api.Repository;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface SnapshotRepository<S extends Snapshot, K> extends Repository<S> {

    CompletableFuture<Boolean> setSnapshotKey(S snapshot, K key, Optional<?> optionalValue);

    CompletableFuture<Optional<?>> getSnapshotKey(S snapshot, K key);

}
