package org.anvilpowered.datasync.nukkit.snapshotoptimization

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.command.CommandSender
import cn.nukkit.command.ConsoleCommandSender
import com.google.inject.Inject
import com.google.inject.Singleton
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.anvil.api.util.TextService
import org.anvilpowered.datasync.common.snapshotoptimization.CommonSnapshotOptimizationService
import java.util.concurrent.CompletableFuture

@Singleton
class NukkitSnapshotOptimizationService<TKey, TDataStore> @Inject constructor(
    registry: Registry
) : CommonSnapshotOptimizationService<TKey, Player, Player, CommandSender, String, TDataStore>(registry) {

    @Inject
    private lateinit var textService: TextService<String, CommandSender>

    private fun sendMessageToSourceAndConsole(sender: CommandSender, message: String) {
        sender.sendMessage(message)
        if (sender !is ConsoleCommandSender) {
            Server.getInstance().consoleSender.sendMessage(message)
        }
    }

    private fun optimize(player: Player, sender: CommandSender, name: String): CompletableFuture<Boolean> {
        return if (lockedPlayers.contains(player.uniqueId)) {
            CompletableFuture.completedFuture(false)
        } else memberRepository.getSnapshotIdsForUser(player.uniqueId).thenApplyAsync { ids: List<TKey> ->
            optimizeFull(ids, player.uniqueId, sender, name)
        }.join()
    }

    override fun optimize(players: Collection<Player>, sender: CommandSender, name: String): Boolean {
        if (optimizationTaskRunning) {
            return false
        }
        CompletableFuture.runAsync {
            for (player in players) {
                optimize(player, sender, name).join()
                incrementCompleted()
                if (requestCancelOptimizationTask) {
                    break
                }
            }
        }.thenAcceptAsync {
            printOptimizationFinished(sender, snapshotsDeleted, snapshotsUploaded, membersCompleted)
        }
        return true
    }

    override fun optimize(source: CommandSender): Boolean {
        if (optimizationTaskRunning) {
            return false
        }
        optimizationTaskRunning = true
        CompletableFuture.runAsync {
            val memberIds = memberRepository.allIds.join()
            totalMembers = memberIds.size
            for (memberId in memberIds) {
                val optionalMember = memberRepository.getOne(memberId).join()
                if (!optionalMember.isPresent) {
                    continue
                }
                val member = optionalMember.get()
                if (!lockedPlayers.contains(member.userUUID)) {
                    optimizeFull(member.snapshotIds, member.userUUID, source, "Manual").join()
                }
                incrementCompleted()
                if (requestCancelOptimizationTask) {
                    break
                }
            }
        }.thenAcceptAsync {
            printOptimizationFinished(source, snapshotsDeleted, snapshotsUploaded, membersCompleted)
        }
        return true
    }

    override fun sendError(source: CommandSender, message: String) {
        sendMessageToSourceAndConsole(source, message)
    }

    override fun submitTask(runnable: Runnable) {
        Server.getInstance().scheduler.run { runnable }
    }

    private fun printOptimizationFinished(sender: CommandSender, snapshotsDeleted: Int,
                                          snapshotsUploaded: Int, membersCompleted: Int) {
        val snapshotsDeletedString = if (snapshotsDeleted == 1) " snapshot from " else " snapshots from "
        val snapshotsUploadedString = if (snapshotsUploaded == 1) " snapshot " else " snapshots "
        val memberString = if (membersCompleted == 1) " user!" else " users!"
        textService.builder()
            .appendPrefix()
            .yellow().append("Optimization Complete! Uploaded ")
            .append(snapshotsUploaded).append(snapshotsUploadedString)
            .append(" and remove ").append(snapshotsDeleted).append(snapshotsDeletedString)
            .append(membersCompleted).append(memberString)
            .sendTo(sender)
    }
}
