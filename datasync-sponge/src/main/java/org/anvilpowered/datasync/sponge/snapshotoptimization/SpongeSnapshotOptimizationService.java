/*
 *   DataSync - AnvilPowered
 *   Copyright (C) 2020 Cableguy20
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

package org.anvilpowered.datasync.sponge.snapshotoptimization;

import com.google.inject.Inject;
import org.anvilpowered.anvil.api.datastore.DataStoreContext;
import org.anvilpowered.anvil.api.plugin.PluginInfo;
import org.anvilpowered.anvil.api.registry.Registry;
import org.anvilpowered.datasync.api.model.member.Member;
import org.anvilpowered.datasync.common.snapshotoptimization.CommonSnapshotOptimizationService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.inject.Singleton;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Singleton
public class SpongeSnapshotOptimizationService<
    TKey,
    TDataStore>
    extends CommonSnapshotOptimizationService<
    TKey, User, Player, CommandSource, Key<?>, TDataStore> {

    @Inject
    protected PluginInfo<Text> pluginInfo;

    @Inject
    protected PluginContainer pluginContainer;

    @Inject
    public SpongeSnapshotOptimizationService(
        Registry registry,
        DataStoreContext<TKey, TDataStore> dataStoreContext
    ) {
        super(registry, dataStoreContext);
    }

    private void sendMessageToSourceAndConsole(final CommandSource source, Text message) {
        source.sendMessage(message);
        if (!(source instanceof ConsoleSource)) {
            Sponge.getServer().getConsole().sendMessage(message);
        }
    }

    @Override
    protected void sendError(final CommandSource source, final String message) {
        sendMessageToSourceAndConsole(source,
            Text.of(pluginInfo.getPrefix(), TextColors.RED, message));
    }

    @Override
    protected void submitTask(Runnable runnable) {
        Task.builder().execute(runnable).submit(pluginContainer);
    }

    private CompletableFuture<Boolean> optimize(final User user, final CommandSource source,
                                                final String name) {
        if (lockedPlayers.contains(user.getUniqueId())) {
            return CompletableFuture.completedFuture(false);
        }
        return memberRepository.getSnapshotIdsForUser(user.getUniqueId()).thenApplyAsync(ids ->
            optimizeFull(ids, user.getUniqueId(), source, name).join()
        );
    }

    @Override
    public boolean optimize(final Collection<? extends User> users, final CommandSource source,
                            final String name) {
        if (isOptimizationTaskRunning()) {
            return false;
        }
        CompletableFuture.runAsync(() -> {
            for (User user : users) {
                optimize(user, source, name).join();
                incrementCompleted();

                if (requestCancelOptimizationTask) {
                    break;
                }
            }
        }).thenAcceptAsync(v -> {
            printOptimizationFinished(
                source,
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
    public boolean optimize(final CommandSource source) {
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
                    optimizeFull(member.getSnapshotIds(), member.getUserUUID(), source, "Manual")
                        .join();
                }
                incrementCompleted();

                if (requestCancelOptimizationTask) {
                    break;
                }
            }
        }).thenAcceptAsync(v -> {
            printOptimizationFinished(
                source,
                getSnapshotsDeleted(),
                getSnapshotsUploaded(),
                getMembersCompleted()
            );
            resetCounters();
            stopOptimizationTask();
        });
        return true;
    }

    private void printOptimizationFinished(CommandSource source, int snapshotsDeleted,
                                           int snapshotsUploaded, int membersCompleted) {
        String snapshotsDeletedString = snapshotsDeleted == 1
                                        ? " snapshot from " : " snapshots from ";
        String snapshotsUploadedString = snapshotsUploaded == 1
                                         ? " snapshot" : " snapshots";
        String memberString = membersCompleted == 1
                              ? " user!" : " users!";
        source.sendMessage(Text.of(pluginInfo.getPrefix(), TextColors.YELLOW,
            "Optimization complete! Uploaded ", snapshotsUploaded, snapshotsUploadedString,
            " and removed ", snapshotsDeleted, snapshotsDeletedString, membersCompleted,
            memberString));
    }
}
