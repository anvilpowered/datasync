package rocks.milspecsg.msdatasync.service.data;

import rocks.milspecsg.msdatasync.api.data.InventorySerializer;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.model.core.Snapshot;

public abstract class ApiInventorySerializer<S extends Snapshot, P, K, I> extends ApiSerializer<S, P, K> implements InventorySerializer<S, P, I> {

    @Override
    public String getName() {
        return "msdatasync:inventory";
    }

}
