package rocks.milspecsg.msdatasync.db.mongodb;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.mongodb.morphia.Morphia;
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

    @Override
    protected void initMorphiaMaps(Morphia morphia) {
        morphia.map(
            Member.class,
            SerializedItemStack.class
        );
    }

}
