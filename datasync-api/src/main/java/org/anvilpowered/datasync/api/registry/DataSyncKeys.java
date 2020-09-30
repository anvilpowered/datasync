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

package org.anvilpowered.datasync.api.registry;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import org.anvilpowered.anvil.api.registry.Key;
import org.anvilpowered.anvil.api.registry.Keys;
import org.anvilpowered.anvil.api.registry.TypeTokens;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public final class DataSyncKeys {

    public static final TypeToken<List<String>> LIST_STRING = new TypeToken<List<String>>() {
    };

    private DataSyncKeys() {
        throw new AssertionError("**boss music** No instance for you!");
    }

    public static final Key<List<String>> SERIALIZE_ENABLED_SERIALIZERS =
        Key.builder(LIST_STRING)
            .name("SERIALIZE_ENABLED_SERIALIZERS")
            .fallback(ImmutableList.of(
                "datasync:experience",
                "datasync:gameMode",
                "datasync:health",
                "datasync:hunger",
                "datasync:inventory"
            ))
            .build();

    public static final Key<Boolean> DESERIALIZE_ON_JOIN =
        Key.builder(TypeTokens.BOOLEAN)
            .name("DESERIALIZE_ON_JOIN")
            .fallback(false)
            .build();
    public static final Key<Integer> DESERIALIZE_ON_JOIN_DELAY_MILLIS =
        Key.builder(TypeTokens.INTEGER)
            .name("DESERIALIZE_ON_JOIN_WAIT_MILLIS")
            .fallback(0)
            .build();
    public static final Key<Boolean> SERIALIZE_ON_DEATH =
        Key.builder(TypeTokens.BOOLEAN)
            .name("SERIALIZE_ON_DEATH")
            .fallback(true)
            .build();
    public static final Key<Boolean> SERIALIZE_ON_DISCONNECT =
        Key.builder(TypeTokens.BOOLEAN)
            .name("SERIALIZE_ON_DISCONNECT")
            .fallback(true)
            .build();
    public static final Key<Integer> SNAPSHOT_MIN_COUNT =
        Key.builder(TypeTokens.INTEGER)
            .name("SNAPSHOT_MIN_COUNT")
            .fallback(5)
            .build();
    public static final Key<List<String>> SNAPSHOT_OPTIMIZATION_STRATEGY =
        Key.builder(LIST_STRING)
            .name("SNAPSHOT_OPTIMIZATION_STRATEGY")
            .fallback(ImmutableList.of("60:24", "1440:7"))
            .build();
    public static final Key<Integer> SNAPSHOT_UPLOAD_INTERVAL_MINUTES =
        Key.builder(TypeTokens.INTEGER)
            .name("SNAPSHOT_UPLOAD_INTERVAL")
            .fallback(5)
            .build();
    public static final Key<String> LOCK_COMMAND_PERMISSION =
        Key.builder(TypeTokens.STRING)
            .name("LOCK_COMMAND_PERMISSION")
            .fallback("datasync.lock")
            .build();
    public static final Key<String> RELOAD_COMMAND_PERMISSION =
        Key.builder(TypeTokens.STRING)
            .name("RELOAD_COMMAND_PERMISSION")
            .fallback("datasync.reload")
            .build();
    public static final Key<String> TEST_COMMAND_PERMISSION =
        Key.builder(TypeTokens.STRING)
            .name("TEST_COMMAND_PERMISSION")
            .fallback("datasync.test")
            .build();
    public static final Key<String> SNAPSHOT_BASE_PERMISSION =
        Key.builder(TypeTokens.STRING)
            .name("SNAPSHOT_BASE_PERMISSION")
            .fallback("datasync.snapshot.base")
            .build();
    public static final Key<String> SNAPSHOT_CREATE_PERMISSION =
        Key.builder(TypeTokens.STRING)
            .name("SNAPSHOT_CREATE_PERMISSION")
            .fallback("datasync.snapshot.create")
            .build();
    public static final Key<String> SNAPSHOT_DELETE_PERMISSION =
        Key.builder(TypeTokens.STRING)
            .name("SNAPSHOT_DELETE_PERMISSION")
            .fallback("datasync.snapshot.delete")
            .build();
    public static final Key<String> SNAPSHOT_RESTORE_PERMISSION =
        Key.builder(TypeTokens.STRING)
            .name("SNAPSHOT_RESTORE_PERMISSION")
            .fallback("datasync.snapshot.restore")
            .build();
    public static final Key<String> SNAPSHOT_VIEW_EDIT_PERMISSION =
        Key.builder(TypeTokens.STRING)
            .name("SNAPSHOT_VIEW_EDIT_PERMISSION")
            .fallback("datasync.snapshot.view.edit")
            .build();
    public static final Key<String> SNAPSHOT_VIEW_BASE_PERMISSION =
        Key.builder(TypeTokens.STRING)
            .name("SNAPSHOT_VIEW_BASE_PERMISSION")
            .fallback("datasync.snapshot.view.base")
            .build();
    public static final Key<String> MANUAL_OPTIMIZATION_ALL_PERMISSION =
        Key.builder(TypeTokens.STRING)
            .name("MANUAL_OPTIMIZATION_ALL_PERMISSION")
            .fallback("datasync.optimize.all")
            .build();
    public static final Key<String> MANUAL_OPTIMIZATION_BASE_PERMISSION =
        Key.builder(TypeTokens.STRING)
            .name("MANUAL_OPTIMIZATION_BASE_PERMISSION")
            .fallback("datasync.optimize.base")
            .build();

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
