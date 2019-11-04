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

package rocks.milspecsg.msdatasync.service.sponge.snapshotoptimization.component;

import com.google.inject.Inject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import rocks.milspecsg.msdatasync.model.core.member.Member;
import rocks.milspecsg.msdatasync.model.core.snapshot.Snapshot;
import rocks.milspecsg.msdatasync.service.common.snapshotoptimization.component.CommonSnapshotOptimizationService;
import rocks.milspecsg.msrepository.PluginInfo;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;
import rocks.milspecsg.msrepository.datastore.DataStoreConfig;
import rocks.milspecsg.msrepository.datastore.DataStoreContext;

import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Singleton
public class SpongeSnapshotOptimizationService<
    TKey,
    TMember extends Member<TKey>,
    TSnapshot extends Snapshot<TKey>,
    TDataStore,
    TDataStoreConfig extends DataStoreConfig>
    extends CommonSnapshotOptimizationService<TKey, TMember, TSnapshot, Player, User, CommandSource, Key<?>, TDataStore, TDataStoreConfig> {

    @Inject
    protected PluginInfo<Text> pluginInfo;

    @Inject
    public SpongeSnapshotOptimizationService(ConfigurationService configurationService, DataStoreContext<TKey, TDataStore, TDataStoreConfig> dataStoreContext) {
        super(configurationService, dataStoreContext);
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
        sendMessageToSourceAndConsole(source, Text.of(pluginInfo.getPrefix(), TextColors.RED, message));
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
        CompletableFuture.runAsync(() -> {
            for (User user : users) {
                optimize(user, source, name, plugin).join();
                incrementCompleted();

                if (requestCancelOptimizationTask) {
                    break;
                }
            }
        }).thenAcceptAsync(v -> {
            printOptimizationFinished(source, getSnapshotsDeleted(), getSnapshotsUploaded(), getMembersCompleted());
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
        CompletableFuture.runAsync(() -> {
            List<TKey> memberIds = memberRepository.getAllIds().join();
            setTotalMembers(memberIds.size());
            for (TKey memberId : memberIds) {
                Optional<TMember> optionalMember = memberRepository.getOne(memberId).join();
                if (!optionalMember.isPresent()) continue;
                TMember member = optionalMember.get();
                if (!lockedPlayers.contains(member.getUserUUID())) {
                    optimizeFull(member.getSnapshotIds(), member.getUserUUID(), source, "Manual", plugin).join();
                }
                incrementCompleted();

                if (requestCancelOptimizationTask) {
                    break;
                }
            }
        }).thenAcceptAsync(v -> {
            printOptimizationFinished(source, getSnapshotsDeleted(), getSnapshotsUploaded(), getMembersCompleted());
            resetCounters();
            stopOptimizationTask();
        });
        return true;
    }

    private void printOptimizationFinished(final CommandSource source, final int snapshotsDeleted, final int snapshotsUploaded, final int membersCompleted) {
        String snapshotsDeletedString = snapshotsDeleted == 1 ? " snapshot from " : " snapshots from ";
        String snapshotsUploadedString = snapshotsUploaded == 1 ? " snapshot" : " snapshots";
        String memberString = membersCompleted == 1 ? " user!" : " users!";
        source.sendMessage(Text.of(pluginInfo.getPrefix(), TextColors.YELLOW, "Optimization complete! Uploaded ", snapshotsUploaded, snapshotsUploadedString, " and removed ", snapshotsDeleted, snapshotsDeletedString, membersCompleted, memberString));
    }
}
