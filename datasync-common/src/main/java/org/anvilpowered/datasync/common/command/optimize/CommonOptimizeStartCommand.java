package org.anvilpowered.datasync.common.command.optimize;

import com.google.inject.Inject;
import org.anvilpowered.anvil.api.registry.Registry;
import org.anvilpowered.anvil.api.util.PermissionService;
import org.anvilpowered.anvil.api.util.TextService;
import org.anvilpowered.anvil.api.util.UserService;
import org.anvilpowered.datasync.api.registry.DataSyncKeys;
import org.anvilpowered.datasync.api.snapshotoptimization.SnapshotOptimizationManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommonOptimizeStartCommand<
    TString,
    TUser,
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

    @Inject
    private UserService<TUser, TPlayer> userService;

    public void execute(TCommandSource source, String[] context) {
        if (!permissionService.hasPermission(source, registry.getOrDefault(DataSyncKeys.MANUAL_OPTIMIZATION_BASE_PERMISSION))) {
            textService.builder()
                .appendPrefix()
                .red().append("Insufficient Permissions!")
                .sendTo(source);
            return;
        }

        if (context.length == 0) {
            textService.builder()
                .appendPrefix()
                .yellow().append("Mode is required")
                .sendTo(source);
            return;
        }
        String mode = context[0];

        if (context.length < 2) {
            textService.builder()
                .appendPrefix()
                .yellow().append("No users were selected by your query")
                .sendTo(source);
        }
        context[0] = "";
        List<String> playerNames = Arrays.asList(context.clone());

        if (mode.equals("all")) {
            if (!permissionService.hasPermission(source,
                registry.getOrDefault(DataSyncKeys.MANUAL_OPTIMIZATION_ALL_PERMISSION))
            ) {
                textService.builder()
                    .appendPrefix()
                    .red().append("You do not have permission to start optimization task: all")
                    .sendTo(source);
            } else if (snapshotOptimizationManager.getPrimaryComponent().optimize(source)) {
                textService.builder()
                    .appendPrefix()
                    .yellow().append("Successfully started optimization task: all")
                    .sendTo(source);
            } else {
                textService.builder()
                    .appendPrefix()
                    .yellow().append("Optimizer already running! Use /sync optimize info");
            }
        } else {
            if (playerNames.isEmpty()) {
                textService.builder()
                    .appendPrefix()
                    .yellow().append("No users were selected by your query")
                    .sendTo(source);
                return;
            } else {
                List<TPlayer> players = new ArrayList<>();
                for (String name : playerNames) {
                    if (!userService.get(name).isPresent()) {
                        continue;
                    }
                    players.add((TPlayer) userService.get(name).get());
                }
                if (snapshotOptimizationManager.getPrimaryComponent().optimize(players, source, "Manual")) {
                    textService.builder()
                        .appendPrefix()
                        .yellow().append("Successfully started optimization task: user")
                        .sendTo(source);
                } else {
                    textService.builder()
                        .appendPrefix()
                        .yellow().append("Optimizer already running! Use /sync optimize info")
                        .sendTo(source);
                }
            }
        }
    }
}
