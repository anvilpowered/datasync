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
import rocks.milspecsg.msrepository.api.config.ConfigurationService;
import rocks.milspecsg.msrepository.api.tools.resultbuilder.StringResult;
import rocks.milspecsg.msrepository.service.common.manager.CommonManager;

import java.text.ParseException;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class CommonMemberManager<
    TMember extends Member<?>,
    TSnapshot extends Snapshot<?>,
    TUser,
    TString>
    extends CommonManager<MemberRepository<?, TMember, TSnapshot, TUser, ?, ?>>
    implements MemberManager<TMember, TSnapshot, TUser, TString> {

    @Inject
    protected StringResult<TString> stringResult;

    @Inject
    protected PluginInfo<TString> pluginInfo;

    @Inject
    protected DateFormatService dateFormatService;

    @Inject
    public CommonMemberManager(ConfigurationService configurationService) {
        super(configurationService);
    }

    @Override
    public CompletableFuture<TString> deleteSnapshot(UUID userUUID, String date, String userName) {
        return CompletableFuture.supplyAsync(() -> {
            Date parsedDate;
            try {
                parsedDate = dateFormatService.parse(date);
            } catch (ParseException e) {
                return stringResult.builder()
                    .append(pluginInfo.getPrefix())
                    .red().append("Invalid date format")
                    .build();
            }
            return getPrimaryComponent().deleteSnapshot(userUUID, parsedDate).thenApplyAsync(success -> {
                if (success) {
                    return stringResult.builder()
                        .append(pluginInfo.getPrefix())
                        .yellow().append("Successfully deleted snapshot ")
                        .gold().append(date)
                        .yellow().append(" for user ", userName)
                        .build();
                } else {
                    return stringResult.builder()
                        .append(pluginInfo.getPrefix())
                        .red().append("An error occurred while deleting snapshot ", date, " for user ", userName)
                        .build();
                }
            }).join();
        });
    }
}
