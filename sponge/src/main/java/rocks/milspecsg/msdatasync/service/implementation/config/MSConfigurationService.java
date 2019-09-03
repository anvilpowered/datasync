package rocks.milspecsg.msdatasync.service.implementation.config;

import com.google.common.reflect.TypeToken;
import rocks.milspecsg.msdatasync.service.config.ConfigKeys;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.config.DefaultConfig;
import rocks.milspecsg.msrepository.service.config.ApiConfigurationService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

@Singleton
public class MSConfigurationService extends ApiConfigurationService {

    @Inject
    public MSConfigurationService(@DefaultConfig(sharedRoot = false) ConfigurationLoader<CommentedConfigurationNode> configLoader) {
        super(configLoader);
    }

    @Override
    protected void initNodeTypeMap() {
        nodeTypeMap.put(ConfigKeys.ENABLED_SERIALIZERS_LIST, new TypeToken<List<String>>() {});

        nodeTypeMap.put(ConfigKeys.SERIALIZE_ON_JOIN_LEAVE, new TypeToken<Boolean>() {});

        nodeTypeMap.put(ConfigKeys.SERIALIZATION_TASK_INTERVAL_SECONDS, new TypeToken<Integer>() {});

        nodeTypeMap.put(ConfigKeys.MONGODB_HOSTNAME, new TypeToken<String>() {});
        nodeTypeMap.put(ConfigKeys.MONGODB_PORT, new TypeToken<Integer>() {});
        nodeTypeMap.put(ConfigKeys.MONGODB_DBNAME, new TypeToken<String>() {});
        nodeTypeMap.put(ConfigKeys.MONGODB_USERNAME, new TypeToken<String>() {});
        nodeTypeMap.put(ConfigKeys.MONGODB_PASSWORD, new TypeToken<String>() {});
        nodeTypeMap.put(ConfigKeys.MONGODB_USEAUTH, new TypeToken<Boolean>() {});
    }

    @Override
    protected void initVerificationMaps() {

        // set any numbers lower than -1 to -1
        Map<Predicate<Integer>, Function<Integer, Integer>> verifyThing = new HashMap<>();
        // if i < 1, set i to -1
        verifyThing.put(i -> i < 0, i -> 0);
        verifyThing.put(i -> i > 0 && i < 30, i -> 30);
        integerVerificationMap.put(ConfigKeys.SERIALIZATION_TASK_INTERVAL_SECONDS, verifyThing);
    }

    @Override
    protected void initDefaultMaps() {
        defaultListMap.put(ConfigKeys.ENABLED_SERIALIZERS_LIST, Arrays.asList("Experience", "GameMode", "Health", "Hunger", "Inventory"));

        defaultBooleanMap.put(ConfigKeys.SERIALIZE_ON_JOIN_LEAVE, true);

        defaultIntegerMap.put(ConfigKeys.SERIALIZATION_TASK_INTERVAL_SECONDS, 300);

        defaultStringMap.put(ConfigKeys.MONGODB_HOSTNAME, "localhost");

        defaultIntegerMap.put(ConfigKeys.MONGODB_PORT, 27017);

        defaultStringMap.put(ConfigKeys.MONGODB_DBNAME, "msdatasync");

        defaultStringMap.put(ConfigKeys.MONGODB_USERNAME, "");

        defaultStringMap.put(ConfigKeys.MONGODB_PASSWORD, "");

        defaultBooleanMap.put(ConfigKeys.MONGODB_USEAUTH, false);
    }

    @Override
    protected void initNodeNameMap() {
        nodeNameMap.put(ConfigKeys.ENABLED_SERIALIZERS_LIST, "enabledSerializers");

        nodeNameMap.put(ConfigKeys.SERIALIZE_ON_JOIN_LEAVE, "serializeOnJoinLeave");

        nodeNameMap.put(ConfigKeys.SERIALIZATION_TASK_INTERVAL_SECONDS, "serializationTaskIntervalSeconds");

        nodeNameMap.put(ConfigKeys.MONGODB_HOSTNAME, "mongoHostname");

        nodeNameMap.put(ConfigKeys.MONGODB_PORT, "mongoPort");

        nodeNameMap.put(ConfigKeys.MONGODB_DBNAME, "mongoDBName");

        nodeNameMap.put(ConfigKeys.MONGODB_USERNAME, "mongoUsername");

        nodeNameMap.put(ConfigKeys.MONGODB_PASSWORD, "mongoPassword");

        nodeNameMap.put(ConfigKeys.MONGODB_USEAUTH, "mongoUseAuth");
    }

    @Override
    protected void initNodeDescriptionMap() {
        nodeDescriptionMap.put(ConfigKeys.ENABLED_SERIALIZERS_LIST, "\nThings to sync to DB." +
            "\nAvailable: Experience, GameMode, Health, Hunger, Inventory");

        nodeDescriptionMap.put(ConfigKeys.SERIALIZE_ON_JOIN_LEAVE, "\nWhether MSDataSync should sync players to DB on join/leave");

        nodeDescriptionMap.put(ConfigKeys.SERIALIZATION_TASK_INTERVAL_SECONDS, "\nInterval for automatic serialization task. Set to 0 to disable, min 30");

        nodeDescriptionMap.put(ConfigKeys.MONGODB_HOSTNAME, "\nMongoDB hostname");

        nodeDescriptionMap.put(ConfigKeys.MONGODB_PORT, "\nMongoDB port");

        nodeDescriptionMap.put(ConfigKeys.MONGODB_DBNAME, "\nMongoDB database name");

        nodeDescriptionMap.put(ConfigKeys.MONGODB_USERNAME, "\nMongoDB username");

        nodeDescriptionMap.put(ConfigKeys.MONGODB_PASSWORD, "\nMongoDB password");

        nodeDescriptionMap.put(ConfigKeys.MONGODB_USEAUTH, "\nWhether to use authentication (username/password) for MongoDB connection");
    }
}
