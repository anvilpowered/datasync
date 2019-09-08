package rocks.milspecsg.msdatasync.service.implementation.member;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.model.core.Snapshot;

import java.util.Optional;

public class MSMemberRepository extends ApiSpongeMemberRepository {

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
