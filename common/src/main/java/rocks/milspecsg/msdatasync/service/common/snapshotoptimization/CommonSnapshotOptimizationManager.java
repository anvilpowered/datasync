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

package rocks.milspecsg.msdatasync.service.common.snapshotoptimization;

import com.google.inject.Inject;
import rocks.milspecsg.msdatasync.api.snapshotoptimization.SnapshotOptimizationManager;
import rocks.milspecsg.msdatasync.api.snapshotoptimization.component.SnapshotOptimizationService;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;
import rocks.milspecsg.msrepository.service.common.manager.CommonManager;

public class CommonSnapshotOptimizationManager<
    TUser, TCommandSource>
    extends CommonManager<SnapshotOptimizationService<?, TUser, TCommandSource, ?, ?>>
    implements SnapshotOptimizationManager<TUser, TCommandSource> {

    @Inject
    public CommonSnapshotOptimizationManager(ConfigurationService configurationService) {
        super(configurationService);
    }
}
