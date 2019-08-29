package rocks.milspecsg.msdatasync.api.member;

import com.google.common.reflect.TypeToken;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msrepository.api.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface MemberRepository<M extends Member, P, K, U> extends Repository<M> {

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

    CompletableFuture<Optional<U>> getUser(ObjectId id);

    CompletableFuture<Boolean> setMemberKey(Member member, K key, Optional<?> optionalValue);

    CompletableFuture<Optional<?>> getMemberKey(Member member, K key);

    Query<M> asQuery(UUID userUUID);

}
