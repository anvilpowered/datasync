/*
 *     MSDataSync - MilSpecSG
 *     Copyright (C) 2019 Cableguy20
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

package rocks.milspecsg.msdatasync.common.tasks;

import com.google.inject.Inject;
import rocks.milspecsg.msdatasync.api.snapshotoptimization.SnapshotOptimizationManager;
import rocks.milspecsg.msdatasync.api.tasks.SerializationTaskService;
import rocks.milspecsg.msdatasync.common.data.key.MSDataSyncKeys;
import rocks.milspecsg.msrepository.api.data.registry.Registry;

public abstract class CommonSerializationTaskService<
    TUser,
    TString,
    TCommandSource>
    implements SerializationTaskService {

    @Inject
    protected SnapshotOptimizationManager<TUser, TString, TCommandSource> snapshotOptimizationManager;

    protected Registry registry;

    protected int baseInterval;

    protected CommonSerializationTaskService(Registry registry) {
        this.registry = registry;
        this.registry.addRegistryLoadedListener(this::registryLoaded);
    }

    private void registryLoaded(Object plugin) {
        baseInterval = registry.getOrDefault(MSDataSyncKeys.SNAPSHOT_UPLOAD_INTERVAL_MINUTES);
        stopSerializationTask();
        startSerializationTask();
    }
}