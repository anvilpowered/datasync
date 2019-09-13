package rocks.milspecsg.msdatasync.service.implementation.config;

import com.google.common.reflect.TypeToken;
import rocks.milspecsg.msdatasync.misc.SyncUtils;
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
    SyncUtils syncUtils;

    @Inject
    public MSConfigurationService(@DefaultConfig(sharedRoot = false) ConfigurationLoader<CommentedConfigurationNode> configLoader) {
        super(configLoader);
    }

    @Override
    protected void initNodeTypeMap() {
        nodeTypeMap.put(ConfigKeys.SERIALIZE_ENABLED_SERIALIZERS_LIST, new TypeToken<List<String>>() {
        });
        nodeTypeMap.put(ConfigKeys.SERIALIZE_ON_JOIN_LEAVE, new TypeToken<Boolean>() {
        });
        nodeTypeMap.put(ConfigKeys.SERIALIZE_WAIT_FOR_SNAPSHOT_ON_JOIN, new TypeToken<Boolean>() {
        });
        nodeTypeMap.put(ConfigKeys.MONGODB_HOSTNAME, new TypeToken<String>() {
        });
        nodeTypeMap.put(ConfigKeys.MONGODB_PORT, new TypeToken<Integer>() {
        });
        nodeTypeMap.put(ConfigKeys.MONGODB_DBNAME, new TypeToken<String>() {
        });
        nodeTypeMap.put(ConfigKeys.MONGODB_USERNAME, new TypeToken<String>() {
        });
        nodeTypeMap.put(ConfigKeys.MONGODB_PASSWORD, new TypeToken<String>() {
        });
        nodeTypeMap.put(ConfigKeys.MONGODB_USE_AUTH, new TypeToken<Boolean>() {
        });
        nodeTypeMap.put(ConfigKeys.SNAPSHOT_MIN_COUNT, new TypeToken<Integer>() {
        });
        nodeTypeMap.put(ConfigKeys.SNAPSHOT_OPTIMIZATION_STRATEGY, new TypeToken<List<String>>() {
        });
        nodeTypeMap.put(ConfigKeys.SNAPSHOT_UPLOAD_INTERVAL, new TypeToken<Integer>() {
        });
        nodeTypeMap.put(ConfigKeys.SERVER_NAME, new TypeToken<String>() {
        });
    }

    @Override
    protected void initVerificationMaps() {

        // set any numbers lower than 0 to 0
        Map<Predicate<Integer>, Function<Integer, Integer>> verifyInterval = new HashMap<>();
        // if i < 0, set i to 0
        verifyInterval.put(i -> i < 0, i -> 0);
        // if i > 60, set i to 60
        verifyInterval.put(i -> i > 60, i -> 60);
        integerVerificationMap.put(ConfigKeys.SNAPSHOT_UPLOAD_INTERVAL, verifyInterval);

        // set any numbers lower than 0 to 0
        Map<Predicate<Integer>, Function<Integer, Integer>> verifyMinCount = new HashMap<>();
        // if i < -1, set i to -1
        verifyMinCount.put(i -> i < -1, i -> -1);
        integerVerificationMap.put(ConfigKeys.SNAPSHOT_MIN_COUNT, verifyMinCount);


        Map<Predicate<List<?>>, Function<List<?>, List<?>>> verifyOptimizationStrategy = new HashMap<>();

        String verifyOptimizationStrategyErrorMessage =
            "[MSDataSync] Invalid format for snapshot.optimizationStrategy!\n" +
                "\tMust be \"x:y\" where\n" +
                "\t\t1) x and y are positive integers\n" +
                "\t\t2) x[n + 1] = x[n] * y[n]] where x[n] and y[n] are the values of x and y at line n respectively";
        verifyOptimizationStrategy.put(list -> !syncUtils.decodeOptimizationStrategy(list).isPresent(), list -> {
            System.err.println(verifyOptimizationStrategyErrorMessage);
            return list;
        });

        listVerificationMap.put(ConfigKeys.SNAPSHOT_OPTIMIZATION_STRATEGY, verifyOptimizationStrategy);

    }

    @Override
    protected void initDefaultMaps() {
        defaultListMap.put(ConfigKeys.SERIALIZE_ENABLED_SERIALIZERS_LIST, Arrays.asList("msdatasync:experience", "msdatasync:gameMode", "msdatasync:health", "msdatasync:hunger", "msdatasync:inventory"));
        defaultBooleanMap.put(ConfigKeys.SERIALIZE_ON_JOIN_LEAVE, true);
        defaultBooleanMap.put(ConfigKeys.SERIALIZE_WAIT_FOR_SNAPSHOT_ON_JOIN, false);
        defaultStringMap.put(ConfigKeys.MONGODB_HOSTNAME, "localhost");
        defaultIntegerMap.put(ConfigKeys.MONGODB_PORT, 27017);
        defaultStringMap.put(ConfigKeys.MONGODB_DBNAME, "msdatasync");
        defaultStringMap.put(ConfigKeys.MONGODB_USERNAME, "admin");
        defaultStringMap.put(ConfigKeys.MONGODB_PASSWORD, "password");
        defaultBooleanMap.put(ConfigKeys.MONGODB_USE_AUTH, false);
        defaultIntegerMap.put(ConfigKeys.SNAPSHOT_MIN_COUNT, 5);
        defaultListMap.put(ConfigKeys.SNAPSHOT_OPTIMIZATION_STRATEGY, Arrays.asList("60:24", "1440:7"));
        defaultIntegerMap.put(ConfigKeys.SNAPSHOT_UPLOAD_INTERVAL, 5);
        defaultStringMap.put(ConfigKeys.SERVER_NAME, "server");
    }

    @Override
    protected void initNodeNameMap() {
        nodeNameMap.put(ConfigKeys.SERIALIZE_ENABLED_SERIALIZERS_LIST, "serialize.enabledSerializers");
        nodeNameMap.put(ConfigKeys.SERIALIZE_ON_JOIN_LEAVE, "serialize.serializeOnJoinLeave");
        nodeNameMap.put(ConfigKeys.SERIALIZE_WAIT_FOR_SNAPSHOT_ON_JOIN, "serialize.waitForSnapshotOnJoin");
        nodeNameMap.put(ConfigKeys.MONGODB_HOSTNAME, "mongodb.hostname");
        nodeNameMap.put(ConfigKeys.MONGODB_PORT, "mongodb.port");
        nodeNameMap.put(ConfigKeys.MONGODB_DBNAME, "mongodb.dbName");
        nodeNameMap.put(ConfigKeys.MONGODB_USERNAME, "mongodb.username");
        nodeNameMap.put(ConfigKeys.MONGODB_PASSWORD, "mongodb.password");
        nodeNameMap.put(ConfigKeys.MONGODB_USE_AUTH, "mongodb.useAuth");
        nodeNameMap.put(ConfigKeys.SNAPSHOT_MIN_COUNT, "snapshot.minCount");
        nodeNameMap.put(ConfigKeys.SNAPSHOT_OPTIMIZATION_STRATEGY, "snapshot.optimizationStrategy");
        nodeNameMap.put(ConfigKeys.SNAPSHOT_UPLOAD_INTERVAL, "snapshot.uploadInterval");
        nodeNameMap.put(ConfigKeys.SERVER_NAME, "serverName");

    }

    @Override
    protected void initNodeDescriptionMap() {
        nodeDescriptionMap.put(ConfigKeys.SERIALIZE_ENABLED_SERIALIZERS_LIST, "\nThings to sync to DB." +
            "\nAvailable: msdatasync:experience, msdatasync:gameMode, msdatasync:health, msdatasync:hunger, msdatasync:inventory");
        nodeDescriptionMap.put(ConfigKeys.SERIALIZE_ON_JOIN_LEAVE, "\nWhether MSDataSync should sync players to DB on join/leave");
        nodeDescriptionMap.put(ConfigKeys.SERIALIZE_WAIT_FOR_SNAPSHOT_ON_JOIN,
            "\nWhether MSDataSync should wait for snapshots to be uploaded before downloading them.\n" +
            "Note: this option is highly recommended if you are running a multi-server environment like Velocity"
        );
        nodeDescriptionMap.put(ConfigKeys.MONGODB_HOSTNAME, "\nMongoDB hostname");
        nodeDescriptionMap.put(ConfigKeys.MONGODB_PORT, "\nMongoDB port");
        nodeDescriptionMap.put(ConfigKeys.MONGODB_DBNAME, "\nMongoDB database name");
        nodeDescriptionMap.put(ConfigKeys.MONGODB_USERNAME, "\nMongoDB username");
        nodeDescriptionMap.put(ConfigKeys.MONGODB_PASSWORD, "\nMongoDB password");
        nodeDescriptionMap.put(ConfigKeys.MONGODB_USE_AUTH, "\nWhether to use authentication (username/password) for MongoDB connection");
        nodeDescriptionMap.put(ConfigKeys.SNAPSHOT_MIN_COUNT, "\nMinimum number of snapshots to keep before deleting any");
        nodeDescriptionMap.put(ConfigKeys.SNAPSHOT_OPTIMIZATION_STRATEGY,
            "\nSnapshot optimization strategy. Format:\n" +
                "Must be \"x:y\" where\n" +
                "\t1) x and y are positive integers (x is minutes)\n" +
                "\t2) x[n + 1] = x[n] * y[n] where x[n] and y[n] are the values of x and y at line n respectively\n" +
                "Default of \"60:24\",\"1440:7\" will:\n" +
                "\t1) not remove any snapshots within the last hour\n" +
                "\t2) keep one snapshot per hour (not including first hour) for 24 hours\n" +
                "\t3) keep one snapshot per day (not including first day) for 7 days\n" +
                "\t4) Delete all snapshots older than 7 days (keeping a minimum of minCount)"
        );
        nodeDescriptionMap.put(ConfigKeys.SNAPSHOT_UPLOAD_INTERVAL, "\nInterval for automatic serialization task. Set to 0 to disable, min 1, max 60. Recommended range 3-15");

        nodeDescriptionMap.put(ConfigKeys.SERVER_NAME, "\nName of server. This value is attached with every snapshot made on this server");
    }
}
