package rocks.milspecsg.msdatasync.api.data;

import rocks.milspecsg.msdatasync.model.core.Snapshot;

public interface InventorySerializer<S extends Snapshot, P> extends Serializer<S, P> {
}
