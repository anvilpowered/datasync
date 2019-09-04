package rocks.milspecsg.msdatasync.api.data;

import rocks.milspecsg.msdatasync.model.core.Snapshot;

import java.util.concurrent.CompletableFuture;

public interface PlayerSerializer<S extends Snapshot, P> extends Serializer<S, P> {

    CompletableFuture<Boolean> serialize(P player);

    CompletableFuture<Boolean> deserialize(P player, Object plugin);

    CompletableFuture<Boolean> sync(P player, Object plugin);

}
