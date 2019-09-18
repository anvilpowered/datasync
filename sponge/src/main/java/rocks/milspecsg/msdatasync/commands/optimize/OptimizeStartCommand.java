package rocks.milspecsg.msdatasync.commands.optimize;

import com.google.inject.Inject;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import rocks.milspecsg.msdatasync.MSDataSync;
import rocks.milspecsg.msdatasync.MSDataSyncPluginInfo;
import rocks.milspecsg.msdatasync.PluginPermissions;
import rocks.milspecsg.msdatasync.api.snapshot.SnapshotOptimizationService;
import rocks.milspecsg.msdatasync.commands.SyncLockCommand;

import java.util.Collection;
import java.util.Optional;

public class OptimizeStartCommand implements CommandExecutor {

    @Inject
    SnapshotOptimizationService<User, CommandSource> snapshotOptimizationService;

    @Override
    public CommandResult execute(CommandSource source, CommandContext context) throws CommandException {

        SyncLockCommand.assertUnlocked(source);

        Optional<String> optionalMode = context.getOne(Text.of("mode"));
        Collection<User> users = context.getAll(Text.of("user"));

        if (!optionalMode.isPresent()) {
            throw new CommandException(Text.of(MSDataSyncPluginInfo.pluginPrefix, "Mode is required"));
        }

        if (optionalMode.get().equals("all")) {
            if (!source.hasPermission(PluginPermissions.MANUAL_OPTIMIZATION_ALL)) {
                throw new CommandException(Text.of(MSDataSyncPluginInfo.pluginPrefix, "You do not have permission to start optimization task: all"));
            } else if (snapshotOptimizationService.optimize(source, MSDataSync.plugin)) {
                source.sendMessage(Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.YELLOW, "Successfully started optimization task: all"));
            } else {
                throw new CommandException(Text.of(MSDataSyncPluginInfo.pluginPrefix, "Optimizer already running! Use /sync optimize info"));
            }
            snapshotOptimizationService.optimize(source, MSDataSync.plugin);
        } else {
            if (users.isEmpty()) {
                throw new CommandException(Text.of(MSDataSyncPluginInfo.pluginPrefix, "No users were selected by your query"));
            } else if (snapshotOptimizationService.optimize(users, source, "Manual", MSDataSync.plugin)) {
                source.sendMessage(Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.YELLOW, "Successfully started optimization task: user"));
            } else {
                throw new CommandException(Text.of(MSDataSyncPluginInfo.pluginPrefix, "Optimizer already running! Use /sync optimize info"));
            }
        }

        return CommandResult.success();
    }
}
