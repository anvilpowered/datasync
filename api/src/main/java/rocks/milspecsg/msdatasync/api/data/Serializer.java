package rocks.milspecsg.msdatasync.api.data;

import rocks.milspecsg.msdatasync.model.core.Snapshot;

import java.util.UUID;

/**
 * @param <S> {@link Snapshot} or subclass. Default implementation by MSDataSync as {@link Snapshot}
 * @param <P> Player class to get data from
 */
public interface Serializer<S extends Snapshot, P> {

    /**
     * @return Name of {@link Serializer}.
     * Should follow format "plugin:name"
     * For example "msdatasync:inventory"
     */
    String getName();

    /**
     * Moves data from {@code player} into {@code member}
     *
     * @param snapshot {@link Snapshot} to add data to
     * @param player   Player to get data from
     * @return Whether serialization was successful
     */
    boolean serialize(S snapshot, P player);

    /**
     * Moves data from {@code member} into {@code player}
     *
     * @param snapshot {@link Snapshot} to get data from
     * @param player   Player to add data to
     * @return Whether deserialization was successful
     */
    boolean deserialize(S snapshot, P player);

}
