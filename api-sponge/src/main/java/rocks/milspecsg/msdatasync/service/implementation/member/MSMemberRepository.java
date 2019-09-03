package rocks.milspecsg.msdatasync.service.implementation.member;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.service.member.ApiMemberRepository;
import rocks.milspecsg.msrepository.db.mongodb.MongoContext;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MSMemberRepository extends ApiMemberRepository<Member, Player, Key, User> {

    @Inject
    public MSMemberRepository(MongoContext mongoContext) {
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
    public Member generateEmpty() {
        return new Member();
    }

    @Override
    public UpdateOperations<Member> createUpdateOperations() {
        Optional<Datastore> optionalDatastore = mongoContext.getDataStore();
        return optionalDatastore.map(datastore -> datastore.createUpdateOperations(Member.class)).orElse(null);
    }

    @Override
    public Query<Member> asQuery() {
        Optional<Datastore> optionalDatastore = mongoContext.getDataStore();
        return optionalDatastore.map(datastore -> datastore.createQuery(Member.class)).orElse(null);
    }
}
