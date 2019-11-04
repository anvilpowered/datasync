/*
 *     MSDataSync - MilSpecSG
 *     Copyright (C) 2019 Cableguy20
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
import rocks.milspecsg.msdatasync.model.core.snapshot.Snapshot;

import java.util.Date;
import java.util.Optional;
import java.util.function.Consumer;

public class SnapshotInfoCommand implements CommandExecutor {

    @Inject
    private CommandUtils commandUtils;

    @Inject
    private DateFormatService dateFormatService;

    @Override
    public CommandResult execute(CommandSource source, CommandContext context) throws CommandException {


        Optional<User> optionalUser = context.getOne(Text.of("user"));

        if (!optionalUser.isPresent()) {
            throw new CommandException(Text.of(MSDataSyncPluginInfo.pluginPrefix, "User is required"));
        }

        User targetPlayer = optionalUser.get();

        Consumer<Optional<Snapshot<?>>> afterFound = optionalSnapshot -> {
            if (!optionalSnapshot.isPresent()) {
                source.sendMessage(Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.RED, "Could not find snapshot for user " + targetPlayer.getName()));
                return;
            }
            Snapshot<?> snapshot = optionalSnapshot.get();

            Date created = snapshot.getCreatedUtcDate();
            Date updated = snapshot.getUpdatedUtcDate();

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
                    .append(Text.of(TextColors.GRAY, "\n\nName: ", TextColors.YELLOW, snapshot.getName()))
                    .append(Text.of(TextColors.GRAY, "\n\nServer: ", TextColors.YELLOW, snapshot.getServer(), "\n\n"))
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
