package rocks.milspecsg.msdatasync.service.member;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import rocks.milspecsg.msdatasync.api.keys.DataKeyService;
import rocks.milspecsg.msdatasync.api.member.MemberRepository;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msrepository.db.mongodb.MongoContext;
import rocks.milspecsg.msrepository.model.Dbo;
import rocks.milspecsg.msrepository.service.ApiRepository;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class ApiMemberRepository<M extends Member, P, K, User> extends ApiRepository<M> implements MemberRepository<M, P, K, User> {

    @Inject
    DataKeyService<K> dataKeyService;

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

    @Override
    public CompletableFuture<Optional<M>> getOne(ObjectId id) {
        return CompletableFuture.supplyAsync(() -> Optional.ofNullable(asQuery(id).get()));
    }

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


    @Override
    public CompletableFuture<Boolean> setMemberKey(Member member, K key, Optional<?> optionalValue) {
        return CompletableFuture.supplyAsync(() -> {
            if (!optionalValue.isPresent()) {
                return false;
            }

            Optional<String> optionalName = dataKeyService.getName(key);
            if (!optionalName.isPresent()) {
                return false;
            }
            member.keys.put(optionalName.get(), optionalValue.get());
            return true;
        });
    }

    @Override
    public CompletableFuture<Optional<?>> getMemberKey(Member member, K key) {
        return CompletableFuture.supplyAsync(() -> {

            Optional<String> optionalName = dataKeyService.getName(key);
            if (!optionalName.isPresent()) return Optional.empty();
            if (!member.keys.containsKey(optionalName.get())) return Optional.empty();

            return Optional.ofNullable(member.keys.get(optionalName.get()));
        });
    }

}
