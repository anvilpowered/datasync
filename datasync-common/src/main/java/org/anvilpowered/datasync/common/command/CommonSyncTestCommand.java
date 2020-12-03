package org.anvilpowered.datasync.common.command;

import com.google.inject.Inject;
import org.anvilpowered.anvil.api.registry.Registry;
import org.anvilpowered.anvil.api.util.PermissionService;
import org.anvilpowered.anvil.api.util.TextService;
import org.anvilpowered.anvil.api.util.UserService;
import org.anvilpowered.datasync.api.registry.DataSyncKeys;
import org.anvilpowered.datasync.api.serializer.user.UserSerializerManager;

import java.util.Optional;

public class CommonSyncTestCommand<
    TString,
    TUser,
    TPlayer,
    TCommandSource> {

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

    public void execute(TCommandSource source, Class<?> playerClass) {
        if (!playerClass.isAssignableFrom(source.getClass())) {
            textService.builder()
                .appendPrefix()
                .yellow().append("Player only command!")
                .sendTo(source);
            return;
        }

        if (!permissionService.hasPermission(source, registry.getOrDefault(DataSyncKeys.TEST_COMMAND_PERMISSION))) {
            textService.builder()
                .appendPrefix()
                .red().append("Insufficient Permissions!")
                .sendTo(source);
            return;
        }

        Optional<TPlayer> optionalPlayer = userService.getPlayer((TUser) source);

        if (!optionalPlayer.isPresent()) {
            return;
        }
        userSerializerManager.serialize((TUser) optionalPlayer.get())
            .exceptionally(e -> {
                e.printStackTrace();
                textService.send(textService.of("An error has occurred!"), source);
                return null;
            })
            .thenAcceptAsync(text -> {
                if (text == null) {
                    return;
                }
                textService.send(text, source);
                textService.builder()
                    .appendPrefix()
                    .green().append("Deserializing in 5 seconds")
                    .sendTo(source);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                userSerializerManager.restore(userService.getUUID((TUser) optionalPlayer.get()), null)
                    .thenAcceptAsync(msg -> textService.send(msg, source));
            });
    }
}
