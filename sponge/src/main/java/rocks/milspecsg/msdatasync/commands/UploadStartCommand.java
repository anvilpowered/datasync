package rocks.milspecsg.msdatasync.commands;

import com.google.inject.Inject;
import org.spongepowered.api.Sponge;
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
import rocks.milspecsg.msdatasync.model.core.Snapshot;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class UploadStartCommand implements CommandExecutor {


    @Inject
    UserSerializer<Snapshot, User> userSerializer;

    @Override
    public CommandResult execute(CommandSource source, CommandContext context) throws CommandException {

        SyncLockCommand.assertUnlocked(source);

        // serialize everyone on the server
        Collection<Player> players = Sponge.getServer().getOnlinePlayers();
        ConcurrentLinkedQueue<Player> successful = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<Player> unsuccessful = new ConcurrentLinkedQueue<>();

        if (players.isEmpty()) {
            throw new CommandException(Text.of(MSDataSyncPluginInfo.pluginPrefix, "There are no players currently online"));
        }

        for (Player player : players) {
            userSerializer.serialize(player).thenAcceptAsync(optionalSnapshot -> {
                if (optionalSnapshot.isPresent()) {
                    successful.add(player);
                } else {
                    unsuccessful.add(player);
                }
                if (successful.size() + unsuccessful.size() >= players.size()) {
                    if (successful.size() > 0) {
                        String s = successful.stream().map(User::getName).collect(Collectors.joining(", "));
                        source.sendMessage(
                            Text.of(TextColors.YELLOW, "The following players were successfully serialized: \n", TextColors.GREEN, s)
                        );
                    }
                    if (unsuccessful.size() > 0) {
                        String u = unsuccessful.stream().map(User::getName).collect(Collectors.joining(", "));
                        source.sendMessage(
                            Text.of(TextColors.RED, "The following players were unsuccessfully serialized: \n", u)
                        );
                    }
                }
            });
        }

        return CommandResult.success();
    }
}
