/*
 *   DataSync - AnvilPowered
 *   Copyright (C) 2020 Cableguy20
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.anvilpowered.datasync.spigot.snapshotoptimization

import com.google.inject.Inject
import com.google.inject.Singleton
import net.md_5.bungee.api.chat.TextComponent
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.anvil.api.util.TextService
import org.anvilpowered.datasync.common.snapshotoptimization.CommonSnapshotOptimizationService
import org.anvilpowered.datasync.spigot.DataSyncSpigot
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

@Singleton
class SpigotSnapshotOptimizationService<TKey, TDataStore> @Inject constructor(
    registry: Registry
) : CommonSnapshotOptimizationService<TKey, Player, Player, CommandSender, String, TDataStore>(registry) {

    @Inject
    private lateinit var textService: TextService<TextComponent, CommandSender>

    @Inject
    private lateinit var dataSyncSpigot: DataSyncSpigot

    private fun sendMessageToSourceAndConsole(sender: CommandSender, message: String) {
        sender.sendMessage(message)
        if (sender !is ConsoleCommandSender) {
            Bukkit.getConsoleSender().sendMessage(message)
        }
    }

    override fun sendError(sender: CommandSender, message: String) {
        sendMessageToSourceAndConsole(sender, message)
    }

    override fun submitTask(runnable: Runnable) {
        Bukkit.getScheduler().runTask(dataSyncSpigot, runnable)
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
            resetCounters()
            stopOptimizationTask()
        }
        return true
    }

    override fun optimize(sender: CommandSender): Boolean {
        if (optimizationTaskRunning) {
            return false
        }
        optimizationTaskRunning = true
        CompletableFuture.runAsync {
            val memberIds = memberRepository.allIds.join()
            totalMembers = memberIds.size
            for (memberId in memberIds) {
                val optionalMember = memberRepository.getOne(memberId).join()
                if (!optionalMember.isPresent) continue
                val member = optionalMember.get()
                if (!lockedPlayers.contains(member.userUUID)) {
                    optimizeFull(member.snapshotIds, member.userUUID, sender, "Manual").join()
                }
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
