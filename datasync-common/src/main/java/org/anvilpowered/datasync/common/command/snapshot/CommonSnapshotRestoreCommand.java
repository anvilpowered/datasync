package org.anvilpowered.datasync.common.command.snapshot;

import com.google.inject.Inject;
import org.anvilpowered.anvil.api.registry.Registry;
import org.anvilpowered.anvil.api.util.PermissionService;
import org.anvilpowered.anvil.api.util.TextService;
import org.anvilpowered.anvil.api.util.UserService;
import org.anvilpowered.datasync.api.misc.LockService;
import org.anvilpowered.datasync.api.registry.DataSyncKeys;
import org.anvilpowered.datasync.api.serializer.user.UserSerializerManager;

import java.util.Optional;

public class CommonSnapshotRestoreCommand<
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

    public void execute(TCommandSource source, String[] context) {
        if (!permissionService.hasPermission(source, registry.getOrDefault(DataSyncKeys.SNAPSHOT_RESTORE_PERMISSION))) {
            textService.builder()
                .appendPrefix()
                .red().append("Insufficient Permissions!")
                .sendTo(source);
            return;
        }

        if (!lockService.assertUnlocked(source)){
            return;
        }

        String player;
        String snapshot;

        if (context.length == 0) {
            textService.builder()
                .appendPrefix()
                .red().append("User is required!")
                .sendTo(source);
            return;
        } else if (context.length == 1) {
            player = context[0];
            snapshot = null;
        } else {
            player = context[0];
            snapshot = context[1];
        }
        Optional<TPlayer> optionalPlayer = userService.getPlayer(player);
        if (!optionalPlayer.isPresent()) {
            textService.builder()
                .appendPrefix()
                .red().append("Invalid player!")
                .sendTo(source);
            return;
        }
        userSerializerManager.restore(userService.getUUID((TUser) optionalPlayer.get()), snapshot)
            .thenAcceptAsync(msg -> textService.send(msg, source));
    }
}
