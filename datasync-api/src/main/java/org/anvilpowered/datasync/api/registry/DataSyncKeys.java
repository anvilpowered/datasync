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

package org.anvilpowered.datasync.api.registry;

import com.google.common.collect.ImmutableList;
import org.anvilpowered.anvil.api.registry.Key;
import org.anvilpowered.anvil.api.registry.Keys;

import java.util.Arrays;
import java.util.List;

public final class DataSyncKeys {

    private DataSyncKeys() {
        throw new AssertionError("**boss music** No instance for you!");
    }

    public static final Key<List<String>> SERIALIZE_ENABLED_SERIALIZERS = new Key<List<String>>(
        "SERIALIZE_ENABLED_SERIALIZERS",
        ImmutableList.of(
            "datasync:experience",
            "datasync:gameMode",
            "datasync:health",
            "datasync:hunger",
            "datasync:inventory"
        )
    ) {
    };

    public static final Key<Boolean> DESERIALIZE_ON_JOIN
        = new Key<Boolean>("DESERIALIZE_ON_JOIN", false) {
    };
    public static final Key<Integer> DESERIALIZE_ON_JOIN_DELAY_MILLIS
        = new Key<Integer>("DESERIALIZE_ON_JOIN_WAIT_MILLIS", 0) {
    };
    public static final Key<Boolean> SERIALIZE_ON_DEATH
        = new Key<Boolean>("SERIALIZE_ON_DEATH", true) {
    };
    public static final Key<Boolean> SERIALIZE_ON_DISCONNECT
        = new Key<Boolean>("SERIALIZE_ON_DISCONNECT", true) {
    };
    public static final Key<Integer> SNAPSHOT_MIN_COUNT
        = new Key<Integer>("SNAPSHOT_MIN_COUNT", 5) {
    };
    public static final Key<List<String>> SNAPSHOT_OPTIMIZATION_STRATEGY
        = new Key<List<String>>("SNAPSHOT_OPTIMIZATION_STRATEGY", Arrays.asList("60:24", "1440:7")) {
    };
    public static final Key<Integer> SNAPSHOT_UPLOAD_INTERVAL_MINUTES
        = new Key<Integer>("SNAPSHOT_UPLOAD_INTERVAL", 5) {
    };
    public static final Key<String> LOCK_COMMAND_PERMISSION
        = new Key<String>("LOCK_COMMAND_PERMISSION", "datasync.lock") {
    };
    public static final Key<String> RELOAD_COMMAND_PERMISSION
        = new Key<String>("RELOAD_COMMAND_PERMISSION", "datasync.reload") {
    };
    public static final Key<String> TEST_COMMAND_PERMISSION
        = new Key<String>("TEST_COMMAND_PERMISSION", "datasync.test") {
    };
    public static final Key<String> SNAPSHOT_BASE_PERMISSION
        = new Key<String>("SNAPSHOT_BASE_PERMISSION", "datasync.snapshot.base") {
    };
    public static final Key<String> SNAPSHOT_CREATE_PERMISSION
        = new Key<String>("SNAPSHOT_CREATE_PERMISSION", "datasync.snapshot.create") {
    };
    public static final Key<String> SNAPSHOT_DELETE_PERMISSION
        = new Key<String>("SNAPSHOT_DELETE_PERMISSION", "datasync.snapshot.delete") {
    };
    public static final Key<String> SNAPSHOT_RESTORE_PERMISSION
        = new Key<String>("SNAPSHOT_RESTORE_PERMISSION", "datasync.snapshot.restore") {
    };
    public static final Key<String> SNAPSHOT_VIEW_EDIT_PERMISSION
        = new Key<String>("SNAPSHOT_VIEW_EDIT_PERMISSION", "datasync.snapshot.view.edit") {
    };
    public static final Key<String> SNAPSHOT_VIEW_BASE_PERMISSION
        = new Key<String>("SNAPSHOT_VIEW_BASE_PERMISSION", "datasync.snapshot.view.base") {
    };
    public static final Key<String> MANUAL_OPTIMIZATION_ALL_PERMISSION
        = new Key<String>("MANUAL_OPTIMIZATION_ALL_PERMISSION", "datasync.optimize.all") {
    };
    public static final Key<String> MANUAL_OPTIMIZATION_BASE_PERMISSION
        = new Key<String>("MANUAL_OPTIMIZATION_BASE_PERMISSION", "datasync.optimize.base") {
    };

    static {
        Keys.startRegistration("datasync")
            .register(SERIALIZE_ENABLED_SERIALIZERS)
            .register(DESERIALIZE_ON_JOIN)
            .register(SERIALIZE_ON_DEATH)
            .register(SERIALIZE_ON_DISCONNECT)
            .register(DESERIALIZE_ON_JOIN_DELAY_MILLIS)
            .register(SNAPSHOT_MIN_COUNT)
            .register(SNAPSHOT_OPTIMIZATION_STRATEGY)
            .register(SNAPSHOT_UPLOAD_INTERVAL_MINUTES)
            .register(LOCK_COMMAND_PERMISSION)
            .register(RELOAD_COMMAND_PERMISSION)
            .register(TEST_COMMAND_PERMISSION)
            .register(SNAPSHOT_BASE_PERMISSION)
            .register(SNAPSHOT_CREATE_PERMISSION)
            .register(SNAPSHOT_DELETE_PERMISSION)
            .register(SNAPSHOT_RESTORE_PERMISSION)
            .register(SNAPSHOT_VIEW_EDIT_PERMISSION)
            .register(SNAPSHOT_VIEW_BASE_PERMISSION)
            .register(MANUAL_OPTIMIZATION_ALL_PERMISSION)
            .register(MANUAL_OPTIMIZATION_BASE_PERMISSION)
        ;
    }
}
