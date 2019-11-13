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

package rocks.milspecsg.msdatasync.service.common.member;

import com.google.inject.Inject;
import rocks.milspecsg.msdatasync.api.member.MemberManager;
import rocks.milspecsg.msdatasync.api.member.repository.MemberRepository;
import rocks.milspecsg.msdatasync.api.misc.DateFormatService;
import rocks.milspecsg.msdatasync.model.core.member.Member;
import rocks.milspecsg.msdatasync.model.core.snapshot.Snapshot;
import rocks.milspecsg.msrepository.PluginInfo;
import rocks.milspecsg.msrepository.api.UserService;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;
import rocks.milspecsg.msrepository.api.tools.resultbuilder.StringResult;
import rocks.milspecsg.msrepository.service.common.manager.CommonManager;

import java.text.ParseException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class CommonMemberManager<
    TMember extends Member<?>,
    TSnapshot extends Snapshot<?>,
    TUser,
    TString,
    TCommandSource>
    extends CommonManager<MemberRepository<?, TMember, TSnapshot, TUser, ?, ?>>
    implements MemberManager<TMember, TSnapshot, TUser, TString> {

    @Inject
    protected StringResult<TString, TCommandSource> stringResult;

    @Inject
    protected PluginInfo<TString> pluginInfo;

    @Inject
    protected UserService<TUser> userService;

    @Inject
    protected DateFormatService dateFormatService;

    @Inject
    public CommonMemberManager(ConfigurationService configurationService) {
        super(configurationService);
    }

    @Override
    public CompletableFuture<TString> deleteSnapshot(UUID userUUID, Optional<String> optionalString) {
        return getPrimaryComponent().getSnapshot(userUUID, optionalString).thenApplyAsync(optionalSnapshot -> {
            String userName = userService.getUserName(userUUID).orElse("null");
            if (!optionalSnapshot.isPresent()) {
                return stringResult.builder()
                    .append(pluginInfo.getPrefix())
                    .red().append("Could not find snapshot for ", userName)
                    .build();
            }

            Date date = optionalSnapshot.get().getCreatedUtcDate();
            String formattedDate = dateFormatService.format(date);

            return getPrimaryComponent().deleteSnapshot(userUUID, date).thenApplyAsync(success -> {
                if (success) {
                    return stringResult.builder()
                        .append(pluginInfo.getPrefix())
                        .yellow().append("Successfully deleted snapshot ")
                        .gold().append(formattedDate)
                        .yellow().append(" for ", userService.getUserName(userUUID).orElse("null"))
                        .build();
                } else {
                    return stringResult.builder()
                        .append(pluginInfo.getPrefix())
                        .red().append("An error occurred while deleting snapshot ", formattedDate, " for ", userName)
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

            Date created = snapshot.getCreatedUtcDate();
            Date updated = snapshot.getUpdatedUtcDate();

            String createdString = dateFormatService.format(created);
            String updatedString = dateFormatService.format(updated);

            long currentTime = new Date().getTime();
            String diffCreatedString = dateFormatService.formatDiff(new Date(currentTime - created.getTime())) + " ago";
            String diffUpdatedString = dateFormatService.formatDiff(new Date(currentTime - updated.getTime())) + " ago";

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
                                    getPrimaryComponent().getPrevious(userUUID, created).thenAcceptAsync(o -> {
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
                                    getPrimaryComponent().getNext(userUUID, created).thenAcceptAsync(o -> {
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
        return getPrimaryComponent().getSnapshot(userUUID, optionalString).thenApplyAsync(optionalSnapshot -> {
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
        return getPrimaryComponent().getSnapshotDates(userUUID).thenApplyAsync(dates -> {
            Collection<TString> lines = new ArrayList<>();
            String userName = userService.getUserName(userUUID).orElse("null");
            long currentTime = new Date().getTime();
            dates.forEach(created -> {
                String createdString = dateFormatService.format(created);
                String diffCreatedString = dateFormatService.formatDiff(new Date(currentTime - created.getTime())) + " ago";
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
