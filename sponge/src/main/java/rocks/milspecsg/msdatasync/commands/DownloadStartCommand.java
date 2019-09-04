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
import rocks.milspecsg.msdatasync.MSDataSync;
import rocks.milspecsg.msdatasync.MSDataSyncPluginInfo;
import rocks.milspecsg.msdatasync.api.data.PlayerSerializer;
import rocks.milspecsg.msdatasync.model.core.Snapshot;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class DownloadStartCommand implements CommandExecutor {

    @Inject
    PlayerSerializer<Snapshot, Player> playerSerializer;

    @Override
    public CommandResult execute(CommandSource source, CommandContext context) throws CommandException {

        SyncLockCommand.assertUnlocked(source);

        Optional<Player> optionalPlayer = context.getOne(Text.of("player"));

        if (optionalPlayer.isPresent()) {
            // serialize only one player
//            System.out.println("Deserializing " + optionalPlayer.get().getName());
            playerSerializer.deserialize(optionalPlayer.get(), MSDataSync.plugin).thenAcceptAsync(success -> {
                if (success) {
                    source.sendMessage(Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.YELLOW, "Successfully deserialized ", optionalPlayer.get().getName()));
                } else {
                    source.sendMessage(Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.RED, "An error occurred while deserializing ", optionalPlayer.get().getName()));
                }
            });
        } else {
            // serialize everyone on the server
            Collection<Player> players = Sponge.getServer().getOnlinePlayers();
            ConcurrentLinkedQueue<Player> successful = new ConcurrentLinkedQueue<>();
            ConcurrentLinkedQueue<Player> unsuccessful = new ConcurrentLinkedQueue<>();
            Sponge.getServer().getConsole().sendMessage(Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.YELLOW, "Starting download..."));

            for (Player player : players) {
                playerSerializer.deserialize(player, MSDataSync.plugin).thenAcceptAsync(success -> {
                    if (success) {
                        successful.add(player);
                    } else {
                        unsuccessful.add(player);
                    }
                    if (successful.size() + unsuccessful.size() >= players.size()) {
                        if (successful.size() > 0) {
                            String s = successful.stream().map(User::getName).collect(Collectors.joining(","));
                            source.sendMessage(
                                Text.of(TextColors.YELLOW, "The following players were successfully deserialized: \n", TextColors.GREEN, s)
                            );
                        }
                        if (unsuccessful.size() > 0) {
                            String u = unsuccessful.stream().map(User::getName).collect(Collectors.joining(","));
                            source.sendMessage(
                                Text.of(TextColors.RED, "The following players were unsuccessfully deserialized: \n", u)
                            );
                        }
                    }
                });
            }
        }

        return CommandResult.success();
    }
}
