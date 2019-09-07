package rocks.milspecsg.msdatasync.commands.snapshot;

import com.google.inject.Inject;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import rocks.milspecsg.msdatasync.MSDataSyncPluginInfo;
import rocks.milspecsg.msdatasync.api.data.UserSerializer;
import rocks.milspecsg.msdatasync.commands.SyncLockCommand;
import rocks.milspecsg.msdatasync.misc.DateFormatService;
import rocks.milspecsg.msdatasync.model.core.Snapshot;

import java.util.Optional;

public class SnapshotCreateCommand implements CommandExecutor {

    @Inject
    UserSerializer<Snapshot, User> userSerializer;

    @Inject
    DateFormatService dateFormatService;

    @Override
    public CommandResult execute(CommandSource source, CommandContext context) throws CommandException {

        SyncLockCommand.assertUnlocked(source);

        Optional<User> optionalUser = context.getOne(Text.of("user"));

        if (optionalUser.isPresent()) {
            userSerializer.serialize(optionalUser.get()).thenAcceptAsync(optionalSnapshot -> {
                if (optionalSnapshot.isPresent()) {
                    source.sendMessage(
                        Text.of(
                            MSDataSyncPluginInfo.pluginPrefix, TextColors.YELLOW,
                            "Successfully serialized ", optionalUser.get().getName(),
                            " and uploaded snapshot ", TextColors.GOLD,
                            dateFormatService.format(optionalSnapshot.get().getId().getDate())
                        )
                    );
                } else {
                    source.sendMessage(Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.RED, "An error occurred while serializing ", optionalUser.get().getName()));
                }
            });
        } else {
            throw new CommandException(Text.of(MSDataSyncPluginInfo.pluginPrefix, "User is required"));
        }

        return CommandResult.success();
    }
}
