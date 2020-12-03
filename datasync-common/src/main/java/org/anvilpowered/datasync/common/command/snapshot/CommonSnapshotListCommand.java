package org.anvilpowered.datasync.common.command.snapshot;

import com.google.inject.Inject;
import org.anvilpowered.anvil.api.util.PermissionService;
import org.anvilpowered.anvil.api.util.TextService;
import org.anvilpowered.anvil.api.util.UserService;
import org.anvilpowered.datasync.api.member.MemberManager;
import org.anvilpowered.datasync.api.registry.DataSyncKeys;

import java.util.Optional;

public class CommonSnapshotListCommand<
    TString,
    TUser,
    TPlayer,
    TCommandSource> {

    @Inject
    private MemberManager<TString> memberManager;

    @Inject
    private PermissionService permissionService;

    @Inject
    private TextService<TString, TCommandSource> textService;

    @Inject
    private UserService<TUser, TPlayer> userService;


    public void execute(TCommandSource source, String[] context) {
        if (!permissionService.hasPermission(source, DataSyncKeys.SNAPSHOT_BASE_PERMISSION.getFallbackValue())) {
            textService.builder()
                .appendPrefix()
                .yellow().append("Insufficient Permissions!")
                .sendTo(source);
            return;
        }

        if (context.length != 1) {
            textService.builder()
                .appendPrefix()
                .red().append("User is required!")
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
        try {
            memberManager.list(userService.getUUID((TUser) optionalPlayer.get()))
                .thenAcceptAsync(list -> {
                    textService.paginationBuilder()
                        .title(textService.builder().gold().append("Snapshots - " + context[0]).build())
                        .padding(textService.builder().dark_green().append("-"))
                        .contents(list).linesPerPage(10)
                        .build().sendTo(source);
                });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
