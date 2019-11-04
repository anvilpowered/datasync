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

package rocks.milspecsg.msdatasync.api.serializer;

import rocks.milspecsg.msdatasync.model.core.snapshot.Snapshot;

public interface Serializer<
    TSnapshot extends Snapshot<?>,
    TUser> {

    /**
     * @return Name of {@link Serializer}.
     * Should follow format "plugin:name"
     * For example "msdatasync:inventory"
     */
    String getName();

    /**
     * Moves data from {@code player} into {@code member}
     *
     * @param snapshot {@link Snapshot} to add data to
     * @param user     User to get data from
     * @return Whether serialization was successful
     */
    boolean serialize(TSnapshot snapshot, TUser user);

    /**
     * Moves data from {@code member} into {@code player}
     *
     * @param snapshot {@link Snapshot} to get data from
     * @param user     User to add data to
     * @return Whether deserialization was successful
     */
    boolean deserialize(TSnapshot snapshot, TUser user);

}
