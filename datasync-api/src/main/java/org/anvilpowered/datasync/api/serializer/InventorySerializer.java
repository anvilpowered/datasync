/*
 *   DataSync - AnvilPowered
 *   Copyright (C) 2020 Cableguy20
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.datasync.api.serializer;

import org.anvilpowered.datasync.api.model.snapshot.Snapshot;

public interface InventorySerializer<
    TUser,
    TInventory,
    TItemStackSnapshot>
    extends Serializer<TUser> {

    /**
     * Moves data from {@code inventory} into {@code member}
     *
     * @param snapshot  {@link Snapshot} to add data to
     * @param inventory Player to get data from
     * @param maxSlots Maximum number of slots that will get serialized
     * @return Whether serialization was successful
     */
    boolean serializeInventory(Snapshot<?> snapshot, TInventory inventory, int maxSlots);

    /**
     * Moves data from {@code inventory} into {@code member}
     *
     * @param snapshot  {@link Snapshot} to add data to
     * @param inventory Player to get data from
     * @return Whether serialization was successful
     */
    boolean serializeInventory(Snapshot<?> snapshot, TInventory inventory);

    /**
     * Moves data from {@code member} into {@code inventory}
     *
     * @param snapshot  {@link Snapshot} to get data from
     * @param inventory Player to add data to
     * @param fallbackItemStackSnapshot Item stack to put into unused slots. To be made uneditable
     * @return Whether deserialization was successful
     */
    boolean deserializeInventory(Snapshot<?> snapshot, TInventory inventory, TItemStackSnapshot fallbackItemStackSnapshot);

    /**
     * Moves data from {@code member} into {@code inventory}
     *
     * @param snapshot  {@link Snapshot} to get data from
     * @param inventory Player to add data to
     * @return Whether deserialization was successful
     */
    boolean deserializeInventory(Snapshot<?> snapshot, TInventory inventory);

    TItemStackSnapshot getDefaultFallbackItemStackSnapshot();

    TItemStackSnapshot getExitWithoutSavingItemStackSnapshot();
}
