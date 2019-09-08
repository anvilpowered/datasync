package rocks.milspecsg.msdatasync.service.data;

import rocks.milspecsg.msdatasync.api.data.InventorySerializer;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.model.core.Snapshot;

public abstract class ApiInventorySerializer<S extends Snapshot, K, U, I, F> extends ApiSerializer<S, K, U> implements InventorySerializer<S, U, I, F> {

    @Override
    public String getName() {
        return "msdatasync:inventory";
    }

}
