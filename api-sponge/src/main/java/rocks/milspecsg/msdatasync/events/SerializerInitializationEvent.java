package rocks.milspecsg.msdatasync.events;

import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;
import rocks.milspecsg.msdatasync.api.data.Serializer;
import rocks.milspecsg.msdatasync.api.data.SnapshotSerializer;
import rocks.milspecsg.msdatasync.api.snapshot.SnapshotRepository;
import rocks.milspecsg.msdatasync.model.core.Snapshot;

import java.util.concurrent.CompletableFuture;

public class SerializerInitializationEvent<S extends Snapshot> extends AbstractEvent {

    private final Cause cause;
    private final SnapshotSerializer<S, Player> snapshotSerializer;
    private final SnapshotRepository<S, Key> snapshotRepository;

    public SerializerInitializationEvent(
        SnapshotSerializer<S, Player> snapshotSerializer,
        SnapshotRepository<S, Key> snapshotRepository,
        Cause cause) {

        this.cause = cause;
        this.snapshotSerializer = snapshotSerializer;
        this.snapshotRepository = snapshotRepository;

    }

    @Override
    public Cause getCause() {
        return this.cause;
    }

    public SnapshotSerializer<S, Player> getSnapshotSerializer() {
        return snapshotSerializer;
    }

    public SnapshotRepository<S, Key> getSnapshotRepository() {
        return snapshotRepository;
    }
}
