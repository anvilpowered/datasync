package org.anvilpowered.datasync.common.command.snapshot;

import com.google.inject.Inject;
import org.anvilpowered.anvil.api.registry.Registry;
import org.anvilpowered.anvil.api.util.PermissionService;
import org.anvilpowered.anvil.api.util.TextService;
import org.anvilpowered.anvil.api.util.UserService;
import org.anvilpowered.datasync.api.member.MemberManager;
import org.anvilpowered.datasync.api.misc.LockService;
import org.anvilpowered.datasync.api.registry.DataSyncKeys;

import java.util.Optional;

public class CommonSnapshotDeleteCommand<
    TString,
    TUser,
    TPlayer,
    TCommandSource> {

    @Inject
    private LockService lockService;

    @Inject
    private MemberManager<TString> memberManager;

    @Inject
    private PermissionService permissionService;

    @Inject
    private Registry registry;

    @Inject
    private TextService<TString, TCommandSource> textService;

    @Inject
    private UserService<TUser, TPlayer> userService;

    public void execute(TCommandSource source, String[] context) {
        if (!permissionService.hasPermission(source, registry.getOrDefault(DataSyncKeys.SNAPSHOT_DELETE_PERMISSION))) {
            textService.builder()
                .appendPrefix()
                .red().append("Insufficient Permissions!")
                .sendTo(source);
            return;
        }

        if (!lockService.assertUnlocked(source)) {
            return;
        }
        if (context.length != 2) {
            textService.builder()
                .appendPrefix()
                .yellow().append("User and Snapshot are required!")
                .sendTo(source);
            return;
        }

        Optional<TPlayer> optionalPlayer = userService.getPlayer(context[0]);
        if (!optionalPlayer.isPresent()) {
            textService.builder()
                .appendPrefix()
                .red().append("Invalid player!")
                .sendTo(source);
            return;
        }
        memberManager.deleteSnapshot(userService.getUUID((TUser) optionalPlayer.get()), context[1])
            .thenAcceptAsync(msg -> textService.send(msg, source));
    }
}
