package rocks.milspecsg.msdatasync.misc;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import rocks.milspecsg.msdatasync.MSDataSyncPluginInfo;
import rocks.milspecsg.msdatasync.api.member.MemberRepository;
import rocks.milspecsg.msdatasync.commands.SyncCommandManager;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.model.core.Snapshot;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class CommandUtils {

    @Inject
    private MemberRepository<Member, Snapshot, User> memberRepository;

    @Inject
    private DateFormatService dateFormatService;

    public void parseDateOrGetLatest(CommandSource source, CommandContext context, User targetUser, Consumer<Optional<Snapshot>> afterFound) throws CommandException {
        Optional<String> optionalDate = context.getOne(Text.of("date"));
        if (!optionalDate.isPresent()) {
            source.sendMessage(Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.YELLOW, "No date present... finding latest snapshot for " + targetUser.getName()));
            memberRepository.getLatestSnapshot(targetUser.getUniqueId()).thenAcceptAsync(afterFound);
        } else {
            Date date;
            try {
                date = dateFormatService.parse(optionalDate.get());
            } catch (ParseException e) {
                throw new CommandException(Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.RED, "Invalid date format"));
            }
            memberRepository.getSnapshot(targetUser.getUniqueId(), date).thenAcceptAsync(afterFound);
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

}
