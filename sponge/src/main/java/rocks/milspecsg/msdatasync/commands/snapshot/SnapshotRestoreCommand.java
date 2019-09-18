package rocks.milspecsg.msdatasync.commands.snapshot;

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
import rocks.milspecsg.msdatasync.api.data.UserSerializer;
import rocks.milspecsg.msdatasync.api.misc.DateFormatService;
import rocks.milspecsg.msdatasync.commands.SyncLockCommand;
import rocks.milspecsg.msdatasync.misc.CommandUtils;
import rocks.milspecsg.msdatasync.model.core.Snapshot;

import java.util.Optional;
import java.util.function.Consumer;

public class SnapshotRestoreCommand implements CommandExecutor {

    @Inject
    UserSerializer<Snapshot, User> userSerializer;

    @Inject
    CommandUtils commandUtils;

    @Inject
    DateFormatService dateFormatService;

    @Override
    public CommandResult execute(CommandSource source, CommandContext context) throws CommandException {

        SyncLockCommand.assertUnlocked(source);

        Optional<User> optionalUser = context.getOne(Text.of("user"));

        if (!optionalUser.isPresent()) {
            throw new CommandException(Text.of(MSDataSyncPluginInfo.pluginPrefix, "User is required"));
        }

        User targetUser = optionalUser.get();

        Consumer<Optional<Snapshot>> afterFound = optionalSnapshot -> {
            if (!optionalSnapshot.isPresent()) {
                source.sendMessage(Text.of(MSDataSyncPluginInfo.pluginPrefix, "Could not find snapshot for " + targetUser.getName()));
                return;
            }
            Snapshot snapshot = optionalSnapshot.get();
            source.sendMessage(Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.YELLOW, "Restoring snapshot " + dateFormatService.format(snapshot.getId().getDate()), " for user ", targetUser.getName()));
            userSerializer.deserialize(targetUser, MSDataSync.plugin, snapshot);
        };

        commandUtils.parseDateOrGetLatest(source, context, targetUser, afterFound);

        return CommandResult.success();
    }
}
