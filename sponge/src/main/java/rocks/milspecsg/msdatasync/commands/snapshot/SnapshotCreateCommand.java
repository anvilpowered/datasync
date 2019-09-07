package rocks.milspecsg.msdatasync.commands.snapshot;

import com.google.inject.Inject;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import rocks.milspecsg.msdatasync.MSDataSyncPluginInfo;
import rocks.milspecsg.msdatasync.api.data.PlayerSerializer;
import rocks.milspecsg.msdatasync.commands.SyncLockCommand;
import rocks.milspecsg.msdatasync.misc.DateFormatService;
import rocks.milspecsg.msdatasync.model.core.Snapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Optional;

public class SnapshotCreateCommand implements CommandExecutor {

    @Inject
    PlayerSerializer<Snapshot, Player> playerSerializer;

    @Inject
    DateFormatService dateFormatService;

    @Override
    public CommandResult execute(CommandSource source, CommandContext context) throws CommandException {

        SyncLockCommand.assertUnlocked(source);

        Optional<Player> optionalPlayer = context.getOne(Text.of("player"));

        if (optionalPlayer.isPresent()) {
            playerSerializer.serialize(optionalPlayer.get()).thenAcceptAsync(optionalSnapshot -> {
                if (optionalSnapshot.isPresent()) {
                    source.sendMessage(
                        Text.of(
                            MSDataSyncPluginInfo.pluginPrefix, TextColors.YELLOW,
                            "Successfully serialized ", optionalPlayer.get().getName(),
                            " and uploaded snapshot ", TextColors.GOLD,
                            dateFormatService.format(optionalSnapshot.get().getId().getDate())
                        )
                    );
                } else {
                    source.sendMessage(Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.RED, "An error occurred while serializing ", optionalPlayer.get().getName()));
                }
            });
        } else {
            throw new CommandException(Text.of(MSDataSyncPluginInfo.pluginPrefix, "Player is required"));
        }

        return CommandResult.success();
    }
}
