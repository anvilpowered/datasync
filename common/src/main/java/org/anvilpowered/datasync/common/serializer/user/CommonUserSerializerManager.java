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

package org.anvilpowered.datasync.common.serializer.user;

import com.google.inject.Inject;
import org.anvilpowered.anvil.api.data.registry.Registry;
import org.anvilpowered.anvil.api.plugin.PluginInfo;
import org.anvilpowered.anvil.api.util.TextService;
import org.anvilpowered.anvil.api.util.TimeFormatService;
import org.anvilpowered.anvil.api.util.UserService;
import org.anvilpowered.anvil.base.manager.BaseManager;
import org.anvilpowered.datasync.api.member.MemberManager;
import org.anvilpowered.datasync.api.model.member.Member;
import org.anvilpowered.datasync.api.model.snapshot.Snapshot;
import org.anvilpowered.datasync.api.serializer.user.UserSerializerManager;
import org.anvilpowered.datasync.api.serializer.user.component.UserSerializerComponent;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class CommonUserSerializerManager<
    TUser,
    TPlayer,
    TString,
    TCommandSource>
    extends BaseManager<UserSerializerComponent<?, TUser, ?>>
    implements UserSerializerManager<TUser, TString> {

    @Inject
    protected MemberManager<TString> memberManager;

    @Inject
    protected TextService<TString, TCommandSource> textService;

    @Inject
    protected PluginInfo<TString> pluginInfo;

    @Inject
    protected UserService<TUser, TPlayer> userService;

    @Inject
    protected TimeFormatService timeFormatService;

    @Inject
    public CommonUserSerializerManager(Registry registry) {
        super(registry);
    }

    @Override
    public CompletableFuture<TString> serialize(Collection<? extends TUser> users) {
        if (users.isEmpty()) {
            return CompletableFuture.completedFuture(
                textService.builder()
                    .append(pluginInfo.getPrefix())
                    .red().append("There are no players currently online")
                    .build()
            );
        }

        ConcurrentLinkedQueue<TUser> successful = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<TUser> unsuccessful = new ConcurrentLinkedQueue<>();
        CompletableFuture<TString> result = new CompletableFuture<>();

        for (TUser user : users) {
            getPrimaryComponent().serialize(user, "Manual").thenAcceptAsync(optionalSnapshot -> {
                if (optionalSnapshot.isPresent()) {
                    successful.add(user);
                } else {
                    unsuccessful.add(user);
                }
                if (successful.size() + unsuccessful.size() >= users.size()) {
                    TextService.Builder<TString, TCommandSource> builder = textService.builder();
                    if (!successful.isEmpty()) {
                        String s = successful.stream().map(u -> userService.getUserName(user)).collect(Collectors.joining(", "));
                        builder.yellow().append("The following players were successfully serialized:\n").green().append(s);
                    }
                    if (!unsuccessful.isEmpty()) {
                        String s = unsuccessful.stream().map(u -> userService.getUserName(user)).collect(Collectors.joining(", "));
                        builder.red().append("The following players were unsuccessfully serialized:\n").green().append(s);
                    }
                    result.complete(builder.build());
                }
            });
        }
        return result;
    }

    @Override
    public CompletableFuture<TString> serialize(TUser user, String name) {
        return getPrimaryComponent().serialize(user, name).thenApplyAsync(optionalSnapshot -> {
            if (optionalSnapshot.isPresent()) {
                return textService.builder()
                    .append(pluginInfo.getPrefix())
                    .yellow().append("Successfully uploaded snapshot ")
                    .gold().append(
                        timeFormatService.format(optionalSnapshot.get().getCreatedUtc()),
                        " (", name, ")"
                    )
                    .yellow().append(" for ", userService.getUserName(user), "!")
                    .build();
            }
            return textService.builder()
                .append(pluginInfo.getPrefix())
                .red().append(
                    "An error occurred while serializing ", name, " for ",
                    userService.getUserName(user), "!"
                )
                .build();
        });
    }

    @Override
    public CompletableFuture<TString> serialize(TUser user) {
        return serialize(user, "Manual");
    }

    @Override
    public CompletableFuture<TString> deserialize(TUser user, String event) {
        return getPrimaryComponent().deserialize(user).thenApplyAsync(optionalSnapshot -> {
            if (optionalSnapshot.isPresent()) {
                return textService.builder()
                    .append(pluginInfo.getPrefix())
                    .yellow().append("Successfully downloaded snapshot ")
                    .gold().append(
                        timeFormatService.format(optionalSnapshot.get().getCreatedUtc()),
                        " (", optionalSnapshot.get().getName(), ")"
                    )
                    .yellow().append(" for ", userService.getUserName(user), " on ", event, "!")
                    .build();
            }
            return textService.builder()
                .append(pluginInfo.getPrefix())
                .red().append(
                    "An error occurred while deserializing ",
                    userService.getUserName(user), " on ", event, "!"
                )
                .build();
        });
    }

    @Override
    public CompletableFuture<TString> deserialize(TUser user) {
        return deserialize(user, "N/A");
    }

    @Override
    public CompletableFuture<TString> restore(UUID userUUID, Optional<String> optionalString) {
        return memberManager.getPrimaryComponent()
            .getSnapshotForUser(userUUID, optionalString)
            .thenApplyAsync(optionalSnapshot -> {
                Optional<TUser> optionalUser = userService.get(userUUID);
                if (!optionalUser.isPresent()) {
                    return textService.builder()
                        .append(pluginInfo.getPrefix())
                        .red().append("Could not find ", userUUID)
                        .build();
                }
                String userName = userService.getUserName(optionalUser.get());
                if (!optionalSnapshot.isPresent()) {
                    return textService.builder()
                        .append(pluginInfo.getPrefix())
                        .red().append("Could not find snapshot for ", userName)
                        .build();
                }
                String createdString = timeFormatService.format(optionalSnapshot.get().getCreatedUtc());
                getPrimaryComponent().deserialize(optionalSnapshot.get(), optionalUser.get());
                return textService.builder()
                    .append(pluginInfo.getPrefix())
                    .yellow().append("Restored snapshot ", createdString, " for ", userName)
                    .build();
            });
    }
}
