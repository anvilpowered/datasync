package rocks.milspecsg.msdatasync.api.data;

import rocks.milspecsg.msdatasync.model.core.Snapshot;

public interface InventorySerializer<S extends Snapshot, P, I> extends Serializer<S, P> {

    /**
     * Moves data from {@code inventory} into {@code member}
     *
     * @param snapshot  {@link Snapshot} to add data to
     * @param inventory Player to get data from
     * @return Whether serialization was successful
     */
    boolean serializeInventory(S snapshot, I inventory);

    /**
     * Moves data from {@code member} into {@code inventory}
     *
     * @param snapshot  {@link Snapshot} to get data from
     * @param inventory Player to add data to
     * @return Whether deserialization was successful
     */
    boolean deserializeInventory(S snapshot, I inventory);
}
