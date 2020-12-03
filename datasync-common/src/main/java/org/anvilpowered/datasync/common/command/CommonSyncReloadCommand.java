package org.anvilpowered.datasync.common.command;

import com.google.inject.Inject;
import org.anvilpowered.anvil.api.registry.Registry;
import org.anvilpowered.anvil.api.util.PermissionService;
import org.anvilpowered.anvil.api.util.TextService;
import org.anvilpowered.datasync.api.registry.DataSyncKeys;

public class CommonSyncReloadCommand<TString, TCommandSource> {

    @Inject
    private PermissionService permissionService;

    @Inject
    private Registry registry;

    @Inject
    private TextService<TString, TCommandSource> textService;

    public void execute(TCommandSource source) {
        if (!permissionService.hasPermission(source, registry.getOrDefault(DataSyncKeys.RELOAD_COMMAND_PERMISSION))) {
            textService.builder()
                .appendPrefix()
                .red().append("Insufficient Permissions!")
                .sendTo(source);
            return;
        }

        registry.load();
        textService.builder()
            .appendPrefix()
            .green().append("Successfully reloaded!")
            .sendTo(source);
    }
}
