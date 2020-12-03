package org.anvilpowered.datasync.common.command;

import com.google.inject.Inject;
import org.anvilpowered.anvil.api.util.PermissionService;
import org.anvilpowered.anvil.api.util.TextService;
import org.anvilpowered.anvil.api.util.UserService;
import org.anvilpowered.datasync.api.misc.LockService;
import org.anvilpowered.datasync.api.registry.DataSyncKeys;

import java.util.Optional;

public class CommonSyncLockCommand<
    TString,
    TUser,
    TPlayer,
    TCommandSource> {

    @Inject
    private LockService lockService;

    @Inject
    private PermissionService permissionService;

    @Inject
    private TextService<TString, TCommandSource> textService;

    @Inject
    private UserService<TUser, TPlayer> userService;

    public void execute(TCommandSource source, String[] context, Class<?> playerClass) {
        if (!playerClass.isAssignableFrom(source.getClass())) {
            textService.builder()
                .appendPrefix()
                .yellow().append("Console is always unlocked!")
                .sendTo(source);
            return;
        }

        if (!permissionService.hasPermission(source, DataSyncKeys.LOCK_COMMAND_PERMISSION.getFallbackValue())) {
            return;
        }
        Optional<TPlayer> player = userService.getPlayer((TUser) source);

        if (!player.isPresent()) {
            return;
        }
        int index = lockService.getUnlockedPlayers().indexOf(userService.getUUID((TUser) source));
        String status = index >= 0 ? "unlocked" : "locked";
        if (context.length < 1) {
            textService.builder()
                .appendPrefix()
                .yellow().append("Currently " + status)
                .sendTo(source);
            return;
        }

        switch (context[0]) {
            case "on":
                if (index >= 0) {
                    lockService.getUnlockedPlayers().remove(index);
                    textService.builder()
                        .appendPrefix()
                        .yellow().append("Lock enabled")
                        .sendTo(source);
                } else {
                    textService.builder()
                        .appendPrefix()
                        .yellow().append("Lock already enabled")
                        .sendTo(source);
                }
                break;
            case "off":
                if (index < 0) {
                    lockService.add(userService.getUUID((TUser) source));
                    textService.builder()
                        .appendPrefix()
                        .yellow().append("Lock disabled")
                        .red().append(" (be careful)")
                        .sendTo(source);
                } else {
                    textService.builder()
                        .appendPrefix()
                        .yellow().append("Lock already disabled")
                        .sendTo(source);
                }
                break;
            default:
                textService.builder()
                    .appendPrefix()
                    .red().append("Unrecognized option: \"" + context[0] + "\". Lock is")
                    .yellow().append(status)
                    .sendTo(source);
        }
    }
}
