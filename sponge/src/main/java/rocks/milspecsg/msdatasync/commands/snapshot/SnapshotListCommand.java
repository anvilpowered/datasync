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
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import rocks.milspecsg.msdatasync.api.member.MemberManager;
import rocks.milspecsg.msdatasync.api.member.repository.MemberRepository;
import rocks.milspecsg.msdatasync.api.misc.DateFormatService;
import rocks.milspecsg.msdatasync.misc.CommandUtils;
import rocks.milspecsg.msdatasync.model.core.member.Member;
import rocks.milspecsg.msdatasync.model.core.member.MongoMember;
import rocks.milspecsg.msdatasync.model.core.snapshot.MongoSnapshot;
import rocks.milspecsg.msdatasync.model.core.snapshot.Snapshot;

import java.util.*;

public class SnapshotListCommand implements CommandExecutor {

    @Inject
    private MemberManager<Member<?>, Snapshot<?>, User, Text> memberRepository;

    @Inject
    private CommandUtils commandUtils;

    @Inject
    DateFormatService dateFormatService;

    @Override
    public CommandResult execute(CommandSource source, CommandContext context) throws CommandException {

        Optional<User> optionalUser = context.getOne(Text.of("user"));
        if (!optionalUser.isPresent()) {
            throw new CommandException(Text.of("User is required"));
        }

        User player = optionalUser.get();

        memberRepository.getPrimaryComponent().getSnapshotDates(player.getUniqueId()).thenAcceptAsync(dates -> {
            List<Text> lines = new ArrayList<>();
            dates.forEach(date -> {
                String d = dateFormatService.format(date);
                lines.add(Text.builder()
                    .append(
                        Text.builder()
                            .append(Text.of(TextColors.YELLOW, d))
                            .onHover(TextActions.showText(
                                Text.of(
                                    TextColors.YELLOW,
                                    "Click for more info\n",
                                    TextColors.AQUA,
                                    dateFormatService.formatDiff(new Date(new Date().getTime() - date.getTime())),
                                    " ago"
                                )))
                            .onClick(TextActions.suggestCommand("/sync snapshot info " + player.getName() + " " + d))
                            .build())
                    .append(Text.of(" "))
                    .append(commandUtils.snapshotActions(player, d))
                    .build()
                );
            });

            Optional<PaginationService> paginationService = Sponge.getServiceManager().provide(PaginationService.class);
            if (!paginationService.isPresent()) return;
            PaginationList.Builder paginationBuilder =
                paginationService.get()
                    .builder()
                    .title(Text.of(TextColors.GOLD, "Snapshots - ", player.getName()))
                    .padding(Text.of(TextColors.DARK_GREEN, "-"))
                    .contents(lines).linesPerPage(20);
            paginationBuilder.build().sendTo(source);
        });

        return CommandResult.success();
    }
}
