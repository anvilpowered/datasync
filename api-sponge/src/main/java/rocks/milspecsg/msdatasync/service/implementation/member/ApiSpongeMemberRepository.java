package rocks.milspecsg.msdatasync.service.implementation.member;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.model.core.Snapshot;
import rocks.milspecsg.msdatasync.service.member.ApiMemberRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class ApiSpongeMemberRepository<M extends Member, S extends Snapshot> extends ApiMemberRepository<M, S, Key, User> {

    @Override
    public Optional<User> getUser(UUID userUUID) {
        return Sponge.getServiceManager().provide(UserStorageService.class).flatMap(u -> u.get(userUUID));
    }

    @Override
    public Optional<User> getUser(String lastKnownName) {
        return Sponge.getServiceManager().provide(UserStorageService.class).flatMap(u -> u.get(lastKnownName));
    }

    @Override
    public CompletableFuture<Optional<User>> getUser(ObjectId id) {
        return CompletableFuture.supplyAsync(() -> getUUID(id).join().flatMap(this::getUser));
    }

    @Override
    public CompletableFuture<List<ObjectId>> getSnapshotIds(Query<M> query) {
        return null;
    }

    @Override
    public CompletableFuture<List<ObjectId>> getSnapshotIds(ObjectId id) {
        return null;
    }

    @Override
    public CompletableFuture<List<ObjectId>> getSnapshotIds(UUID userUUID) {
        return null;
    }

    @Override
    public CompletableFuture<List<Date>> getSnapshotDates(Query<M> query) {
        return null;
    }

    @Override
    public CompletableFuture<List<Date>> getSnapshotDates(ObjectId id) {
        return null;
    }

    @Override
    public CompletableFuture<List<Date>> getSnapshotDates(UUID userUUID) {
        return null;
    }

    @Override
    public CompletableFuture<Boolean> addSnapshot(Query<M> query, ObjectId snapshotId) {
        return CompletableFuture.supplyAsync(() -> {
            UpdateOperations<M> updateOperations = createUpdateOperations().addToSet("snapshotIds", snapshotId);

            return mongoContext.getDataStore().map(datastore -> datastore.update(query, updateOperations).getUpdatedCount() > 0).orElse(false);
        });
    }

    @Override
    public CompletableFuture<Boolean> addSnapshot(ObjectId id, ObjectId snapshotId) {
        return addSnapshot(asQuery(id), snapshotId);
    }

    @Override
    public CompletableFuture<Boolean> addSnapshot(UUID userUUID, ObjectId snapshotId) {
        return addSnapshot(asQuery(userUUID), snapshotId);
    }

    @Override
    public CompletableFuture<Optional<S>> getSnapshot(Query<M> query, Date date) {
        return null;
    }

    @Override
    public CompletableFuture<Optional<S>> getSnapshot(ObjectId id, Date date) {
        return null;
    }

    @Override
    public CompletableFuture<Optional<S>> getSnapshot(UUID userUUID, Date date) {
        return null;
    }

    @Override
    public CompletableFuture<Optional<List<ObjectId>>> getClosestSnapshots(ObjectId id, Date date) {
        return null;
    }

    @Override
    public CompletableFuture<Optional<S>> getLatestSnapshot(ObjectId id) {
        return getOne(id).thenApplyAsync(optionalMember -> optionalMember.flatMap(member -> snapshotRepository.getOne(member.snapshotIds.get(member.snapshotIds.size() - 1)).join()));
    }

    @Override
    public CompletableFuture<Optional<S>> getLatestSnapshot(UUID userUUID) {
        return getOne(userUUID).thenApplyAsync(optionalMember -> optionalMember.flatMap(member -> snapshotRepository.getOne(member.snapshotIds.get(member.snapshotIds.size() - 1)).join()));
    }

}
