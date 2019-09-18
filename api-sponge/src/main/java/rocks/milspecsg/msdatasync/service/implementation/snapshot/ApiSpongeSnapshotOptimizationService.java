package rocks.milspecsg.msdatasync.service.implementation.snapshot;

import org.bson.types.ObjectId;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.model.core.Snapshot;
import rocks.milspecsg.msdatasync.service.snapshot.ApiSnapshotOptimizationService;
import rocks.milspecsg.msrepository.SpongePluginInfo;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Singleton
public class ApiSpongeSnapshotOptimizationService extends ApiSnapshotOptimizationService<Member, Snapshot, Player, User, CommandSource> {

    @Inject
    protected SpongePluginInfo spongePluginInfo;

    @Inject
    public ApiSpongeSnapshotOptimizationService(ConfigurationService configurationService) {
        super(configurationService);
    }

    private void sendMessageToSourceAndConsole(final CommandSource source, Text message) {
        source.sendMessage(message);
        if (!(source instanceof ConsoleSource)) {
            Sponge.getServer().getConsole().sendMessage(message);
        }
    }

    @Override
    protected Optional<Player> getPlayer(User user) {
        return user.getPlayer();
    }

    @Override
    protected String getName(User user) {
        return user.getName();
    }

    @Override
    protected void sendError(final CommandSource source, final String message) {
        sendMessageToSourceAndConsole(source, Text.of(spongePluginInfo.getPrefix(), TextColors.RED, message));
    }

    @Override
    protected void submitTask(Runnable runnable, Object plugin) {
        Task.builder().execute(runnable).submit(plugin);
    }

    private CompletableFuture<Boolean> optimize(final User user, final CommandSource source, final String name, final Object plugin) {
        if (lockedPlayers.contains(user.getUniqueId())) return CompletableFuture.completedFuture(false);
        return memberRepository.getSnapshotIds(user.getUniqueId()).thenApplyAsync(snapshotIds -> optimizeFull(snapshotIds, user.getUniqueId(), source, name, plugin).join());
    }

    @Override
    public boolean optimize(final Collection<? extends User> users, final CommandSource source, final String name, final Object plugin) {
        if (isOptimizationTaskRunning()) {
            return false;
        }
        CompletableFuture.supplyAsync(() -> {
            for (User user : users) {
                optimize(user, source, name, plugin).join();
                incrementCompleted();

                if (requestCancelOptimizationTask) {
                    break;
                }
            }
            return null;
        }).thenAcceptAsync(v -> {
            printOptimizationFinished(source, snapshotsDeleted, snapshotsUploaded, membersCompleted);
            resetCounters();
            stopOptimizationTask();
        });
        return true;
    }

    @Override
    public boolean optimize(final CommandSource source, final Object plugin) {
        if (optimizationTaskRunning) {
            return false;
        }
        optimizationTaskRunning = true;
        CompletableFuture.supplyAsync(() -> {
            List<ObjectId> memberIds = memberRepository.getAllIds().join();
            setTotal(memberIds.size());
            for (ObjectId memberId : memberIds) {
                Optional<Member> optionalMember = memberRepository.getOne(memberId).join();
                if (!optionalMember.isPresent()) continue;
                Member member = optionalMember.get();
                if (!lockedPlayers.contains(member.userUUID)) {
                    optimizeFull(member.snapshotIds, member.userUUID, source, "Manual", plugin).join();
                }
                incrementCompleted();

                if (requestCancelOptimizationTask) {
                    break;
                }
            }
            return null;
        }).thenAcceptAsync(v -> {
            printOptimizationFinished(source, snapshotsDeleted, snapshotsUploaded, membersCompleted);
            resetCounters();
            stopOptimizationTask();
        });
        return true;
    }

    private void printOptimizationFinished(final CommandSource source, final int snapshotsDeleted, final int snapshotsUploaded, final int membersCompleted) {
        String snapshotsDeletedString = snapshotsDeleted == 1 ? " snapshot from " : " snapshots from ";
        String snapshotsUploadedString = snapshotsUploaded == 1 ? " snapshot" : " snapshots";
        String memberString = membersCompleted == 1 ? " user!" : " users!";
        source.sendMessage(Text.of(spongePluginInfo.getPrefix(), TextColors.YELLOW, "Optimization complete! Uploaded ", snapshotsUploaded, snapshotsUploadedString, " and removed ", snapshotsDeleted, snapshotsDeletedString, membersCompleted, memberString));
    }
}
