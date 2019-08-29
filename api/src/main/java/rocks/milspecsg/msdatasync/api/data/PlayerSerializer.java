package rocks.milspecsg.msdatasync.api.data;

import rocks.milspecsg.msdatasync.model.core.Member;

import java.util.concurrent.CompletableFuture;

public interface PlayerSerializer<M extends Member, P> extends Serializer<M, P> {

    CompletableFuture<Boolean> serialize(P player);

    CompletableFuture<Boolean> deserialize(P player);

}
