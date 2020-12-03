package org.anvilpowered.datasync.common.command.snapshot;

import com.google.inject.Inject;
import org.anvilpowered.anvil.api.registry.Registry;
import org.anvilpowered.anvil.api.util.PermissionService;
import org.anvilpowered.anvil.api.util.TextService;
import org.anvilpowered.anvil.api.util.UserService;
import org.anvilpowered.datasync.api.member.MemberManager;
import org.anvilpowered.datasync.api.registry.DataSyncKeys;

import java.util.Optional;

public class CommonSnapshotInfoCommand<
    TString,
    TUser,
    TPlayer,
    TCommandSource> {

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
        if (!permissionService.hasPermission(source, registry.getOrDefault(DataSyncKeys.SNAPSHOT_BASE_PERMISSION))) {
            textService.builder()
                .appendPrefix()
                .red().append("Insufficient Permissions!")
                .sendTo(source);
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
                .red().append("Invalid Player!")
                .sendTo(source);
            return;
        }

        memberManager.info(userService.getUUID((TUser) optionalPlayer.get()), snapshot)
            .thenAcceptAsync(msg -> textService.send(msg, source));
    }
}
