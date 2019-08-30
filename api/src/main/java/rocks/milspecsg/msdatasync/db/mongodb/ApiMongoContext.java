package rocks.milspecsg.msdatasync.db.mongodb;

import com.google.inject.Singleton;
import org.mongodb.morphia.Morphia;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.model.core.SerializedItemStack;
import rocks.milspecsg.msrepository.db.mongodb.MongoContext;

@Singleton
public class ApiMongoContext extends MongoContext {

    @Override
    protected String getDbName() {
        return "msdatasync";
    }

    @Override
    protected void initMorphiaMaps(Morphia morphia) {
        morphia.map(
            Member.class,
            SerializedItemStack.class
        );
    }

}
