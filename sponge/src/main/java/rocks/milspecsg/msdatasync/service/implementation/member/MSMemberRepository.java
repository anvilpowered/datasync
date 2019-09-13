package rocks.milspecsg.msdatasync.service.implementation.member;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msrepository.db.mongodb.MongoContext;

import java.util.Optional;
import java.util.UUID;

@Singleton
public class MSMemberRepository extends ApiSpongeMemberRepository {

    @Inject
    public MSMemberRepository(MongoContext mongoContext) {
        super(mongoContext);
//        mongoContext.addConnectionOpenedListener(this::connectionOpened);
//        mongoContext.addConnectionClosedListener(this::connectionClosed);
    }

//    private void connectionOpened(Datastore datastore) {
//        if (changeIterator != null) connectionClosed(datastore);
//        changeIterator = datastore.getMongo().getDatabase("members").watch().iterator();
//    }
//
//    private void connectionClosed(Datastore datastore) {
//        if (changeIterator == null) return;
//        changeIterator.close();
//        changeIterator = null;
//    }

//    private MongoCursor<ChangeStreamDocument<Document>> changeIterator = null;

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

//    public Optional<Member> getNext() {
//        return mongoContext.getDataStore().flatMap(datastore -> {
//            Member member = generateEmpty();
//            Document document = changeIterator.next().getFullDocument();
//            if (document == null) return Optional.empty();
//            member.setId(document.getObjectId("_id"));
//            member.snapshotIds = document.getList("snapshotIds", ObjectId.class);
//            member.userUUID = document.get("userUUID", UUID.class);
//            return Optional.of(member);
//        });
//    }
}
