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
import rocks.milspecsg.msdatasync.PluginInfo;
import rocks.milspecsg.msdatasync.api.data.PlayerSerializer;
import rocks.milspecsg.msdatasync.model.core.Member;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UploadStartCommand implements CommandExecutor {

    @Inject
    PlayerSerializer<Member, Player> playerSerializer;

    @Override
    public CommandResult execute(CommandSource source, CommandContext context) throws CommandException {

        SyncLockCommand.assertUnlocked(source);

        Optional<Player> optionalPlayer = context.getOne(Text.of("player"));

        if (optionalPlayer.isPresent()) {
            // serialize only one player
            System.out.println("Serializing " + optionalPlayer.get().getName());
            playerSerializer.serialize(optionalPlayer.get()).thenAcceptAsync(success -> {
                if (success) {
                    source.sendMessage(Text.of(PluginInfo.PluginPrefix, "Successfully serialized ", optionalPlayer.get().getName()));
                } else {
                    source.sendMessage(Text.of(PluginInfo.PluginPrefix, TextColors.RED, "An error occurred while serializing ", optionalPlayer.get().getName()));
                }
            });
        } else {
            // serialize everyone on the server
            Collection<Player> players = Sponge.getServer().getOnlinePlayers();
            ConcurrentLinkedQueue<Player> successful = new ConcurrentLinkedQueue<>();
            ConcurrentLinkedQueue<Player> unsuccessful = new ConcurrentLinkedQueue<>();

            for (Player player : players) {
                playerSerializer.serialize(player).thenAcceptAsync(success -> {
                    if (success) {
                        successful.add(player);
                    } else {
                        unsuccessful.add(player);
                    }
                    if (successful.size() + unsuccessful.size() >= players.size()) {
                        String s = successful.stream().map(User::getName).collect(Collectors.joining(","));
                        source.sendMessage(Text.of(PluginInfo.PluginPrefix, TextColors.YELLOW,
                            "Successfully serialized the following players: \n", TextColors.GREEN, s));
                        if (unsuccessful.size() > 0) {
                            String u = unsuccessful.stream().map(User::getName).collect(Collectors.joining(","));
                            source.sendMessage(Text.of(TextColors.RED, "The following players were unsuccessfully serialized: \n", u));
                        }
                    }
                });
            }
        }

        return CommandResult.success();
    }
}
