package rocks.milspecsg.msdatasync.commands.snapshot;

import com.google.inject.Inject;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import rocks.milspecsg.msdatasync.MSDataSyncPluginInfo;
import rocks.milspecsg.msdatasync.api.member.MemberRepository;
import rocks.milspecsg.msdatasync.commands.SyncLockCommand;
import rocks.milspecsg.msdatasync.misc.DateFormatService;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.model.core.Snapshot;

import java.text.ParseException;
import java.util.Date;
import java.util.Optional;
import java.util.function.Consumer;

public class SnapshotDeleteCommand implements CommandExecutor {

    @Inject
    MemberRepository<Member, Snapshot, User> memberRepository;

    @Inject
    DateFormatService dateFormatService;

    @Override
    public CommandResult execute(CommandSource source, CommandContext context) throws CommandException {

        SyncLockCommand.assertUnlocked(source);

        Optional<User> optionalUser = context.getOne(Text.of("user"));

        if (!optionalUser.isPresent()) {
            throw new CommandException(Text.of(MSDataSyncPluginInfo.pluginPrefix, "User is required"));
        }

        Optional<String> optionalDate = context.getOne(Text.of("date"));

        if (!optionalDate.isPresent()) {
            throw new CommandException(Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.RED, "Date is required"));
        }

        User targetUser = optionalUser.get();

        Consumer<Optional<Snapshot>> afterFound = optionalSnapshot -> {
            if (!optionalSnapshot.isPresent()) {
                source.sendMessage(Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.RED, "Could not find snapshot for " + targetUser.getName()));
                return;
            }

            memberRepository.deleteSnapshot(targetUser.getUniqueId(), optionalSnapshot.get().getId()).thenAcceptAsync(success -> {
                if (success) {
                    source.sendMessage(
                        Text.of(
                            MSDataSyncPluginInfo.pluginPrefix, TextColors.YELLOW,
                            "Successfully deleted snapshot ", TextColors.GOLD,
                            dateFormatService.format(optionalSnapshot.get().getId().getDate()),
                            TextColors.YELLOW, " for user ", targetUser.getName()
                        )
                    );
                } else {
                    source.sendMessage(
                        Text.of(
                            MSDataSyncPluginInfo.pluginPrefix, TextColors.RED,
                            "An error occurred while deleting snapshot ",
                            dateFormatService.format(optionalSnapshot.get().getId().getDate()),
                            " for user ", TextColors.YELLOW, targetUser.getName()
                        )
                    );
                }
            });
        };

        Date date;
        try {
            date = dateFormatService.parse(optionalDate.get());
        } catch (ParseException e) {
            throw new CommandException(Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.RED, "Invalid date format"));
        }
        memberRepository.getSnapshot(targetUser.getUniqueId(), date).thenAcceptAsync(afterFound);

        return CommandResult.success();
    }
}
