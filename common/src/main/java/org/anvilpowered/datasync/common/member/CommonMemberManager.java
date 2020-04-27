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

package org.anvilpowered.datasync.common.member;

import com.google.inject.Inject;
import org.anvilpowered.anvil.api.data.registry.Registry;
import org.anvilpowered.anvil.api.plugin.PluginInfo;
import org.anvilpowered.anvil.api.util.TextService;
import org.anvilpowered.anvil.api.util.TimeFormatService;
import org.anvilpowered.anvil.api.util.UserService;
import org.anvilpowered.anvil.base.datastore.BaseManager;
import org.anvilpowered.datasync.api.member.MemberManager;
import org.anvilpowered.datasync.api.member.repository.MemberRepository;
import org.anvilpowered.datasync.api.model.snapshot.Snapshot;

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
    TUser,
    TPlayer,
    TString,
    TCommandSource>
    extends BaseManager<MemberRepository<?, ?>>
    implements MemberManager<TString> {

    @Inject
    protected TextService<TString, TCommandSource> textService;

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
            String userName = userService.getUserName(userUUID).join().orElse("null");
            if (!optionalSnapshot.isPresent()) {
                return textService.builder()
                    .append(pluginInfo.getPrefix())
                    .red().append("Could not find snapshot for ", userName)
                    .build();
            }

            Instant createdUtc = optionalSnapshot.get().getCreatedUtc();
            String formattedInstant = timeFormatService.format(createdUtc).get();

            return getPrimaryComponent().deleteSnapshotForUser(userUUID, createdUtc).thenApplyAsync(success -> {
                if (success) {
                    return textService.builder()
                        .append(pluginInfo.getPrefix())
                        .yellow().append("Successfully deleted snapshot ")
                        .gold().append(formattedInstant)
                        .yellow().append(" for ", userService.getUserName(userUUID).join().orElse("null"))
                        .build();
                } else {
                    return textService.builder()
                        .append(pluginInfo.getPrefix())
                        .red().append("An error occurred while deleting snapshot ", formattedInstant, " for ", userName)
                        .build();
                }
            }).join();
        });
    }

    private TString getSnapshotActions(String userName, String createdString) {
        return textService.builder()
            .append(
                textService.builder()
                    .green().append("[ Restore ]")
                    .onHoverShowText(textService.builder().green().append("Click to restore"))
                    .onClickSuggestCommand("/sync snapshot restore " + userName + " " + createdString)
            ).append(" ")
            .append(
                textService.builder()
                    .gold().append("[ Edit ]")
                    .onHoverShowText(textService.builder().gold().append("Click to edit"))
                    .onClickSuggestCommand("/sync snapshot edit " + userName + " " + createdString)
            ).append(" ")
            .append(
                textService.builder()
                    .red().append("[ Delete ]")
                    .onHoverShowText(textService.builder().red().append("Click to delete"))
                    .onClickSuggestCommand("/sync snapshot delete " + userName + " " + createdString)
            ).build();
    }

    @Override
    public TString info(UUID userUUID, Snapshot<?> snapshot) {
        String userName = userService.getUserName(userUUID).join().orElse("null");

        Instant created = snapshot.getCreatedUtc();
        Instant updated = snapshot.getUpdatedUtc();

        String createdString = timeFormatService.format(created).get();
        String updatedString = timeFormatService.format(updated).get();

        String diffCreatedString = timeFormatService.format(Duration.between(created, OffsetDateTime.now(ZoneOffset.UTC).toInstant())) + " ago";
        String diffUpdatedString = timeFormatService.format(Duration.between(updated, OffsetDateTime.now(ZoneOffset.UTC).toInstant())) + " ago";

        return textService.builder()
            .append(textService.builder().dark_green().append("======= ").gold().append("Snapshot - ", userName).dark_green().append(" ======="))
            .append(textService.builder().gray().append("\n\nId: ").yellow().append(snapshot.getId()))
            .append(textService.builder().gray().append("\n\nCreated: ")
                .append(
                    textService.builder().yellow().append(createdString).onHoverShowText(
                        textService.builder().aqua().append(diffCreatedString)
                    )
                )
            ).append(textService.builder().gray().append("\n\nUpdated: ")
                .append(
                    textService.builder().yellow().append(updatedString).onHoverShowText(
                        textService.builder().aqua().append(diffUpdatedString)
                    )
                )
            ).append(textService.builder().gray().append("\n\nName: ").yellow().append(snapshot.getName()))
            .append(textService.builder().gray().append("\n\nServer: ")).yellow().append(snapshot.getServer())
            .append("\n\n")
            .append(getSnapshotActions(userName, createdString))
            .append("\n\n        ")
            .append(
                textService.builder()
                    .append(
                        textService.builder()
                            .aqua().append("[ < ]")
                            .onHoverShowText(textService.builder().aqua().append("Previous"))
                            .onClickExecuteCallback(cs -> {
                                getPrimaryComponent().getPreviousForUser(userUUID, created).thenAcceptAsync(o -> {
                                    if (o.isPresent()) {
                                        textService.send(info(userUUID, o.get()), cs);
                                    } else {
                                        textService.builder().red().append(pluginInfo.getPrefix(), "No previous snapshot exists for ", userName, "!").sendTo(cs);
                                    }
                                });
                            })
                    ).append(" ")
                    .append(
                        textService.builder()
                            .aqua().append("[ List ]")
                            .onHoverShowText(textService.builder().aqua().append("Go back to list"))
                            .onClickRunCommand("/sync snapshot list " + userName)
                    ).append(" ")
                    .append(
                        textService.builder()
                            .aqua().append("[ > ]")
                            .onHoverShowText(textService.builder().aqua().append("Next"))
                            .onClickExecuteCallback(cs -> {
                                getPrimaryComponent().getNextForUser(userUUID, created).thenAcceptAsync(o -> {
                                    if (o.isPresent()) {
                                        textService.send(info(userUUID, o.get()), cs);
                                    } else {
                                        textService.builder().red().append(pluginInfo.getPrefix(), "No next snapshot exists for ", userName, "!").sendTo(cs);
                                    }
                                });
                            })
                    ).append("\n\n")
            ).append(textService.builder().dark_green().append("======= ").gold().append("Snapshot - ", userName).dark_green().append(" ======="))
            .build();
    }

    @Override
    public CompletableFuture<TString> info(UUID userUUID, Optional<String> optionalString) {
        return getPrimaryComponent().getSnapshotForUser(userUUID, optionalString).thenApplyAsync(optionalSnapshot -> {
            String userName = userService.getUserName(userUUID).join().orElse("null");
            if (!optionalSnapshot.isPresent()) {
                return textService.builder()
                    .append(pluginInfo.getPrefix())
                    .red().append("Could not find snapshot for ", userName)
                    .build();
            }
            return info(userUUID, optionalSnapshot.get());
        });
    }

    @Override
    public CompletableFuture<Iterable<TString>> list(UUID userUUID) {
        return getPrimaryComponent().getSnapshotCreationTimesForUser(userUUID).thenApplyAsync(createdUtcs -> {
            Collection<TString> lines = new ArrayList<>();
            String userName = userService.getUserName(userUUID).join().orElse("null");
            createdUtcs.forEach(created -> {
                String createdString = timeFormatService.format(created).get();
                String diffCreatedString = timeFormatService.format(Duration.between(created, OffsetDateTime.now(ZoneOffset.UTC).toInstant())) + " ago";
                lines.add(textService.builder()
                    .append(
                        textService.builder()
                            .yellow().append(createdString)
                            .onHoverShowText(
                                textService.builder()
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
