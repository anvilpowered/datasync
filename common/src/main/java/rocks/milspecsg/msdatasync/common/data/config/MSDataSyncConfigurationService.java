package rocks.milspecsg.msdatasync.common.data.config;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import rocks.milspecsg.msdatasync.common.data.key.MSDataSyncKeys;
import rocks.milspecsg.msrepository.api.data.key.Keys;
import rocks.milspecsg.msrepository.common.data.config.CommonConfigurationService;

@Singleton
public class MSDataSyncConfigurationService extends CommonConfigurationService {

    @Inject
    public MSDataSyncConfigurationService(ConfigurationLoader<CommentedConfigurationNode> configLoader) {
        super(configLoader);
        defaultMap.put(Keys.MONGODB_DBNAME, "msdatasync");
    }

    @Override
    protected void initNodeNameMap() {
        super.initNodeNameMap();
        nodeNameMap.put(MSDataSyncKeys.SERIALIZE_ENABLED_SERIALIZERS, "serialize.enabledSerializers");
        nodeNameMap.put(MSDataSyncKeys.SERIALIZE_ON_JOIN_LEAVE, "serialize.serializeOnJoinLeave");
        nodeNameMap.put(MSDataSyncKeys.SERIALIZE_WAIT_FOR_SNAPSHOT_ON_JOIN, "serialize.waitForSnapshotOnJoin");
        nodeNameMap.put(MSDataSyncKeys.SNAPSHOT_MIN_COUNT, "snapshot.minCount");
        nodeNameMap.put(MSDataSyncKeys.SNAPSHOT_OPTIMIZATION_STRATEGY, "snapshot.optimizationStrategy");
        nodeNameMap.put(MSDataSyncKeys.SNAPSHOT_UPLOAD_INTERVAL_MINUTES, "snapshot.uploadInterval");
        nodeNameMap.put(MSDataSyncKeys.SERVER_NAME, "serverName");
    }

    @Override
    protected void initNodeDescriptionMap() {
        super.initNodeDescriptionMap();
        nodeDescriptionMap.put(MSDataSyncKeys.SERIALIZE_ENABLED_SERIALIZERS,
            "\nThings to sync to DB." +
                "\nAvailable: msdatasync:experience, msdatasync:gameMode, msdatasync:health, msdatasync:hunger, msdatasync:inventory"
        );
        nodeDescriptionMap.put(MSDataSyncKeys.SERIALIZE_ON_JOIN_LEAVE, "\nWhether MSDataSync should sync players to DB on join/leave");
        nodeDescriptionMap.put(MSDataSyncKeys.SERIALIZE_WAIT_FOR_SNAPSHOT_ON_JOIN,
            "\nWhether MSDataSync should wait for snapshots to be uploaded before downloading them.\n" +
                "Note: this option is highly recommended if you are running a multi-server environment like Velocity"
        );
        nodeDescriptionMap.put(MSDataSyncKeys.SNAPSHOT_MIN_COUNT, "\nMinimum number of snapshots to keep before deleting any");
        nodeDescriptionMap.put(MSDataSyncKeys.SNAPSHOT_OPTIMIZATION_STRATEGY,
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
        nodeDescriptionMap.put(MSDataSyncKeys.SNAPSHOT_UPLOAD_INTERVAL_MINUTES, "\nInterval for automatic serialization task. Set to 0 to disable, min 1, max 60. Recommended range 3-15");
        nodeDescriptionMap.put(MSDataSyncKeys.SERVER_NAME, "\nName of server. This value is attached with every snapshot made on this server");
    }
}
