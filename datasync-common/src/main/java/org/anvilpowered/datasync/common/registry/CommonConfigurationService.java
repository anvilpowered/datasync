/*
 *   DataSync - AnvilPowered
 *   Copyright (C) 2020 Cableguy20
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.datasync.common.registry;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.anvilpowered.anvil.api.registry.Keys;
import org.anvilpowered.anvil.base.registry.BaseConfigurationService;
import org.anvilpowered.datasync.api.registry.DataSyncKeys;

@Singleton
public class CommonConfigurationService extends BaseConfigurationService {

    @Inject
    public CommonConfigurationService(ConfigurationLoader<CommentedConfigurationNode> configLoader) {
        super(configLoader);
        setDefault(Keys.DATA_DIRECTORY, "datasync");
        setDefault(Keys.MONGODB_DBNAME, "datasync");
        withDefault();
        setName(DataSyncKeys.SERIALIZE_ENABLED_SERIALIZERS, "serialize.enabledSerializers");
        setName(DataSyncKeys.DESERIALIZE_ON_JOIN, "serialize.deserializeOnJoin");
        setName(DataSyncKeys.DESERIALIZE_ON_JOIN_DELAY_MILLIS, "serialize.deserializeOnJoinDelayMillis");
        setName(DataSyncKeys.SERIALIZE_ON_DEATH, "serialize.serializeOnDeath");
        setName(DataSyncKeys.SERIALIZE_ON_DISCONNECT, "serialize.serializeOnDisconnect");
        setName(DataSyncKeys.SNAPSHOT_MIN_COUNT, "snapshot.minCount");
        setName(DataSyncKeys.SNAPSHOT_OPTIMIZATION_STRATEGY, "snapshot.optimizationStrategy");
        setName(DataSyncKeys.SNAPSHOT_UPLOAD_INTERVAL_MINUTES, "snapshot.uploadInterval");
        setDescription(DataSyncKeys.SERIALIZE_ENABLED_SERIALIZERS,
            "\nThings to sync to DB." +
                "\nAvailable: datasync:experience, datasync:gameMode, datasync:health, datasync:hunger, datasync:inventory"
        );
        setDescription(DataSyncKeys.DESERIALIZE_ON_JOIN, "\nWhether DataSync should deserialize players on join");
        setDescription(DataSyncKeys.DESERIALIZE_ON_JOIN_DELAY_MILLIS,
            "\nThe number of milliseconds DataSync should wait before downloading a snapshot on join."
                + "\nNote: This only takes effect if deserializeOnJoin=true"
                + "\nOnly set this for multi-server environments like Velocity or Bungeecord."
                + "\n- This is necessary when the disconnect snapshot is uploaded too late."
                + "\n- Player inventories will be frozen until the snapshot is downloaded to prevent item loss."
                + "\n- A value around 5000-10000 should suffice for most heavy modpacks. Adjust as required."
                + "\nAdditional notes:"
                + "\n- If players are consistently losing inventories on join, increase this number."
                + "\n- If players are consistently having to wait on join, decrease this number."

        );
        setDescription(DataSyncKeys.SERIALIZE_ON_DEATH, "\nWhether DataSync should serialize players to DB on death");
        setDescription(DataSyncKeys.SERIALIZE_ON_DISCONNECT, "\nWhether DataSync should serialize players to DB on disconnect");
        setDescription(DataSyncKeys.SNAPSHOT_MIN_COUNT, "\nMinimum number of snapshots to keep before deleting any");
        setDescription(DataSyncKeys.SNAPSHOT_OPTIMIZATION_STRATEGY,
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
        setDescription(DataSyncKeys.SNAPSHOT_UPLOAD_INTERVAL_MINUTES, "\nInterval for automatic serialization task. Set to 0 to disable, min 1, max 60. Recommended range 3-15");
    }
}
