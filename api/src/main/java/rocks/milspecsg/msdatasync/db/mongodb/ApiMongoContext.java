package rocks.milspecsg.msdatasync.db.mongodb;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.mapping.DefaultCreator;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.model.core.SerializedItemStack;
import rocks.milspecsg.msdatasync.service.config.ConfigKeys;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;
import rocks.milspecsg.msrepository.db.mongodb.MongoContext;

@Singleton
public class ApiMongoContext extends MongoContext {

    private ConfigurationService configurationService;

    @Inject
    public ApiMongoContext(ConfigurationService configurationService) {
        this.configurationService = configurationService;
        configurationService.addConfigLoadedListener(this::loadConfig);
        addConnectionOpenedListener(this::connectionOpened);
    }

    private void loadConfig(Object plugin) {
        closeConnection();

        String hostname = configurationService.getConfigString(ConfigKeys.MONGODB_HOSTNAME);
        int port = configurationService.getConfigInteger(ConfigKeys.MONGODB_PORT);
        String dbName = configurationService.getConfigString(ConfigKeys.MONGODB_DBNAME);
        String username = configurationService.getConfigString(ConfigKeys.MONGODB_USERNAME);
        String password = configurationService.getConfigString(ConfigKeys.MONGODB_PASSWORD);
        boolean useAuth = configurationService.getConfigBoolean(ConfigKeys.MONGODB_USE_AUTH);

        init(hostname, port, dbName, username, password, useAuth);
    }

    private void connectionOpened(Datastore datastore) {
        // if enabled, database must be a replica set
        if (configurationService.getConfigBoolean(ConfigKeys.SERIALIZE_WAIT_FOR_SNAPSHOT_ON_JOIN)) {
            boolean isReplicaSet = datastore.getMongo().getReplicaSetStatus() != null;
        }
    }

    @Override
    protected void initMorphiaMaps(Morphia morphia) {
        morphia.map(
            Member.class,
            SerializedItemStack.class
        );

        morphia.getMapper().getOptions().setObjectFactory(new DefaultCreator() {
            @Override
            protected ClassLoader getClassLoaderForClass() {
                return ApiMongoContext.this.getClass().getClassLoader();
            }
        });
    }

}
