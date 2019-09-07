package rocks.milspecsg.msdatasync.api.data;

import rocks.milspecsg.msdatasync.model.core.Snapshot;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface PlayerSerializer<S extends Snapshot, P> extends Serializer<S, P> {

    CompletableFuture<Optional<S>> serialize(P player, String name);

    CompletableFuture<Optional<S>> serialize(P player);

    CompletableFuture<Optional<S>> deserialize(P player, Object plugin, Snapshot snapshot);

    CompletableFuture<Optional<S>> deserialize(P player, Object plugin);

    CompletableFuture<Optional<S>> sync(P player, Object plugin);

}
