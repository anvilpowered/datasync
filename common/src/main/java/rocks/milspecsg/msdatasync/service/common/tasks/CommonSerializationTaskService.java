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

package rocks.milspecsg.msdatasync.service.common.tasks;

import com.google.inject.Inject;
import rocks.milspecsg.msdatasync.api.config.ConfigKeys;
import rocks.milspecsg.msdatasync.api.snapshotoptimization.SnapshotOptimizationManager;
import rocks.milspecsg.msdatasync.api.tasks.SerializationTaskService;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;

public abstract class CommonSerializationTaskService<
    TUser,
    TString,
    TCommandSource>
    implements SerializationTaskService {

    @Inject
    protected SnapshotOptimizationManager<TUser, TString, TCommandSource> snapshotOptimizationManager;

    protected ConfigurationService configurationService;

    protected int baseInterval = 5;

    protected CommonSerializationTaskService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
        this.configurationService.addConfigLoadedListener(this::loadConfig);
    }

    private void loadConfig(Object plugin) {
        baseInterval = configurationService.getConfigInteger(ConfigKeys.SNAPSHOT_UPLOAD_INTERVAL);
        stopSerializationTask();
        startSerializationTask();
    }

}