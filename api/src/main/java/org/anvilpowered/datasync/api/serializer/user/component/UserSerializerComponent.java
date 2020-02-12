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

package org.anvilpowered.datasync.api.serializer.user.component;

import org.anvilpowered.anvil.api.component.Component;
import org.anvilpowered.datasync.api.model.snapshot.Snapshot;
import org.anvilpowered.datasync.api.serializer.Serializer;

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

    CompletableFuture<Optional<TSnapshot>> deserialize(TUser user, Object plugin, TSnapshot snapshot);

    CompletableFuture<Optional<TSnapshot>> deserialize(TUser user, Object plugin);
}
