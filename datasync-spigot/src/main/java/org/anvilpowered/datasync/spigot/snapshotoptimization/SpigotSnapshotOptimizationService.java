package org.anvilpowered.datasync.spigot.snapshotoptimization;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.md_5.bungee.api.chat.TextComponent;
import org.anvilpowered.anvil.api.plugin.PluginInfo;
import org.anvilpowered.anvil.api.registry.Registry;
import org.anvilpowered.anvil.api.util.TextService;
import org.anvilpowered.datasync.api.model.member.Member;
import org.anvilpowered.datasync.common.snapshotoptimization.CommonSnapshotOptimizationService;
import org.anvilpowered.datasync.spigot.DataSyncSpigot;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Singleton
public class SpigotSnapshotOptimizationService<
    TKey,
    TDataStore>
    extends CommonSnapshotOptimizationService<
    TKey, Player, Player, CommandSender, String, TDataStore> {

    @Inject
    private PluginInfo<TextComponent> pluginInfo;

    @Inject
    private TextService<TextComponent, CommandSender> textService;

    @Inject
    private DataSyncSpigot dataSyncSpigot;

    @Inject
    public SpigotSnapshotOptimizationService(Registry registry) {
        super(registry);
    }

    private void sendMessageToSourceAndConsole(final CommandSender sender, String message) {
        sender.sendMessage(message);
        if (!(sender instanceof ConsoleCommandSender)) {
            Bukkit.getConsoleSender().sendMessage(message);
        }
    }


    @Override
    protected void sendError(CommandSender sender, String message) {
        sendMessageToSourceAndConsole(sender, message);
    }

    @Override
    protected void submitTask(Runnable runnable) {
        Bukkit.getScheduler().runTask(dataSyncSpigot, runnable);
    }

    private CompletableFuture<Boolean> optimize(final Player player, final CommandSender sender, final String name) {
        if (lockedPlayers.contains(player.getUniqueId())) {
            return CompletableFuture.completedFuture(false);
        }
        return memberRepository.getSnapshotIdsForUser(player.getUniqueId()).thenApplyAsync(ids -> optimizeFull(ids,
            player.getUniqueId(), sender, name)).join();
    }

    @Override
    public boolean optimize(Collection<? extends Player> players, CommandSender sender, String name) {
        if (optimizationTaskRunning) {
            return false;
        }
        CompletableFuture.runAsync(() -> {
            for (Player player : players) {
                optimize(player, sender, name).join();
                incrementCompleted();

                if (requestCancelOptimizationTask) {
                    break;
                }
            }
        }).thenAcceptAsync(v -> {
            printOptimizationFinished(
                sender,
                getSnapshotsDeleted(),
                getSnapshotsUploaded(),
                getMembersCompleted()
            );
            resetCounters();
            stopOptimizationTask();
        });
        return true;
    }

    @Override
    public boolean optimize(CommandSender sender) {
        if (optimizationTaskRunning) {
            return false;
        }
        optimizationTaskRunning = true;
        CompletableFuture.runAsync(() -> {
            List<TKey> memberIds = memberRepository.getAllIds().join();
            setTotalMembers(memberIds.size());
            for (TKey memberId : memberIds) {
                Optional<Member<TKey>> optionalMember = memberRepository.getOne(memberId).join();
                if (!optionalMember.isPresent()) continue;
                Member<TKey> member = optionalMember.get();
                if (!lockedPlayers.contains(member.getUserUUID())) {
                    optimizeFull(member.getSnapshotIds(), member.getUserUUID(), sender, "Manual").join();
                }
                incrementCompleted();

                if (requestCancelOptimizationTask) {
                    break;
                }
            }
        }).thenAcceptAsync(v ->
            printOptimizationFinished(
                sender,
                getSnapshotsDeleted(),
                getSnapshotsUploaded(),
                getMembersCompleted()
            ));
        return true;
    }

    private void printOptimizationFinished(CommandSender sender, int snapshotsDeleted,
                                           int snapshotsUploaded, int membersCompleted) {
        String snapshotsDeletedString = snapshotsDeleted == 1 ? " snapshot from " : " snapshots from ";
        String snapshotsUploadedString = snapshotsUploaded == 1 ? " snapshot " : " snapshots ";
        String memberString = membersCompleted == 1 ? " user!" : " users!";
        sender.sendMessage("Optimization Complete! uploaded " + snapshotsUploaded + snapshotsUploadedString
            + " and removed " + snapshotsDeleted + snapshotsDeletedString + membersCompleted + memberString);
    }
}
