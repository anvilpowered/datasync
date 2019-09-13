package rocks.milspecsg.msdatasync.service.member;

import com.google.inject.Inject;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import rocks.milspecsg.msdatasync.api.member.MemberRepository;
import rocks.milspecsg.msdatasync.api.snapshot.SnapshotRepository;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.model.core.Snapshot;
import rocks.milspecsg.msrepository.db.mongodb.MongoContext;
import rocks.milspecsg.msrepository.model.Dbo;
import rocks.milspecsg.msrepository.service.ApiRepository;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class ApiMemberRepository<M extends Member, S extends Snapshot, K, U> extends ApiRepository<M> implements MemberRepository<M, S, U> {

    @Inject
    protected SnapshotRepository<S, K> snapshotRepository;

    @Inject
    public ApiMemberRepository(MongoContext mongoContext) {
        super(mongoContext);
    }

    @Override
    public CompletableFuture<Optional<M>> getOneOrGenerate(UUID userUUID) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<M> optionalMember = getOne(userUUID).join();
            if (optionalMember.isPresent()) return optionalMember;
            // if there isn't one already, create a new one
            M member = generateEmpty();
            member.userUUID = userUUID;
            return insertOne(member).join();
        });
    }

//    @Override
//    public CompletableFuture<Optional<M>> getOne(ObjectId id) {
//        return CompletableFuture.supplyAsync(() -> Optional.ofNullable(asQuery(id).get()));
//    }

    @Override
    public CompletableFuture<Optional<M>> getOne(UUID userUUID) {
        return CompletableFuture.supplyAsync(() -> Optional.ofNullable(asQuery(userUUID).get()));
    }

    @Override
    public CompletableFuture<Optional<ObjectId>> getId(UUID userUUID) {
        return CompletableFuture.supplyAsync(() -> getOneOrGenerate(userUUID).join().map(Dbo::getId));
    }

    @Override
    public CompletableFuture<Optional<UUID>> getUUID(ObjectId id) {
        return CompletableFuture.supplyAsync(() -> getOne(id).join().map(member -> member.userUUID));
    }

    @Override
    public Query<M> asQuery(UUID userUUID) {
        return asQuery().field("userUUID").equal(userUUID);
    }

}
