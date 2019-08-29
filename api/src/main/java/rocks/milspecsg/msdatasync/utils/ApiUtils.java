package rocks.milspecsg.msdatasync.utils;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public abstract class ApiUtils {

    public static <P> CompletableFuture<Stream<P>> combineTasks(Stream<CompletableFuture<P>> stream) {
        return CompletableFuture.allOf(stream.toArray(CompletableFuture[]::new))
            .thenApplyAsync(v -> stream.map(CompletableFuture::join));
    }
}
