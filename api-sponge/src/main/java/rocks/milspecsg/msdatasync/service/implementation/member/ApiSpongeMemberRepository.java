package rocks.milspecsg.msdatasync.service.implementation.member;

import com.google.inject.Inject;
import com.mongodb.WriteResult;
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
import rocks.milspecsg.msrepository.db.mongodb.MongoContext;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public abstract class ApiSpongeMemberRepository extends ApiMemberRepository<Member, Snapshot, Key, User> {

    @Inject
    public ApiSpongeMemberRepository(MongoContext mongoContext) {
        super(mongoContext);
    }

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
    public CompletableFuture<List<ObjectId>> getSnapshotIds(Query<Member> query) {
        return CompletableFuture.supplyAsync(() -> {
            Member member = query.project("snapshotIds", true).get();

            if (member == null || member.snapshotIds == null) {
                return new ArrayList<>();
            }

            return member.snapshotIds;
        });
    }

    @Override
    public CompletableFuture<List<ObjectId>> getSnapshotIds(ObjectId id) {
        return getSnapshotIds(asQuery(id));
    }

    @Override
    public CompletableFuture<List<ObjectId>> getSnapshotIds(UUID userUUID) {
        return getSnapshotIds(asQuery(userUUID));
    }

    @Override
    public CompletableFuture<List<Date>> getSnapshotDates(Query<Member> query) {
        return getSnapshotIds(query).thenApplyAsync(objectIds -> objectIds.stream().map(ObjectId::getDate).collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<List<Date>> getSnapshotDates(ObjectId id) {
        return getSnapshotDates(asQuery(id));
    }

    @Override
    public CompletableFuture<List<Date>> getSnapshotDates(UUID userUUID) {
        return getSnapshotDates(asQuery(userUUID));
    }

    @Override
    public CompletableFuture<Boolean> deleteSnapshot(Query<Member> query, ObjectId snapshotId) {
        return CompletableFuture.supplyAsync(() -> {
            UpdateOperations<Member> updateOperations = createUpdateOperations().removeAll("snapshotIds", snapshotId);
            if (!mongoContext.getDataStore().map(datastore -> datastore.update(query, updateOperations).getUpdatedCount() > 0).orElse(false)) {
                return false;
            }
            // now remove snapshot from snapshot collection

            WriteResult wr = snapshotRepository.deleteOne(snapshotId).join();
            return wr.wasAcknowledged() && wr.getN() > 0;
        });
    }

    @Override
    public CompletableFuture<Boolean> deleteSnapshot(ObjectId id, ObjectId snapshotId) {
        return deleteSnapshot(asQuery(id), snapshotId);
    }

    @Override
    public CompletableFuture<Boolean> deleteSnapshot(UUID userUUID, ObjectId snapshotId) {
        return deleteSnapshot(asQuery(userUUID), snapshotId);
    }

    @Override
    public CompletableFuture<Boolean> addSnapshot(Query<Member> query, ObjectId snapshotId) {
        return CompletableFuture.supplyAsync(() -> {
            UpdateOperations<Member> updateOperations = createUpdateOperations().addToSet("snapshotIds", snapshotId);
            return mongoContext.getDataStore().map(datastore -> datastore.update(query, updateOperations).getUpdatedCount() > 0).orElse(false);
        });
    }

    @Override
    public CompletableFuture<Boolean> addSnapshot(ObjectId id, ObjectId snapshotId) {
        return addSnapshot(asQuery(id), snapshotId);
    }

    @Override
    public CompletableFuture<Boolean> addSnapshot(UUID userUUID, ObjectId snapshotId) {
        return getOneOrGenerate(userUUID).thenApplyAsync(optionalMember -> {
            if (!optionalMember.isPresent()) {
                return false;
            }
            return addSnapshot(asQuery(userUUID), snapshotId).join();
        });
    }

    @Override
    public CompletableFuture<Optional<Snapshot>> getSnapshot(Query<Member> query, Date date) {
        return getSnapshotIds(query)
            .thenApplyAsync(objectIds -> objectIds.stream()
                .filter(objectId -> objectId.getDate().equals(date))
                .findFirst()
                .flatMap(objectId -> snapshotRepository.getOne(objectId).join())
            );
    }

    @Override
    public CompletableFuture<Optional<Snapshot>> getSnapshot(ObjectId id, Date date) {
        return getSnapshot(asQuery(id), date);
    }

    @Override
    public CompletableFuture<Optional<Snapshot>> getSnapshot(UUID userUUID, Date date) {
        return getSnapshot(asQuery(userUUID), date);
    }

    @Override
    public CompletableFuture<List<ObjectId>> getClosestSnapshots(Query<Member> query, Date date) {
        return getSnapshotIds(query).thenApplyAsync(objectIds -> {
                Optional<ObjectId> closestBefore = Optional.empty();
                Optional<ObjectId> closestAfter = Optional.empty();
                Optional<ObjectId> same = Optional.empty();

                for (ObjectId objectId : objectIds) {
                    Date toTest = objectId.getDate();

                    if (toTest.equals(date)) {
                        same = Optional.of(objectId);
                    } else if (toTest.before(date) && (!closestBefore.isPresent() || toTest.after(closestBefore.get().getDate()))) {
                        closestBefore = Optional.of(objectId);
                    } else if (toTest.after(date) && (!closestAfter.isPresent() || toTest.before(closestAfter.get().getDate()))) {
                        closestAfter = Optional.of(objectId);
                    }
                }

                List<ObjectId> toReturn = new ArrayList<>();
                closestBefore.ifPresent(toReturn::add);
                same.ifPresent(toReturn::add);
                closestAfter.ifPresent(toReturn::add);
                return toReturn;
            });
    }

    @Override
    public CompletableFuture<List<ObjectId>> getClosestSnapshots(ObjectId id, Date date) {
        return getClosestSnapshots(asQuery(id), date);
    }

    @Override
    public CompletableFuture<List<ObjectId>> getClosestSnapshots(UUID userUUID, Date date) {
        return getClosestSnapshots(asQuery(userUUID), date);
    }

    @Override
    public CompletableFuture<Optional<Snapshot>> getLatestSnapshot(ObjectId id) {
        return getOne(id).thenApplyAsync(optionalMember -> optionalMember.flatMap(member -> snapshotRepository.getOne(member.snapshotIds.get(member.snapshotIds.size() - 1)).join()));
    }

    @Override
    public CompletableFuture<Optional<Snapshot>> getLatestSnapshot(UUID userUUID) {
        return getOne(userUUID).thenApplyAsync(optionalMember -> optionalMember.flatMap(member -> snapshotRepository.getOne(member.snapshotIds.get(member.snapshotIds.size() - 1)).join()));
    }

}
