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

package rocks.milspecsg.msdatasync.misc;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import rocks.milspecsg.msdatasync.api.member.MemberManager;
import rocks.milspecsg.msdatasync.api.misc.DateFormatService;
import rocks.milspecsg.msdatasync.model.core.member.Member;
import rocks.milspecsg.msdatasync.model.core.snapshot.Snapshot;
import rocks.milspecsg.msrepository.PluginInfo;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class CommandUtils {

    @Inject
    private MemberManager<Member<?>, Snapshot<?>, User, Text> memberManager;

    @Inject
    private DateFormatService dateFormatService;

    @Inject
    private PluginInfo<Text> pluginInfo;

    public void parseDateOrGetLatest(CommandSource source, CommandContext context, User targetUser, Consumer<Optional<Snapshot<?>>> afterFound) throws CommandException {
        Optional<String> optionalDate = context.getOne(Text.of("date"));
        if (optionalDate.isPresent()) {
            Date date;
            try {
                date = dateFormatService.parse(optionalDate.get());
            } catch (ParseException e) {
                throw new CommandException(Text.of(pluginInfo.getPrefix(), TextColors.RED, "Invalid date format"), e);
            }
            memberManager.getPrimaryComponent().getSnapshot(targetUser.getUniqueId(), date).thenAcceptAsync(afterFound);
        } else {
            source.sendMessage(Text.of(pluginInfo.getPrefix(), TextColors.YELLOW, "No date present... finding latest snapshot for " + targetUser.getName()));
            memberManager.getPrimaryComponent().getLatestSnapshot(targetUser.getUniqueId()).thenAcceptAsync(afterFound);
        }
    }

    public Text snapshotActions(User targetUser, String date) {
        return Text.builder()
            .append(
                Text.builder()
                    .append(Text.of(TextColors.GREEN, "[ Restore ]"))
                    .onHover(TextActions.showText(Text.of(TextColors.GREEN, "Click to restore")))
                    .onClick(TextActions.suggestCommand("/sync snapshot restore " + targetUser.getName() + " " + date))
                    .build())
            .append(Text.of(" "))
            .append(
                Text.builder()
                    .append(Text.of(TextColors.GOLD, "[ Edit ]"))
                    .onHover(TextActions.showText(Text.of(TextColors.GOLD, "Click to edit")))
                    .onClick(TextActions.suggestCommand("/sync snapshot edit " + targetUser.getName() + " " + date))
                    .build())
            .append(Text.of(" "))
            .append(
                Text.builder()
                    .append(Text.of(TextColors.RED, "[ Delete ]"))
                    .onHover(TextActions.showText(Text.of(TextColors.RED, "Click to delete")))
                    .onClick(TextActions.suggestCommand("/sync snapshot delete " + targetUser.getName() + " " + date))
                    .build())
            .build();
    }

    public void createHelpPage(final CommandSource source, final Map<List<String>, CommandSpec> commands, final String commandName) {
        List<Text> helpList = Lists.newArrayList();

        for (List<String> aliases : commands.keySet()) {
            CommandSpec commandSpec = commands.get(aliases);

            if (!commandSpec.getShortDescription(source).isPresent()) continue;

            String subCommand = aliases.toString().replace("[", "").replace("]", "");
            String cmd = commandName == null || commandName.length() > 0 ? commandName + " " : "";
            Text commandHelp = Text.builder()
                .append(Text.builder()
                    .append(Text.of(TextColors.GREEN, "/sync ", cmd, subCommand))
                    .build())
                .append(Text.builder()
                    .append(Text.of(TextColors.GOLD, " - " + commandSpec.getShortDescription(source).get().toPlain() + "\n"))
                    .build())
                .append(Text.builder()
                    .append(Text.of(TextColors.GRAY, "Usage: /sync ", cmd, subCommand, " ", commandSpec.getUsage(source).toPlain()))
                    .build())
                .build();

            helpList.add(commandHelp);
        }

        helpList.sort(Text::compareTo);

        Optional<PaginationService> paginationService = Sponge.getServiceManager().provide(PaginationService.class);
        if (!paginationService.isPresent()) return;
        PaginationList.Builder paginationBuilder = paginationService.get().builder().title(Text.of(TextColors.GOLD, "MSDataSync " + (commandName != null && commandName.length() > 1 ? commandName.substring(0, 1).toUpperCase() + commandName.substring(1) : "") + " - MilspecSG")).padding(Text.of(TextColors.DARK_GREEN, "-")).contents(helpList).linesPerPage(20);
        paginationBuilder.build().sendTo(source);
    }

    public void createInfoPage(final CommandSource source) {
        try {
            source.sendMessage(
                Text.of(
                    pluginInfo.getPrefix(), TextColors.YELLOW, "Running version ",
                    TextColors.GOLD, pluginInfo.getVersion(), "\n",
                    TextColors.YELLOW, "This plugin was written by Cableguy20 from MilSpecSG\n",
                    Text.builder()
                        .append(Text.of(TextColors.AQUA, "[ Plugin Page ]"))
                        .onHover(TextActions.showText(Text.of(TextColors.AQUA, "https://github.com/MilSpecSG/MSDataSync")))
                        .onClick(TextActions.openUrl(new URL("https://github.com/MilSpecSG/MSDataSync")))
                        .build(),
                    " ",
                    Text.builder()
                        .append(Text.of(TextColors.AQUA, "[ MilSpecSG ]"))
                        .onHover(TextActions.showText(Text.of(TextColors.AQUA, "https://www.milspecsg.rocks/")))
                        .onClick(TextActions.openUrl(new URL("https://www.milspecsg.rocks/")))
                        .build()
                )
            );
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

}
