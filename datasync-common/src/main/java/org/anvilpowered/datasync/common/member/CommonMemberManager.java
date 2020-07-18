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
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
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
    public CompletableFuture<TString> deleteSnapshot(UUID userUUID, @Nullable String snapshot) {
        return getPrimaryComponent().getSnapshotForUser(userUUID, snapshot).thenApplyAsync(os -> {
            String userName = userService.getUserName(userUUID).join().orElse("null");
            if (!os.isPresent()) {
                return textService.builder()
                    .append(pluginInfo.getPrefix())
                    .red().append("Could not find snapshot for ", userName)
                    .build();
            }

            Instant createdUtc = os.get().getCreatedUtc();
            String formattedInstant = timeFormatService.format(createdUtc).toString();

            return getPrimaryComponent().deleteSnapshotForUser(userUUID, createdUtc)
                .thenApplyAsync(success -> {
                    if (success) {
                        return textService.builder()
                            .append(pluginInfo.getPrefix())
                            .yellow().append("Successfully deleted snapshot ")
                            .gold().append(formattedInstant)
                            .yellow().append(" for ", userService.getUserName(userUUID)
                                .join().orElse("null"))
                            .build();
                    }
                    return textService.builder()
                        .append(pluginInfo.getPrefix())
                        .red().append("An error occurred while deleting snapshot ",
                            formattedInstant, " for ", userName)
                        .build();
                }).join();
        });
    }

    private TString getSnapshotActions(String userName, String created) {
        return textService.builder()
            .append(
                textService.builder()
                    .green().append("[ Restore ]")
                    .onHoverShowText(textService.builder().green().append("Click to restore"))
                    .onClickSuggestCommand("/sync snapshot restore " + userName + " " + created)
            ).append(" ")
            .append(
                textService.builder()
                    .gold().append("[ Edit ]")
                    .onHoverShowText(textService.builder().gold().append("Click to edit"))
                    .onClickSuggestCommand("/sync snapshot edit " + userName + " " + created)
            ).append(" ")
            .append(
                textService.builder()
                    .red().append("[ Delete ]")
                    .onHoverShowText(textService.builder().red().append("Click to delete"))
                    .onClickSuggestCommand("/sync snapshot delete " + userName + " " + created)
            ).build();
    }

    private TString getSnapshotNavigation(UUID userUUID, String userName, Instant created) {
        return textService.builder()
            .append(textService.builder()
                .aqua().append("[ < ]")
                .onHoverShowText(textService.builder().aqua().append("Previous"))
                .onClickExecuteCallback(cs -> getPrimaryComponent()
                    .getPreviousForUser(userUUID, created)
                    .thenAcceptAsync(o -> {
                        if (o.isPresent()) {
                            textService.send(info(userUUID, o.get()), cs);
                        } else {
                            textService.builder().red()
                                .append(pluginInfo.getPrefix(),
                                    "No previous snapshot exists for ", userName, "!"
                                ).sendTo(cs);
                        }
                    }))
            ).append(" ")
            .append(textService.builder()
                .aqua().append("[ List ]")
                .onHoverShowText(textService.builder().aqua().append("Go back to list"))
                .onClickRunCommand("/sync snapshot list " + userName)
            ).append(" ")
            .append(
                textService.builder()
                    .aqua().append("[ > ]")
                    .onHoverShowText(textService.builder().aqua().append("Next"))
                    .onClickExecuteCallback(cs -> getPrimaryComponent()
                        .getNextForUser(userUUID, created)
                        .thenAcceptAsync(o -> {
                            if (o.isPresent()) {
                                textService.send(info(userUUID, o.get()), cs);
                            } else {
                                textService.builder().red()
                                    .append(pluginInfo.getPrefix(),
                                        "No next snapshot exists for ", userName, "!"
                                    ).sendTo(cs);
                            }
                        }))
            ).build();
    }

    private TString getInfoBar(String userName) {
        return textService.builder()
            .dark_green().append("======= ")
            .gold().append("Snapshot - ", userName)
            .dark_green().append(" =======")
            .build();
    }

    private TString getBasicProperty(String name, String value) {
        return textService.builder()
            .gray().append("\n\n", name, ": ")
            .yellow().append(value)
            .build();
    }

    private TString getTimeProperty(String name, String time, String timeDiff) {
        return textService.builder()
            .gray().append("\n\n", name, ": ")
            .append(textService.builder()
                .yellow().append(time)
                .onHoverShowText(textService.builder().aqua().append(timeDiff))
            ).build();
    }

    @Override
    public TString info(UUID userUUID, Snapshot<?> snapshot) {
        String userName = userService.getUserName(userUUID).join().orElse("null");

        Instant created = snapshot.getCreatedUtc();
        Instant updated = snapshot.getUpdatedUtc();

        String createdString = timeFormatService.format(created).toString();
        String updatedString = timeFormatService.format(updated).toString();

        String diffCreatedString = timeFormatService.format(
            Duration.between(created, OffsetDateTime.now(ZoneOffset.UTC).toInstant())
        ) + " ago";
        String diffUpdatedString = timeFormatService.format(
            Duration.between(updated, OffsetDateTime.now(ZoneOffset.UTC).toInstant())
        ) + " ago";

        return textService.builder()
            .append(getInfoBar(userName))
            .append(getBasicProperty("Id", snapshot.getIdAsString()))
            .append(getTimeProperty("Created", createdString, diffCreatedString))
            .append(getTimeProperty("Updated", updatedString, diffUpdatedString))
            .append(getBasicProperty("Name", snapshot.getName()))
            .append(getBasicProperty("Server", snapshot.getServer()))
            .append("\n\n")
            .append(getSnapshotActions(userName, createdString))
            .append("\n\n        ")
            .append(getSnapshotNavigation(userUUID, userName, created))
            .append("\n\n")
            .append(getInfoBar(userName))
            .build();
    }

    @Override
    public CompletableFuture<TString> info(UUID userUUID, @Nullable String snapshot) {
        return getPrimaryComponent().getSnapshotForUser(userUUID, snapshot).thenApplyAsync(os -> {
            String userName = userService.getUserName(userUUID).join().orElse("null");
            if (!os.isPresent()) {
                return textService.builder()
                    .append(pluginInfo.getPrefix())
                    .red().append("Could not find snapshot for ", userName)
                    .build();
            }
            return info(userUUID, os.get());
        });
    }

    @Override
    public CompletableFuture<Iterable<TString>> list(UUID userUUID) {
        return getPrimaryComponent().getSnapshotCreationTimesForUser(userUUID)
            .thenApplyAsync(times -> {
                Collection<TString> lines = new ArrayList<>();
                String userName = userService.getUserName(userUUID).join().orElse("null");
                for (Instant created : times) {
                    String createdString = timeFormatService.format(created).toString();
                    String diffCreatedString = timeFormatService.format(Duration.between(created,
                        OffsetDateTime.now(ZoneOffset.UTC).toInstant())).toString() + " ago";
                    lines.add(textService.builder()
                        .append(textService.builder()
                            .yellow().append(createdString)
                            .onHoverShowText(textService.builder()
                                .yellow().append("Click for more info\n")
                                .aqua().append(diffCreatedString)
                            )
                            .onClickRunCommand("/sync snapshot info " + userName + " "
                                + createdString
                            )
                        ).append(" ")
                        .append(getSnapshotActions(userName, createdString))
                        .build()
                    );
                }
                return lines;
            });
    }
}
