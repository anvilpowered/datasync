package rocks.milspecsg.msdatasync.misc;

import com.google.inject.Inject;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import rocks.milspecsg.msdatasync.MSDataSyncPluginInfo;
import rocks.milspecsg.msdatasync.api.member.MemberRepository;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.model.core.Snapshot;

import java.text.ParseException;
import java.util.Date;
import java.util.Optional;
import java.util.function.Consumer;

public class CommandUtils {

    @Inject
    MemberRepository<Member, Snapshot, User> memberRepository;

    @Inject
    DateFormatService dateFormatService;

    public void parseDateOrGetLatest(CommandSource source, CommandContext context, Player targetPlayer, Consumer<Optional<Snapshot>> afterFound) throws CommandException {
        Optional<String> optionalDate = context.getOne(Text.of("date"));
        if (!optionalDate.isPresent()) {
            source.sendMessage(Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.YELLOW, "No date present... finding latest snapshot for " + targetPlayer.getName()));
            memberRepository.getLatestSnapshot(targetPlayer.getUniqueId()).thenAcceptAsync(afterFound);
        } else {
            Date date;
            try {
                date = dateFormatService.parse(optionalDate.get());
            } catch (ParseException e) {
                throw new CommandException(Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.RED, "Invalid date format"));
            }
            memberRepository.getSnapshot(targetPlayer.getUniqueId(), date).thenAcceptAsync(afterFound);
        }
    }

    public Text snapshotActions(Player targetPlayer, String date) {
        return Text.builder()
            .append(
                Text.builder()
                    .append(Text.of(TextColors.GREEN, "[ Restore ]"))
                    .onHover(TextActions.showText(Text.of(TextColors.GREEN, "Click to restore")))
                    .onClick(TextActions.suggestCommand("/sync snapshot restore " + targetPlayer.getName() + " " + date))
                    .build())
            .append(Text.of(" "))
            .append(
                Text.builder()
                    .append(Text.of(TextColors.GOLD, "[ Edit ]"))
                    .onHover(TextActions.showText(Text.of(TextColors.GOLD, "Click to edit")))
                    .onClick(TextActions.suggestCommand("/sync snapshot edit " + targetPlayer.getName() + " " + date))
                    .build())
            .append(Text.of(" "))
            .append(
                Text.builder()
                    .append(Text.of(TextColors.RED, "[ Delete ]"))
                    .onHover(TextActions.showText(Text.of(TextColors.RED, "Click to delete")))
                    .onClick(TextActions.suggestCommand("/sync snapshot delete " + targetPlayer.getName() + " " + date))
                    .build())
            .build();
    }

}
