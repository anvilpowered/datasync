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

package org.anvilpowered.datasync.sponge.serializer;

import com.google.inject.Inject;
import org.anvilpowered.datasync.api.model.snapshot.Snapshot;
import org.anvilpowered.datasync.common.serializer.CommonInventorySerializer;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class SpongeInventorySerializer
    extends CommonInventorySerializer<Key<?>, User, Inventory, ItemStackSnapshot> {


    private static final int INVENTORY_SLOTS = 41;

    @Inject
    private Logger logger;

    @Override
    public boolean serializeInventory(Snapshot<?> snapshot, Inventory inventory, int maxSlots) {
        boolean success = true;
        List<String> itemStacks = new ArrayList<>();
        Iterator<Inventory> iterator = inventory.slots().iterator();

        for (int i = 0; i < maxSlots; i++) {
            if (!iterator.hasNext()) break;
            Inventory slot = iterator.next();
            ItemStack stack = slot.peek().orElse(ItemStack.empty());
            String json = "error";
            try {
                json = DataFormats.JSON.write(stack.toContainer());
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("There was an error while serializing slot " + i +
                    " with item " + stack.getType().getId());
                success = false;
            }
            itemStacks.add(json);
        }
        snapshot.setItemStacks(itemStacks);
        return success;
    }

    @Override
    public boolean serializeInventory(Snapshot<?> snapshot, Inventory inventory) {
        return serializeInventory(snapshot, inventory, INVENTORY_SLOTS);
    }

    @Override
    public boolean serialize(Snapshot<?> snapshot, User user) {
        return serializeInventory(snapshot, user.getInventory());
    }

    @Override
    public boolean deserializeInventory(Snapshot<?> snapshot, Inventory inventory, ItemStackSnapshot fallbackItemStackSnapshot) {
        inventory.clear();
        Iterator<String> stacks = snapshot.getItemStacks().iterator();
        Iterator<Inventory> slots = inventory.slots().iterator();
        while (slots.hasNext()) {
            Inventory slot = slots.next();
            if (stacks.hasNext()) {
                String stack = stacks.next();
                if ("error".equals(stack)) {
                    logger.error("ItemStack error detected from DB");
                }
                try {
                    Optional<ItemStack> itemStack = Sponge.getDataManager()
                        .deserialize(ItemStack.class, DataFormats.JSON.read(stack));
                    if (itemStack.isPresent()) {
                        slot.set(itemStack.get());
                    } else {
                        logger.error("Failed to parse ItemStack from DB");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (!(inventory instanceof PlayerInventory)) {
                if (slots.hasNext()) {
                    slot.set(fallbackItemStackSnapshot.createStack());
                } else {
                    slot.set(exitWithoutSavingItemStackSnapshot.createStack());
                }
            }
        }
        return true;
    }

    @Override
    public boolean deserializeInventory(Snapshot<?> snapshot, Inventory inventory) {
        return deserializeInventory(snapshot, inventory, defaultFallbackItemStackSnapshot);
    }

    @Override
    public boolean deserialize(Snapshot<?> snapshot, User user) {
        return deserializeInventory(snapshot, user.getInventory());
    }

    private ItemStackSnapshot defaultFallbackItemStackSnapshot =
        ItemStack.builder().itemType(ItemTypes.BARRIER).quantity(1).add(Keys.DISPLAY_NAME, Text.of(TextColors.RED, "Not an actual slot")).build().createSnapshot();

    @Override
    public ItemStackSnapshot getDefaultFallbackItemStackSnapshot() {
        return defaultFallbackItemStackSnapshot;
    }

    private ItemStackSnapshot exitWithoutSavingItemStackSnapshot =
        ItemStack.builder().itemType(ItemTypes.GOLD_INGOT).quantity(1).add(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD, "Exit without saving")).build().createSnapshot();

    @Override
    public ItemStackSnapshot getExitWithoutSavingItemStackSnapshot() {
        return exitWithoutSavingItemStackSnapshot;
    }
}
