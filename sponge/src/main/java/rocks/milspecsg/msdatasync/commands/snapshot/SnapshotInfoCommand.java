package rocks.milspecsg.msdatasync.commands.snapshot;

import com.google.inject.Inject;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import rocks.milspecsg.msdatasync.MSDataSyncPluginInfo;
import rocks.milspecsg.msdatasync.api.misc.DateFormatService;
import rocks.milspecsg.msdatasync.misc.CommandUtils;
import rocks.milspecsg.msdatasync.model.core.Snapshot;

import java.util.Date;
import java.util.Optional;
import java.util.function.Consumer;

public class SnapshotInfoCommand implements CommandExecutor {

    @Inject
    CommandUtils commandUtils;

    @Inject
    DateFormatService dateFormatService;

    @Override
    public CommandResult execute(CommandSource source, CommandContext context) throws CommandException {


        Optional<User> optionalUser = context.getOne(Text.of("user"));

        if (!optionalUser.isPresent()) {
            throw new CommandException(Text.of(MSDataSyncPluginInfo.pluginPrefix, "User is required"));
        }

        User targetPlayer = optionalUser.get();

        Consumer<Optional<Snapshot>> afterFound = optionalSnapshot -> {
            if (!optionalSnapshot.isPresent()) {
                source.sendMessage(Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.RED, "Could not find snapshot for user " + targetPlayer.getName()));
                return;
            }
            Snapshot snapshot = optionalSnapshot.get();

            Date created = snapshot.getId().getDate();
            Date updated = snapshot.getUpdatedUtc();

            source.sendMessage(
                Text.builder()
                    .append(Text.of(TextColors.DARK_GREEN, "======= ", TextColors.GOLD, "Snapshot - ", targetPlayer.getName(), TextColors.DARK_GREEN, " ======="))
                    .append(Text.of(TextColors.GRAY, "\n\nId: ", TextColors.YELLOW, snapshot.getId()))
                    .append(Text.of(TextColors.GRAY, "\n\nCreated: ", TextColors.YELLOW,
                        Text.builder()
                            .append(Text.of(TextColors.YELLOW, dateFormatService.format(created)))
                            .onHover(TextActions.showText(
                                Text.of(
                                    TextColors.AQUA,
                                    dateFormatService.formatDiff(new Date(System.currentTimeMillis() - created.getTime())),
                                    " ago"
                                )))
                            .build()
                        ))
                    .append(Text.of(TextColors.GRAY, "\n\nUpdated: ", TextColors.YELLOW,
                        Text.builder()
                            .append(Text.of(TextColors.YELLOW, dateFormatService.format(updated)))
                            .onHover(TextActions.showText(
                                Text.of(
                                    TextColors.AQUA,
                                    dateFormatService.formatDiff(new Date(System.currentTimeMillis() - updated.getTime())),
                                    " ago"
                                )))
                            .build()
                        ))
                    .append(Text.of(TextColors.GRAY, "\n\nName: ", TextColors.YELLOW, snapshot.name))
                    .append(Text.of(TextColors.GRAY, "\n\nServer: ", TextColors.YELLOW, snapshot.server, "\n\n"))
                    .append(commandUtils.snapshotActions(targetPlayer, dateFormatService.format(created)))
                    .append(Text.of(" "))
                    .append(
                        Text.builder()
                            .append(Text.of(TextColors.AQUA, "[ Back ]"))
                            .onHover(TextActions.showText(Text.of(TextColors.AQUA, "Click to go back to list")))
                            .onClick(TextActions.suggestCommand("/sync snapshot list " + targetPlayer.getName()))
                            .build()
                    )
                    .append(Text.of(TextColors.DARK_GREEN, "\n\n======= ", TextColors.GOLD, "Snapshot - ", targetPlayer.getName(), TextColors.DARK_GREEN, " ======="))
                    .build()
            );


        };

        commandUtils.parseDateOrGetLatest(source, context, targetPlayer, afterFound);

        return CommandResult.success();
    }
}
