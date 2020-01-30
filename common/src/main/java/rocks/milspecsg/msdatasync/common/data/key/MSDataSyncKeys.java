package rocks.milspecsg.msdatasync.common.data.key;

import rocks.milspecsg.msrepository.api.data.key.Key;
import rocks.milspecsg.msrepository.api.data.key.Keys;

import java.util.Arrays;
import java.util.List;

public final class MSDataSyncKeys {

    private MSDataSyncKeys() {
        throw new AssertionError("**boss music** No instance for you!");
    }

    public static final Key<List<String>> SERIALIZE_ENABLED_SERIALIZERS = new Key<List<String>>(
        "SERIALIZE_ENABLED_SERIALIZERS",
        Arrays.asList("msdatasync:experience", "msdatasync:gameMode", "msdatasync:health", "msdatasync:hunger", "msdatasync:inventory")
    ) {
    };

    public static final Key<Boolean> DESERIALIZE_ON_JOIN = new Key<Boolean>("DESERIALIZE_ON_JOIN", false) {
    };
    public static final Key<Boolean> SERIALIZE_ON_DEATH = new Key<Boolean>("SERIALIZE_ON_DEATH", true) {
    };
    public static final Key<Boolean> SERIALIZE_ON_DISCONNECT = new Key<Boolean>("SERIALIZE_ON_DISCONNECT", true) {
    };
    public static final Key<Boolean> SERIALIZE_WAIT_FOR_SNAPSHOT_ON_JOIN = new Key<Boolean>("SERIALIZE_WAIT_FOR_SNAPSHOT_ON_JOIN", false) {
    };
    public static final Key<Integer> SNAPSHOT_MIN_COUNT = new Key<Integer>("SNAPSHOT_MIN_COUNT", 5) {
    };
    public static final Key<List<String>> SNAPSHOT_OPTIMIZATION_STRATEGY = new Key<List<String>>("SNAPSHOT_OPTIMIZATION_STRATEGY", Arrays.asList("60:24", "1440:7")) {
    };
    public static final Key<Integer> SNAPSHOT_UPLOAD_INTERVAL_MINUTES = new Key<Integer>("SNAPSHOT_UPLOAD_INTERVAL", 5) {
    };
    public static final Key<String> SERVER_NAME = new Key<String>("SERVER_NAME", "server") {
    };
    public static final Key<String> LOCK_COMMAND_PERMISSION = new Key<String>("LOCK_COMMAND_PERMISSION", "msdatasync.lock") {
    };
    public static final Key<String> RELOAD_COMMAND_PERMISSION = new Key<String>("RELOAD_COMMAND_PERMISSION", "msdatasync.reload") {
    };
    public static final Key<String> SNAPSHOT_BASE_PERMISSION = new Key<String>("SNAPSHOT_BASE_PERMISSION", "msdatasync.snapshot.base") {
    };
    public static final Key<String> SNAPSHOT_CREATE_PERMISSION = new Key<String>("SNAPSHOT_CREATE_PERMISSION", "msdatasync.snapshot.create") {
    };
    public static final Key<String> SNAPSHOT_DELETE_PERMISSION = new Key<String>("SNAPSHOT_DELETE_PERMISSION", "msdatasync.snapshot.delete") {
    };
    public static final Key<String> SNAPSHOT_RESTORE_PERMISSION = new Key<String>("SNAPSHOT_RESTORE_PERMISSION", "msdatasync.snapshot.restore") {
    };
    public static final Key<String> SNAPSHOT_VIEW_EDIT_PERMISSION = new Key<String>("SNAPSHOT_VIEW_EDIT_PERMISSION", "msdatasync.snapshot.view.edit") {
    };
    public static final Key<String> SNAPSHOT_VIEW_BASE_PERMISSION = new Key<String>("SNAPSHOT_VIEW_BASE_PERMISSION", "msdatasync.snapshot.view.base") {
    };
    public static final Key<String> MANUAL_OPTIMIZATION_ALL_PERMISSION = new Key<String>("MANUAL_OPTIMIZATION_ALL_PERMISSION", "msdatasync.optimize.all") {
    };
    public static final Key<String> MANUAL_OPTIMIZATION_BASE_PERMISSION = new Key<String>("MANUAL_OPTIMIZATION_BASE_PERMISSION", "msdatasync.optimize.base") {
    };

    static {
        Keys.registerKey(SERIALIZE_ENABLED_SERIALIZERS);
        Keys.registerKey(DESERIALIZE_ON_JOIN);
        Keys.registerKey(SERIALIZE_ON_DEATH);
        Keys.registerKey(SERIALIZE_ON_DISCONNECT);
        Keys.registerKey(SERIALIZE_WAIT_FOR_SNAPSHOT_ON_JOIN);
        Keys.registerKey(SNAPSHOT_MIN_COUNT);
        Keys.registerKey(SNAPSHOT_OPTIMIZATION_STRATEGY);
        Keys.registerKey(SNAPSHOT_UPLOAD_INTERVAL_MINUTES);
        Keys.registerKey(SERVER_NAME);
        Keys.registerKey(LOCK_COMMAND_PERMISSION);
        Keys.registerKey(RELOAD_COMMAND_PERMISSION);
        Keys.registerKey(SNAPSHOT_BASE_PERMISSION);
        Keys.registerKey(SNAPSHOT_CREATE_PERMISSION);
        Keys.registerKey(SNAPSHOT_DELETE_PERMISSION);
        Keys.registerKey(SNAPSHOT_RESTORE_PERMISSION);
        Keys.registerKey(SNAPSHOT_VIEW_EDIT_PERMISSION);
        Keys.registerKey(SNAPSHOT_VIEW_BASE_PERMISSION);
        Keys.registerKey(MANUAL_OPTIMIZATION_ALL_PERMISSION);
        Keys.registerKey(MANUAL_OPTIMIZATION_BASE_PERMISSION);
    }
}
