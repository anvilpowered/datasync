package rocks.milspecsg.msdatasync.api.data;

import rocks.milspecsg.msdatasync.model.core.Snapshot;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface UserSerializer<S extends Snapshot, U> extends Serializer<S, U> {

    CompletableFuture<Optional<S>> serialize(U user, String name);

    CompletableFuture<Optional<S>> serialize(U user);

    CompletableFuture<Optional<S>> deserialize(U user, Object plugin, Snapshot snapshot);

    CompletableFuture<Optional<S>> deserialize(U user, Object plugin);

    CompletableFuture<Optional<S>> sync(U user, Object plugin);

}
