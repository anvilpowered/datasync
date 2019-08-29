package rocks.milspecsg.msdatasync.api.data;

import rocks.milspecsg.msdatasync.model.core.Member;

import java.util.concurrent.CompletableFuture;

public interface Serializer<M extends Member, P> {

    String getName();

    /**
     *
     * Moves data from {@code player} into {@code member}
     *
     * @param member {@link Member} to add data to
     * @param player Player to get data from
     */
    CompletableFuture<Boolean> serialize(M member, P player);

    /**
     *
     * Moves data from {@code member} into {@code player}
     *
     * @param member {@link Member} to get data from
     * @param player Player to add data to
     */
    CompletableFuture<Boolean> deserialize(M member, P player);

}
