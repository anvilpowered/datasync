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

package rocks.milspecsg.msdatasync.common.snapshotoptimization;

import com.google.inject.Inject;
import rocks.milspecsg.msdatasync.api.snapshotoptimization.SnapshotOptimizationManager;
import rocks.milspecsg.msdatasync.api.snapshotoptimization.component.SnapshotOptimizationService;
import rocks.milspecsg.msrepository.api.util.PluginInfo;
import rocks.milspecsg.msrepository.api.data.config.ConfigurationService;
import rocks.milspecsg.msrepository.api.util.StringResult;
import rocks.milspecsg.msrepository.common.manager.CommonManager;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.concurrent.CompletableFuture;

public class CommonSnapshotOptimizationManager<
    TUser,
    TString,
    TCommandSource>
    extends CommonManager<SnapshotOptimizationService<?, TUser, TCommandSource, ?>>
    implements SnapshotOptimizationManager<TUser, TString, TCommandSource> {

    @Inject
    StringResult<TString, TCommandSource> stringResult;

    @Inject
    PluginInfo<TString> pluginInfo;

    private static NumberFormat formatter = new DecimalFormat("#0.00");

    @Inject
    public CommonSnapshotOptimizationManager(ConfigurationService configurationService) {
        super(configurationService);
    }

    @Override
    public CompletableFuture<TString> info() {
        return CompletableFuture.supplyAsync(() -> {
            int uploaded = getPrimaryComponent().getSnapshotsUploaded();
            int deleted = getPrimaryComponent().getSnapshotsDeleted();
            int completed = getPrimaryComponent().getMembersCompleted();
            int total = getPrimaryComponent().getTotalMembers();

            return getPrimaryComponent().isOptimizationTaskRunning()
                ? stringResult.builder()
                .append(pluginInfo.getPrefix())
                .yellow().append("Optimization task:\n")
                .gray().append("Snapshots uploaded: ").yellow().append(uploaded, "\n")
                .gray().append("Snapshots deleted: ").yellow().append(deleted, "\n")
                .gray().append("Members processed: ").yellow().append(completed, "/", total, "\n")
                .gray().append("Progress: ").yellow().append(formatter.format(completed * 100d / total), "%")
                .build()
                : stringResult.builder()
                .append(pluginInfo.getPrefix())
                .yellow().append("There is currently no optimization task running")
                .build();
        });
    }

    @Override
    public CompletableFuture<TString> stop() {
        return CompletableFuture.supplyAsync(() ->
            stringResult.builder()
                .append(pluginInfo.getPrefix())
                .yellow().append(getPrimaryComponent().stopOptimizationTask() ? "Successfully stopped optimization task" : "There is currently no optimization task running")
                .build()
        );
    }
}
