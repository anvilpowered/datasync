package org.anvilpowered.datasync.common.command;

import com.google.inject.Inject;
import org.anvilpowered.anvil.api.registry.Registry;
import org.anvilpowered.anvil.api.util.PermissionService;
import org.anvilpowered.anvil.api.util.TextService;
import org.anvilpowered.anvil.api.util.UserService;
import org.anvilpowered.datasync.api.misc.LockService;
import org.anvilpowered.datasync.api.registry.DataSyncKeys;
import org.anvilpowered.datasync.api.serializer.user.UserSerializerManager;

public class CommonSyncUploadCommand<
    TString,
    TUser,
    TPlayer,
    TCommandSource> {

    @Inject
    private LockService lockService;

    @Inject
    private PermissionService permissionService;

    @Inject
    private Registry registry;

    @Inject
    private TextService<TString, TCommandSource> textService;

    @Inject
    private UserService<TUser, TPlayer> userService;

    @Inject
    private UserSerializerManager<TUser, TString> userSerializerManager;

    public void execute(TCommandSource source) {
        if (!permissionService.hasPermission(source, registry.getOrDefault(DataSyncKeys.SNAPSHOT_CREATE_PERMISSION))) {
            textService.builder()
                .appendPrefix()
                .red().append("Insufficient Permissions!")
                .sendTo(source);
            return;
        }
        if (lockService.assertUnlocked(source)) {
            userSerializerManager.serialize((TUser) userService.getOnlinePlayers())
                .thenAcceptAsync(msg -> textService.send(msg, source));
        }
    }
}
