package rocks.milspecsg.msdatasync.api.data;

import rocks.milspecsg.msdatasync.model.core.Snapshot;

import java.util.concurrent.CompletableFuture;

/**
 * @param <S> {@link Snapshot} or subclass. Default implementation by MSDataSync as {@link Snapshot}
 * @param <U> Player class to get data from
 */
public interface SnapshotSerializer<S extends Snapshot, U> extends Serializer<S, U> {

    void registerSerializer(Serializer<S, U> serializer);

    boolean isSerializerEnabled(String name);

}
