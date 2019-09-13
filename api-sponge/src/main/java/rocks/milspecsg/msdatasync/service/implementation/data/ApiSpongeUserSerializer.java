package rocks.milspecsg.msdatasync.service.implementation.data;

import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.scheduler.Task;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.model.core.Snapshot;
import rocks.milspecsg.msdatasync.service.config.ConfigKeys;
import rocks.milspecsg.msdatasync.service.data.ApiUserSerializer;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ApiSpongeUserSerializer extends ApiUserSerializer<Member, Snapshot, Key, User> {

    @Inject
    ConfigurationService configurationService;

    @Override
    public CompletableFuture<Optional<Snapshot>> serialize(User user, String name) {
        Snapshot snapshot = snapshotRepository.generateEmpty();
        snapshot.name = name;
        snapshot.server = configurationService.getConfigString(ConfigKeys.SERVER_NAME);
        serialize(snapshot, user);
        return snapshotRepository.insertOne(snapshot).thenApplyAsync(optionalSnapshot -> {
            if (!optionalSnapshot.isPresent()) {
                System.err.println("[MSDataSync] Snapshot upload failed for user " + user.getName() + "! Check your DB configuration!");
                return Optional.empty();
            }
            return memberRepository.addSnapshot(user.getUniqueId(), optionalSnapshot.get().getId()).join() ? optionalSnapshot : Optional.empty();
        });
    }

    @Override
    public CompletableFuture<Optional<Snapshot>> serialize(User user) {
        return serialize(user, "Manual");
    }

    @Override
    public CompletableFuture<Optional<Snapshot>> deserialize(User user, Object plugin, Snapshot snapshot) {
        if (snapshot == null) return CompletableFuture.completedFuture(Optional.empty());
        CompletableFuture<Optional<Snapshot>> result = new CompletableFuture<>();
        if (snapshot.keys == null) snapshot.keys = new HashMap<>();
        Task.builder().execute(() -> result.complete(deserialize(snapshot, user) ? Optional.of(snapshot) : Optional.empty())).submit(plugin);
        return result;
    }

    @Override
    public CompletableFuture<Optional<Snapshot>> deserialize(User user, Object plugin) {
        CompletableFuture<Void> waitForSnapshot;
        if (configurationService.getConfigBoolean(ConfigKeys.SERIALIZE_WAIT_FOR_SNAPSHOT_ON_JOIN)) {
            waitForSnapshot = CompletableFuture.runAsync(() -> {
//                while (true){
//                    if (!memberRepository.getNext().map(member -> {
//                        System.out.println(member.userUUID);
//                        return member.userUUID.equals(user.getUniqueId());
//                    }).orElse(false))
//                        break;
//                }
                try {
                    Thread.sleep(7000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        } else {
            waitForSnapshot = CompletableFuture.completedFuture(null);
        }
        return waitForSnapshot.thenApplyAsync(v -> memberRepository.getLatestSnapshot(user.getUniqueId()).thenApplyAsync(optionalSnapshot -> {
            if (!optionalSnapshot.isPresent()) {
                System.err.println("[MSDataSync] Could not find snapshot for user " + user.getName() + "! Check your DB configuration!");
                return Optional.<Snapshot>empty();
            }
            return deserialize(user, plugin, optionalSnapshot.get()).join();
        }).join());
    }

    @Override
    public CompletableFuture<Optional<Snapshot>> sync(User user, Object plugin) {
        return null;
    }
}
