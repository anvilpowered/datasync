package rocks.milspecsg.msdatasync.api.data;

import rocks.milspecsg.msdatasync.model.core.Snapshot;

import java.util.concurrent.CompletableFuture;

/**
 * @param <S> {@link Snapshot} or subclass. Default implementation by MSDataSync as {@link Snapshot}
 * @param <P> Player class to get data from
 */
public interface SnapshotSerializer<S extends Snapshot, P> extends Serializer<S, P> {

    void registerSerializer(Serializer<S, P> serializer);

}
