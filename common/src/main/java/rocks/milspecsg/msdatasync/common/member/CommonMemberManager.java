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

package rocks.milspecsg.msdatasync.common.member;

import com.google.inject.Inject;
import rocks.milspecsg.msdatasync.api.member.MemberManager;
import rocks.milspecsg.msdatasync.api.member.repository.MemberRepository;
import rocks.milspecsg.msdatasync.api.model.member.Member;
import rocks.milspecsg.msdatasync.api.model.snapshot.Snapshot;
import rocks.milspecsg.msrepository.api.data.registry.Registry;
import rocks.milspecsg.msrepository.api.plugin.PluginInfo;
import rocks.milspecsg.msrepository.api.util.StringResult;
import rocks.milspecsg.msrepository.api.util.TimeFormatService;
import rocks.milspecsg.msrepository.api.util.UserService;
import rocks.milspecsg.msrepository.common.manager.CommonManager;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CommonMemberManager<
    TMember extends Member<?>,
    TSnapshot extends Snapshot<?>,
    TUser,
    TPlayer extends TCommandSource,
    TString,
    TCommandSource>
    extends CommonManager<MemberRepository<?, TMember, TSnapshot, TUser, ?>>
    implements MemberManager<TMember, TSnapshot, TUser, TString> {

    @Inject
    protected StringResult<TString, TCommandSource> stringResult;

    @Inject
    protected PluginInfo<TString> pluginInfo;

    @Inject
    protected UserService<TUser, TPlayer> userService;

    @Inject
    protected TimeFormatService timeFormatService;

    @Inject
    public CommonMemberManager(Registry registry) {
        super(registry);
    }

    @Override
    public CompletableFuture<TString> deleteSnapshot(UUID userUUID, Optional<String> optionalString) {
        return getPrimaryComponent().getSnapshotForUser(userUUID, optionalString).thenApplyAsync(optionalSnapshot -> {
            String userName = userService.getUserName(userUUID).orElse("null");
            if (!optionalSnapshot.isPresent()) {
                return stringResult.builder()
                    .append(pluginInfo.getPrefix())
                    .red().append("Could not find snapshot for ", userName)
                    .build();
            }

            Instant createdUtc = optionalSnapshot.get().getCreatedUtc();
            String formattedInstant = timeFormatService.format(createdUtc);

            return getPrimaryComponent().deleteSnapshotForUser(userUUID, createdUtc).thenApplyAsync(success -> {
                if (success) {
                    return stringResult.builder()
                        .append(pluginInfo.getPrefix())
                        .yellow().append("Successfully deleted snapshot ")
                        .gold().append(formattedInstant)
                        .yellow().append(" for ", userService.getUserName(userUUID).orElse("null"))
                        .build();
                } else {
                    return stringResult.builder()
                        .append(pluginInfo.getPrefix())
                        .red().append("An error occurred while deleting snapshot ", formattedInstant, " for ", userName)
                        .build();
                }
            }).join();
        });
    }

    private TString getSnapshotActions(String userName, String createdString) {
        return stringResult.builder()
            .append(
                stringResult.builder()
                    .green().append("[ Restore ]")
                    .onHoverShowText(stringResult.builder().green().append("Click to restore"))
                    .onClickSuggestCommand("/sync snapshot restore " + userName + " " + createdString)
            ).append(" ")
            .append(
                stringResult.builder()
                    .gold().append("[ Edit ]")
                    .onHoverShowText(stringResult.builder().gold().append("Click to edit"))
                    .onClickSuggestCommand("/sync snapshot edit " + userName + " " + createdString)
            ).append(" ")
            .append(
                stringResult.builder()
                    .red().append("[ Delete ]")
                    .onHoverShowText(stringResult.builder().red().append("Click to delete"))
                    .onClickSuggestCommand("/sync snapshot delete " + userName + " " + createdString)
            ).build();
    }

    @Override
    public CompletableFuture<TString> info(UUID userUUID, TSnapshot snapshot) {
        return CompletableFuture.supplyAsync(() -> {
            String userName = userService.getUserName(userUUID).orElse("null");

            Instant created = snapshot.getCreatedUtc();
            Instant updated = snapshot.getUpdatedUtc();

            String createdString = timeFormatService.format(created);
            String updatedString = timeFormatService.format(updated);

            String diffCreatedString = timeFormatService.format(Duration.between(created, OffsetDateTime.now(ZoneOffset.UTC).toInstant())) + " ago";
            String diffUpdatedString = timeFormatService.format(Duration.between(updated, OffsetDateTime.now(ZoneOffset.UTC).toInstant())) + " ago";

            return stringResult.builder()
                .append(stringResult.builder().dark_green().append("======= ").gold().append("Snapshot - ", userName).dark_green().append(" ======="))
                .append(stringResult.builder().gray().append("\n\nId: ").yellow().append(snapshot.getId()))
                .append(stringResult.builder().gray().append("\n\nCreated: ")
                    .append(
                        stringResult.builder().yellow().append(createdString).onHoverShowText(
                            stringResult.builder().aqua().append(diffCreatedString)
                        )
                    )
                ).append(stringResult.builder().gray().append("\n\nUpdated: ")
                    .append(
                        stringResult.builder().yellow().append(updatedString).onHoverShowText(
                            stringResult.builder().aqua().append(diffUpdatedString)
                        )
                    )
                ).append(stringResult.builder().gray().append("\n\nName: ").yellow().append(snapshot.getName()))
                .append(stringResult.builder().gray().append("\n\nServer: ")).yellow().append(snapshot.getServer())
                .append("\n\n")
                .append(getSnapshotActions(userName, createdString))
                .append("\n\n        ")
                .append(
                    stringResult.builder()
                        .append(
                            stringResult.builder()
                                .aqua().append("[ < ]")
                                .onHoverShowText(stringResult.builder().aqua().append("Previous"))
                                .onClickExecuteCallback(cs -> {
                                    getPrimaryComponent().getPreviousForUser(userUUID, created).thenAcceptAsync(o -> {
                                        if (o.isPresent()) {
                                            info(userUUID, o.get()).thenAcceptAsync(result -> stringResult.send(result, cs));
                                        } else {
                                            stringResult.builder().red().append(pluginInfo.getPrefix(), "No previous snapshot exists for ", userName, "!").sendTo(cs);
                                        }
                                    });
                                })
                        ).append(" ")
                        .append(
                            stringResult.builder()
                                .aqua().append("[ List ]")
                                .onHoverShowText(stringResult.builder().aqua().append("Go back to list"))
                                .onClickRunCommand("/sync snapshot list " + userName)
                        ).append(" ")
                        .append(
                            stringResult.builder()
                                .aqua().append("[ > ]")
                                .onHoverShowText(stringResult.builder().aqua().append("Next"))
                                .onClickExecuteCallback(cs -> {
                                    getPrimaryComponent().getNextForUser(userUUID, created).thenAcceptAsync(o -> {
                                        if (o.isPresent()) {
                                            info(userUUID, o.get()).thenAcceptAsync(result -> stringResult.send(result, cs));
                                        } else {
                                            stringResult.builder().red().append(pluginInfo.getPrefix(), "No next snapshot exists for ", userName, "!").sendTo(cs);
                                        }
                                    });
                                })
                        ).append("\n\n")
                ).append(stringResult.builder().dark_green().append("======= ").gold().append("Snapshot - ", userName).dark_green().append(" ======="))
                .build();
        });
    }

    @Override
    public CompletableFuture<TString> info(UUID userUUID, Optional<String> optionalString) {
        return getPrimaryComponent().getSnapshotForUser(userUUID, optionalString).thenApplyAsync(optionalSnapshot -> {
            String userName = userService.getUserName(userUUID).orElse("null");
            if (!optionalSnapshot.isPresent()) {
                return stringResult.builder()
                    .append(pluginInfo.getPrefix())
                    .red().append("Could not find snapshot for ", userName)
                    .build();
            }
            return info(userUUID, optionalSnapshot.get()).join();
        });
    }

    @Override
    public CompletableFuture<Iterable<TString>> list(UUID userUUID) {
        return getPrimaryComponent().getSnapshotCreationTimesForUser(userUUID).thenApplyAsync(createdUtcs -> {
            Collection<TString> lines = new ArrayList<>();
            String userName = userService.getUserName(userUUID).orElse("null");
            createdUtcs.forEach(created -> {
                String createdString = timeFormatService.format(created);
                String diffCreatedString = timeFormatService.format(Duration.between(created, OffsetDateTime.now(ZoneOffset.UTC).toInstant())) + " ago";
                lines.add(stringResult.builder()
                    .append(
                        stringResult.builder()
                            .yellow().append(createdString)
                            .onHoverShowText(
                                stringResult.builder()
                                    .yellow().append("Click for more info\n")
                                    .aqua().append(diffCreatedString)
                            ).onClickRunCommand("/sync snapshot info " + userName + " " + createdString)
                    ).append(" ")
                    .append(getSnapshotActions(userName, createdString))
                    .build()
                );
            });
            return lines;
        });
    }
}
