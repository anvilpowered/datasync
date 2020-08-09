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

package org.anvilpowered.datasync.common.snapshotoptimization;

import com.google.inject.Inject;
import org.anvilpowered.anvil.api.plugin.PluginInfo;
import org.anvilpowered.anvil.api.registry.Registry;
import org.anvilpowered.anvil.api.util.TextService;
import org.anvilpowered.anvil.base.datastore.BaseManager;
import org.anvilpowered.datasync.api.snapshotoptimization.SnapshotOptimizationManager;
import org.anvilpowered.datasync.api.snapshotoptimization.SnapshotOptimizationService;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class CommonSnapshotOptimizationManager<
    TUser,
    TString,
    TCommandSource>
    extends BaseManager<SnapshotOptimizationService<?, TUser, TCommandSource, ?>>
    implements SnapshotOptimizationManager<TUser, TString, TCommandSource> {

    @Inject
    TextService<TString, TCommandSource> textService;

    @Inject
    PluginInfo<TString> pluginInfo;

    private static final NumberFormat formatter = new DecimalFormat("#0.00");

    @Inject
    public CommonSnapshotOptimizationManager(Registry registry) {
        super(registry);
    }

    @Override
    public TString info() {
        int uploaded = getPrimaryComponent().getSnapshotsUploaded();
        int deleted = getPrimaryComponent().getSnapshotsDeleted();
        int completed = getPrimaryComponent().getMembersCompleted();
        int total = getPrimaryComponent().getTotalMembers();

        if (getPrimaryComponent().isOptimizationTaskRunning()) {
            return textService.builder()
                .append(pluginInfo.getPrefix())
                .yellow().append("Optimization task:\n")
                .gray().append("Snapshots uploaded: ")
                .yellow().append(uploaded, "\n")
                .gray().append("Snapshots deleted: ")
                .yellow().append(deleted, "\n")
                .gray().append("Members processed: ")
                .yellow().append(completed, "/", total, "\n")
                .gray().append("Progress: ")
                .yellow().append(formatter.format(completed * 100d / total), "%")
                .build();
        }
        return textService.builder()
            .append(pluginInfo.getPrefix())
            .yellow().append("There is currently no optimization task running")
            .build();
    }

    @Override
    public TString stop() {
        TextService.Builder<TString, TCommandSource> builder = textService.builder()
            .append(pluginInfo.getPrefix()).yellow();
        if (getPrimaryComponent().stopOptimizationTask()) {
            builder.append("Successfully stopped optimization task");
        } else {
            builder.append("There is currently no optimization task running");
        }
        return builder.build();
    }
}
