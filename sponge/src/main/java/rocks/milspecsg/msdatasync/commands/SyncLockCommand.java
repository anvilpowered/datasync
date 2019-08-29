package rocks.milspecsg.msdatasync.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import rocks.milspecsg.msdatasync.PluginInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SyncLockCommand implements CommandExecutor {


    private static List<UUID> unlockedPlayers = new ArrayList<>();

    public static void assertUnlocked(CommandSource source) throws CommandException {
        if (source instanceof Player && !unlockedPlayers.contains(((Player) source).getUniqueId())) {
            throw new CommandException(Text.of("You must first unlock this command with /sync lock off"));
        }
    }

    @Override
    public CommandResult execute(CommandSource source, CommandContext context) {
        if (source instanceof Player) {
            Player player = (Player) source;
            Optional<String> optionalValue = context.getOne(Text.of("value"));

            int index = unlockedPlayers.indexOf((player.getUniqueId()));

            if (!optionalValue.isPresent()) {
                source.sendMessage(Text.of(PluginInfo.PluginPrefix, "Currently ", TextColors.YELLOW, index >= 0 ? "unlocked" : "locked"));
                return CommandResult.success();
            }

            String value = optionalValue.get();

            switch (value) {
                case "on":
                    if (index >= 0) {
                        unlockedPlayers.remove(index);
                        source.sendMessage(Text.of(PluginInfo.PluginPrefix, "Lock", TextColors.YELLOW, "enabled"));
                    } else {
                        source.sendMessage(Text.of(PluginInfo.PluginPrefix, "Lock already", TextColors.YELLOW, "enabled"));
                    }
                    break;
                case "off":
                    if (index < 0) {
                        unlockedPlayers.add(player.getUniqueId());
                        source.sendMessage(Text.of(PluginInfo.PluginPrefix, "Lock ", TextColors.YELLOW, "disabled", TextColors.RED, " (be careful)"));
                    } else {
                        source.sendMessage(Text.of(PluginInfo.PluginPrefix, "Lock already ", TextColors.YELLOW, "disabled"));
                    }
                    break;
                default:
                    source.sendMessage(Text.of(PluginInfo.PluginPrefix, TextColors.RED, "Unrecognized option: \"", value, "\". Lock is ", TextColors.YELLOW, index >= 0 ? "disabled" : "enabled"));
                    break;
            }


        } else {
            // console is always unlocked
            source.sendMessage(Text.of(PluginInfo.PluginPrefix, "Console is always unlocked"));
        }

        return CommandResult.success();
    }
}
