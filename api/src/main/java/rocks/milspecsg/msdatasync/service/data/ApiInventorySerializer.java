package rocks.milspecsg.msdatasync.service.data;

import rocks.milspecsg.msdatasync.api.data.InventorySerializer;
import rocks.milspecsg.msdatasync.model.core.Member;

public abstract class ApiInventorySerializer<M extends Member, P, K, U> extends ApiSerializer<M, P, K, U> implements InventorySerializer<M, P> {

    @Override
    public String getName() {
        return "Inventory";
    }

}
