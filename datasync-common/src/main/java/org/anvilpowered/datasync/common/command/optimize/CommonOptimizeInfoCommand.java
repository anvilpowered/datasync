package org.anvilpowered.datasync.common.command.optimize;

import com.google.inject.Inject;
import org.anvilpowered.anvil.api.registry.Registry;
import org.anvilpowered.anvil.api.util.PermissionService;
import org.anvilpowered.anvil.api.util.TextService;
import org.anvilpowered.datasync.api.registry.DataSyncKeys;
import org.anvilpowered.datasync.api.snapshotoptimization.SnapshotOptimizationManager;

public class CommonOptimizeInfoCommand<
    TString,
    TPlayer,
    TCommandSource> {

    @Inject
    private PermissionService permissionService;

    @Inject
    private Registry registry;

    @Inject
    private SnapshotOptimizationManager<TPlayer, TString, TCommandSource> snapshotOptimizationManager;

    @Inject
    private TextService<TString, TCommandSource> textService;

    public void execute(TCommandSource source) {
        if (!permissionService.hasPermission(source, registry.getOrDefault(DataSyncKeys.MANUAL_OPTIMIZATION_BASE_PERMISSION))) {
            textService.builder()
                .appendPrefix()
                .red().append("Insufficient Permissions!")
                .sendTo(source);
            return;
        }
        textService.send(snapshotOptimizationManager.info(), source);
    }
}
