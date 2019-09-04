package rocks.milspecsg.msdatasync.service.implementation.data;

import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.scheduler.Task;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.model.core.Snapshot;
import rocks.milspecsg.msdatasync.service.data.ApiPlayerSerializer;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class ApiSpongePlayerSerializer<M extends Member, S extends Snapshot> extends ApiPlayerSerializer<M, S, Player, Key, User> {

    @Override
    public CompletableFuture<Boolean> serialize(Player player) {
        S snapshot = snapshotRepository.generateEmpty();
        serialize(snapshot, player);
        if (snapshot.keys == null) snapshot.keys = new HashMap<>();
        return snapshotRepository.insertOne(snapshot).thenApplyAsync(optionalSnapshot -> {
            if (!optionalSnapshot.isPresent()) {
                System.err.println("[MSDataSync] Snapshot upload failed for player " + player.getName() + "! Check your DB configuration!");
                return false;
            }
            return memberRepository.addSnapshot(player.getUniqueId(), optionalSnapshot.get().getId()).join();
        });
    }

    @Override
    public CompletableFuture<Boolean> deserialize(Player player, Object plugin) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();

        memberRepository.getLatestSnapshot(player.getUniqueId()).thenAcceptAsync(optionalSnapshot -> {
            if (!optionalSnapshot.isPresent()) {
                System.err.println("[MSDataSync] Could not find snapshot for player " + player.getName() + "! Check your DB configuration!");
                result.complete(false);
                return;
            }

            S snapshot = optionalSnapshot.get();
            if (snapshot.keys == null) snapshot.keys = new HashMap<>();
            Task.builder().execute(() -> result.complete(deserialize(snapshot, player))).submit(plugin);
        });

        return result;
    }

    @Override
    public CompletableFuture<Boolean> sync(Player player, Object plugin) {
        return null;
    }
}
