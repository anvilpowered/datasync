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

package rocks.milspecsg.msdatasync.service.common.serializer.user;

import com.google.inject.Inject;
import rocks.milspecsg.msdatasync.api.misc.DateFormatService;
import rocks.milspecsg.msdatasync.api.serializer.user.UserSerializerManager;
import rocks.milspecsg.msdatasync.api.serializer.user.component.UserSerializerComponent;
import rocks.milspecsg.msdatasync.model.core.snapshot.Snapshot;
import rocks.milspecsg.msrepository.PluginInfo;
import rocks.milspecsg.msrepository.api.UserService;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;
import rocks.milspecsg.msrepository.api.tools.resultbuilder.StringResult;
import rocks.milspecsg.msrepository.service.common.manager.CommonManager;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class CommonUserSerializerManager<
    TSnapshot extends Snapshot<?>,
    TUser,
    TString>
    extends CommonManager<UserSerializerComponent<?, TSnapshot, TUser, ?, ?>>
    implements UserSerializerManager<TSnapshot, TUser, TString> {

    @Inject
    StringResult<TString> stringResult;

    @Inject
    PluginInfo<TString> pluginInfo;

    @Inject
    UserService<TUser> userService;

    @Inject
    DateFormatService dateFormatService;

    @Inject
    public CommonUserSerializerManager(ConfigurationService configurationService) {
        super(configurationService);
    }

    @Override
    public CompletableFuture<TString> serialize(Collection<? extends TUser> users) {
        if (users.isEmpty()) {
            return CompletableFuture.completedFuture(
                stringResult.builder()
                    .append(pluginInfo.getPrefix())
                    .red().append("There are no players currently online")
                    .build()
            );
        }

        ConcurrentLinkedQueue<TUser> successful = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<TUser> unsuccessful = new ConcurrentLinkedQueue<>();
        CompletableFuture<TString> result = new CompletableFuture<>();

        for (TUser user : users) {
            getPrimaryComponent().serialize(user).thenAcceptAsync(optionalSnapshot -> {
                if (optionalSnapshot.isPresent()) {
                    successful.add(user);
                } else {
                    unsuccessful.add(user);
                }
                if (successful.size() + unsuccessful.size() >= users.size()) {
                    StringResult.Builder<TString> builder = stringResult.builder();
                    if (!successful.isEmpty()) {
                        String s = successful.stream().map(u -> userService.getUserName(user)).collect(Collectors.joining(", "));
                        builder.yellow().append("The following players were successfully serialized: \n").green().append(s);
                    }
                    if (!unsuccessful.isEmpty()) {
                        String s = unsuccessful.stream().map(u -> userService.getUserName(user)).collect(Collectors.joining(", "));
                        builder.red().append("The following players were unsuccessfully serialized: \n").green().append(s);
                    }
                    result.complete(builder.build());
                }
            });
        }
        return result;
    }

    @Override
    public CompletableFuture<TString> serialize(TUser user) {
        return getPrimaryComponent().serialize(user).thenApplyAsync(optionalSnapshot ->
            optionalSnapshot.isPresent()
                ? stringResult.builder()
                .append(pluginInfo.getPrefix())
                .yellow().append("Successfully serialized ", userService.getUserName(user), " and uploaded snapshot ")
                .gold().append(dateFormatService.format(optionalSnapshot.get().getCreatedUtcDate()))
                .build()
                : stringResult.builder()
                .append(pluginInfo.getPrefix())
                .red().append("An error occurred while serializing ", userService.getUserName(user))
                .build());
    }
}
