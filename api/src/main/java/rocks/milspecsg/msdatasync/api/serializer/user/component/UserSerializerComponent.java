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

package rocks.milspecsg.msdatasync.api.serializer.user.component;

import rocks.milspecsg.msdatasync.api.model.snapshot.Snapshot;
import rocks.milspecsg.msdatasync.api.serializer.Serializer;
import rocks.milspecsg.msrepository.api.component.Component;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface UserSerializerComponent<
    TKey,
    TSnapshot extends Snapshot<TKey>,
    TUser,
    TDataStore>
    extends Component<TKey, TDataStore>,
    Serializer<TSnapshot, TUser> {

    CompletableFuture<Optional<TSnapshot>> serialize(TUser user, String name);

    CompletableFuture<Optional<TSnapshot>> serialize(TUser user);

    CompletableFuture<Optional<TSnapshot>> deserialize(TUser user, Object plugin, TSnapshot snapshot);

    CompletableFuture<Optional<TSnapshot>> deserialize(TUser user, Object plugin);
}
