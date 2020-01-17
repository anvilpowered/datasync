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

import rocks.milspecsg.msdatasync.api.model.snapshot.Snapshot;

public interface InventorySerializer<
    TSnapshot extends Snapshot<?>,
    TUser,
    TInventory,
    TItemStackSnapshot>
    extends Serializer<TSnapshot, TUser> {

    /**
     * Moves data from {@code inventory} into {@code member}
     *
     * @param snapshot  {@link Snapshot} to add data to
     * @param inventory Player to get data from
     * @param maxSlots Maximum number of slots that will get serialized
     * @return Whether serialization was successful
     */
    boolean serializeInventory(TSnapshot snapshot, TInventory inventory, int maxSlots);

    /**
     * Moves data from {@code inventory} into {@code member}
     *
     * @param snapshot  {@link Snapshot} to add data to
     * @param inventory Player to get data from
     * @return Whether serialization was successful
     */
    boolean serializeInventory(TSnapshot snapshot, TInventory inventory);

    /**
     * Moves data from {@code member} into {@code inventory}
     *
     * @param snapshot  {@link Snapshot} to get data from
     * @param inventory Player to add data to
     * @param fallbackItemStackSnapshot Item stack to put into unused slots. To be made uneditable
     * @return Whether deserialization was successful
     */
    boolean deserializeInventory(TSnapshot snapshot, TInventory inventory, TItemStackSnapshot fallbackItemStackSnapshot);

    /**
     * Moves data from {@code member} into {@code inventory}
     *
     * @param snapshot  {@link Snapshot} to get data from
     * @param inventory Player to add data to
     * @return Whether deserialization was successful
     */
    boolean deserializeInventory(TSnapshot snapshot, TInventory inventory);

    TItemStackSnapshot getDefaultFallbackItemStackSnapshot();

    TItemStackSnapshot getExitWithoutSavingItemStackSnapshot();
}
