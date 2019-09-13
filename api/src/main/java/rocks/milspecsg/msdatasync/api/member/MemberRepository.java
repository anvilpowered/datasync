package rocks.milspecsg.msdatasync.api.member;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.model.core.Snapshot;
import rocks.milspecsg.msrepository.api.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface MemberRepository<M extends Member, S extends Snapshot, U> extends Repository<M> {

    default String getDefaultIdentifierSingularUpper() {
        return "Member";
    }

    default String getDefaultIdentifierPluralUpper() {
        return "Members";
    }

    default String getDefaultIdentifierSingularLower() {
        return "member";
    }

    default String getDefaultIdentifierPluralLower() {
        return "members";
    }

    /**
     * Gets the corresponding {@code Member} from the database.
     * If not present, creates a new one and saves it to the database
     *
     * @param userUUID Mojang issued {@code uuid} of {@code User} to getRequiredRankIndex corresponding {@code Member}
     * @return a ready-to-use {@code Member} that corresponds with the given {@code uuid}
     */
    CompletableFuture<Optional<M>> getOneOrGenerate(UUID userUUID);

    CompletableFuture<Optional<M>> getOne(UUID userUUID);

    CompletableFuture<Optional<ObjectId>> getId(UUID userUUID);

    CompletableFuture<Optional<UUID>> getUUID(ObjectId id);

    Optional<U> getUser(UUID userUUID);

    Optional<U> getUser(String lastKnownName);

    CompletableFuture<List<ObjectId>> getSnapshotIds(Query<M> query);

    CompletableFuture<List<ObjectId>> getSnapshotIds(ObjectId id);

    CompletableFuture<List<ObjectId>> getSnapshotIds(UUID userUUID);

    CompletableFuture<List<Date>> getSnapshotDates(Query<M> query);

    CompletableFuture<List<Date>> getSnapshotDates(ObjectId id);

    CompletableFuture<List<Date>> getSnapshotDates(UUID userUUID);

    CompletableFuture<Boolean> deleteSnapshot(Query<M> query, ObjectId snapshotId);

    CompletableFuture<Boolean> deleteSnapshot(ObjectId id, ObjectId snapshotId);

    CompletableFuture<Boolean> deleteSnapshot(UUID userUUID, ObjectId snapshotId);

    CompletableFuture<Boolean> addSnapshot(Query<M> query, ObjectId snapshotId);

    CompletableFuture<Boolean> addSnapshot(ObjectId id, ObjectId snapshotId);

    CompletableFuture<Boolean> addSnapshot(UUID userUUID, ObjectId snapshotId);

    CompletableFuture<Optional<S>> getSnapshot(Query<M> query, Date date);

    CompletableFuture<Optional<S>> getSnapshot(ObjectId id, Date date);

    CompletableFuture<Optional<S>> getSnapshot(UUID userUUID, Date date);

    CompletableFuture<List<ObjectId>> getClosestSnapshots(Query<M> query, Date date);

    CompletableFuture<List<ObjectId>> getClosestSnapshots(ObjectId id, Date date);

    CompletableFuture<List<ObjectId>> getClosestSnapshots(UUID userUUID, Date date);

    CompletableFuture<Optional<S>> getLatestSnapshot(ObjectId id);

    CompletableFuture<Optional<S>> getLatestSnapshot(UUID userUUID);

    CompletableFuture<Optional<U>> getUser(ObjectId id);

    //TODO: move this method declaration to ApiRepository in repo MSRepository
//    Optional<Member> getNext();

    Query<M> asQuery(UUID userUUID);

}
