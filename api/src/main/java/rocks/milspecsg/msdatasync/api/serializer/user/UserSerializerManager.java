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

package rocks.milspecsg.msdatasync.api.serializer.user;

import rocks.milspecsg.msdatasync.api.model.snapshot.Snapshot;
import rocks.milspecsg.msdatasync.api.serializer.user.component.UserSerializerComponent;
import rocks.milspecsg.msrepository.api.manager.Manager;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface UserSerializerManager<
    TSnapshot extends Snapshot<?>,
    TUser,
    TString>
    extends Manager<UserSerializerComponent<?, TSnapshot, TUser, ?>> {

    @Override
    default String getDefaultIdentifierSingularUpper() {
        return "User serializer";
    }

    @Override
    default String getDefaultIdentifierPluralUpper() {
        return "User serializers";
    }

    @Override
    default String getDefaultIdentifierSingularLower() {
        return "user serializer";
    }

    @Override
    default String getDefaultIdentifierPluralLower() {
        return "user serializers";
    }

    CompletableFuture<TString> serialize(Collection<? extends TUser> users);

    CompletableFuture<TString> serialize(TUser user, String name);

    CompletableFuture<TString> serialize(TUser user);

    CompletableFuture<TString> deserialize(TUser user, Object plugin, String event);

    CompletableFuture<TString> deserialize(TUser user, Object plugin);

    CompletableFuture<TString> restore(UUID userUUID, Optional<String> optionalString, Object plugin);
}
