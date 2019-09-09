package rocks.milspecsg.msdatasync;

public abstract class PluginPermissions {
    public static final String LOCK_COMMAND = "msdatasync.lock";
    public static final String RELOAD_COMMAND = "msdatasync.reload";
    public static final String SNAPSHOT_BASE = "msdatasync.snapshot.base";
    public static final String SNAPSHOT_CREATE = "msdatasync.snapshot.create";
    public static final String SNAPSHOT_DELETE = "msdatasync.snapshot.delete";
    public static final String SNAPSHOT_RESTORE = "msdatasync.snapshot.restore";
    public static final String SNAPSHOT_VIEW_EDIT = "msdatasync.snapshot.view.edit";
    public static final String SNAPSHOT_VIEW_BASE = "msdatasync.snapshot.view.base";
    public static final String MANUAL_OPTIMIZATION_ALL = "msdatasync.optimize.all";
    public static final String MANUAL_OPTIMIZATION_BASE = "msdatasync.optimize.base";
}
