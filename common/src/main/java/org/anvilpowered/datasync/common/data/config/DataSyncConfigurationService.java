/*
 *   DataSync - AnvilPowered
 *   Copyright (C) 2020 Cableguy20
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.datasync.common.data.config;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.anvilpowered.anvil.api.data.key.Keys;
import org.anvilpowered.anvil.base.data.config.BaseConfigurationService;
import org.anvilpowered.datasync.common.data.key.DataSyncKeys;

@Singleton
public class DataSyncConfigurationService extends BaseConfigurationService {

    @Inject
    public DataSyncConfigurationService(ConfigurationLoader<CommentedConfigurationNode> configLoader) {
        super(configLoader);
        defaultMap.put(Keys.MONGODB_DBNAME, "datasync");
    }

    @Override
    protected void initNodeNameMap() {
        super.initNodeNameMap();
        nodeNameMap.put(DataSyncKeys.SERIALIZE_ENABLED_SERIALIZERS, "serialize.enabledSerializers");
        nodeNameMap.put(DataSyncKeys.DESERIALIZE_ON_JOIN, "serialize.deserializeOnJoin");
        nodeNameMap.put(DataSyncKeys.SERIALIZE_ON_DEATH, "serialize.serializeOnDeath");
        nodeNameMap.put(DataSyncKeys.SERIALIZE_ON_DISCONNECT, "serialize.serializeOnDisconnect");
        nodeNameMap.put(DataSyncKeys.SERIALIZE_WAIT_FOR_SNAPSHOT_ON_JOIN, "serialize.waitForSnapshotOnJoin");
        nodeNameMap.put(DataSyncKeys.SNAPSHOT_MIN_COUNT, "snapshot.minCount");
        nodeNameMap.put(DataSyncKeys.SNAPSHOT_OPTIMIZATION_STRATEGY, "snapshot.optimizationStrategy");
        nodeNameMap.put(DataSyncKeys.SNAPSHOT_UPLOAD_INTERVAL_MINUTES, "snapshot.uploadInterval");
    }

    @Override
    protected void initNodeDescriptionMap() {
        super.initNodeDescriptionMap();
        nodeDescriptionMap.put(DataSyncKeys.SERIALIZE_ENABLED_SERIALIZERS,
            "\nThings to sync to DB." +
                "\nAvailable: msdatasync:experience, msdatasync:gameMode, msdatasync:health, msdatasync:hunger, msdatasync:inventory"
        );
        nodeDescriptionMap.put(DataSyncKeys.DESERIALIZE_ON_JOIN, "\nWhether MSDataSync should deserialize players on join");
        nodeDescriptionMap.put(DataSyncKeys.SERIALIZE_ON_DEATH, "\nWhether MSDataSync should serialize players to DB on death");
        nodeDescriptionMap.put(DataSyncKeys.SERIALIZE_ON_DISCONNECT, "\nWhether MSDataSync should serialize players to DB on disconnect");
        nodeDescriptionMap.put(DataSyncKeys.SERIALIZE_WAIT_FOR_SNAPSHOT_ON_JOIN,
            "\nWhether MSDataSync should wait for snapshots to be uploaded before downloading them.\n" +
                "Note: this option is highly recommended if you are running a multi-server environment like Velocity"
        );
        nodeDescriptionMap.put(DataSyncKeys.SNAPSHOT_MIN_COUNT, "\nMinimum number of snapshots to keep before deleting any");
        nodeDescriptionMap.put(DataSyncKeys.SNAPSHOT_OPTIMIZATION_STRATEGY,
            "\nSnapshot optimization strategy. Format:\n" +
                "Must be \"x:y\" where\n" +
                "\t1) x and y are positive integers (x is minutes)\n" +
                "\t2) x[n + 1] = x[n] * y[n] where x[n] and y[n] are the values of x and y at line n respectively\n" +
                "Default of \"60:24\",\"1440:7\" will:\n" +
                "\t1) not remove any snapshots within the last hour\n" +
                "\t2) keep one snapshot per hour (not including first hour) for 24 hours\n" +
                "\t3) keep one snapshot per day (not including first day) for 7 days\n" +
                "\t4) delete all snapshots older than 7 days (keeping a minimum of minCount)"
        );
        nodeDescriptionMap.put(DataSyncKeys.SNAPSHOT_UPLOAD_INTERVAL_MINUTES, "\nInterval for automatic serialization task. Set to 0 to disable, min 1, max 60. Recommended range 3-15");
    }
}
