package rocks.milspecsg.msdatasync.service.implementation.data;

import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.scheduler.Task;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.model.core.Snapshot;
import rocks.milspecsg.msdatasync.service.data.ApiPlayerSerializer;

import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ApiSpongePlayerSerializer extends ApiPlayerSerializer<Member, Snapshot, Player, Key, User> {

    @Override
    public CompletableFuture<Optional<Snapshot>> serialize(Player player, String name) {
        Snapshot snapshot = snapshotRepository.generateEmpty();
        snapshot.name = name;
        serialize(snapshot, player);
        return snapshotRepository.insertOne(snapshot).thenApplyAsync(optionalSnapshot -> {
            if (!optionalSnapshot.isPresent()) {
                System.err.println("[MSDataSync] Snapshot upload failed for player " + player.getName() + "! Check your DB configuration!");
                return Optional.empty();
            }
            return memberRepository.addSnapshot(player.getUniqueId(), optionalSnapshot.get().getId()).join() ? optionalSnapshot : Optional.empty();
        });
    }

    @Override
    public CompletableFuture<Optional<Snapshot>> serialize(Player player) {
        return serialize(player, "Manual");
    }

    @Override
    public CompletableFuture<Optional<Snapshot>> deserialize(Player player, Object plugin, Snapshot snapshot) {
        if (snapshot == null) return CompletableFuture.completedFuture(Optional.empty());
        CompletableFuture<Optional<Snapshot>> result = new CompletableFuture<>();
        if (snapshot.keys == null) snapshot.keys = new HashMap<>();
        Task.builder().execute(() -> result.complete(deserialize(snapshot, player) ? Optional.of(snapshot) : Optional.empty())).submit(plugin);
        return result;
    }

    @Override
    public CompletableFuture<Optional<Snapshot>> deserialize(Player player, Object plugin) {
        return memberRepository.getLatestSnapshot(player.getUniqueId()).thenApplyAsync(optionalSnapshot -> {
            if (!optionalSnapshot.isPresent()) {
                System.err.println("[MSDataSync] Could not find snapshot for player " + player.getName() + "! Check your DB configuration!");
                return Optional.empty();
            }

            return deserialize(player, plugin, optionalSnapshot.get()).join();
        });

    }

    @Override
    public CompletableFuture<Optional<Snapshot>> sync(Player player, Object plugin) {
        return null;
    }
}
