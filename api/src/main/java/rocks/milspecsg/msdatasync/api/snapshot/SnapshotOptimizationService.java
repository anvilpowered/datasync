package rocks.milspecsg.msdatasync.api.snapshot;

import java.util.Collection;
import java.util.UUID;

public interface SnapshotOptimizationService<U, CS> {

    int getTotalMembers();

    int getMembersCompleted();

    int getSnapshotsDeleted();

    int getSnapshotsUploaded();

    boolean isOptimizationTaskRunning();

    boolean stopOptimizationTask();

    void addLockedPlayer(final UUID uuid);

    void removeLockedPlayer(final UUID uuid);

    boolean optimize(final Collection<? extends U> users, final CS source, final String name, final Object plugin);

    boolean optimize(final CS source, final Object plugin);

}
