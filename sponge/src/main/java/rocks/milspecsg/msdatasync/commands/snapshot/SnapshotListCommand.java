package rocks.milspecsg.msdatasync.commands.snapshot;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.HoverAction;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import rocks.milspecsg.msdatasync.api.member.MemberRepository;
import rocks.milspecsg.msdatasync.commands.SyncCommandManager;
import rocks.milspecsg.msdatasync.misc.CommandUtils;
import rocks.milspecsg.msdatasync.misc.DateFormatService;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.model.core.Snapshot;

import java.util.*;

public class SnapshotListCommand implements CommandExecutor {

    @Inject
    MemberRepository<Member, Snapshot, User> memberRepository;

    @Inject
    CommandUtils commandUtils;

    @Inject
    DateFormatService dateFormatService;

    @Override
    public CommandResult execute(CommandSource source, CommandContext context) throws CommandException {

        Optional<Player> optionalPlayer = context.getOne(Text.of("player"));
        if (!optionalPlayer.isPresent()) {
            throw new CommandException(Text.of("Player is required"));
        }

        Player player = optionalPlayer.get();

        memberRepository.getSnapshotDates(player.getUniqueId()).thenAcceptAsync(dates -> {
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
                                    dateFormatService.formatDiff(new Date(System.currentTimeMillis() - date.getTime())),
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
                    .contents(lines).linesPerPage(10);
            paginationBuilder.build().sendTo(source);
        });

        return CommandResult.success();
    }
}
