package rocks.milspecsg.msdatasync.api.snapshot;

import rocks.milspecsg.msdatasync.model.core.Snapshot;
import rocks.milspecsg.msrepository.api.Repository;

import java.util.Optional;

public interface SnapshotRepository<S extends Snapshot, K> extends Repository<S> {

    default String getDefaultIdentifierSingularUpper() {
        return "Snapshot";
    }

    default String getDefaultIdentifierPluralUpper() {
        return "Snapshots";
    }

    default String getDefaultIdentifierSingularLower() {
        return "snapshot";
    }

    default String getDefaultIdentifierPluralLower() {
        return "snapshots";
    }


    boolean setSnapshotValue(S snapshot, K key, Optional<?> optionalValue);

    Optional<?> getSnapshotValue(S snapshot, K key);

}
