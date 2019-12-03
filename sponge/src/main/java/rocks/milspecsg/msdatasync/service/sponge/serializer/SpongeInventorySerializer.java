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

package rocks.milspecsg.msdatasync.service.sponge.serializer;

import com.google.common.collect.ImmutableList;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import rocks.milspecsg.msdatasync.model.core.serializeditemstack.SerializedItemStack;
import rocks.milspecsg.msdatasync.model.core.snapshot.Snapshot;
import rocks.milspecsg.msdatasync.service.common.serializer.CommonInventorySerializer;

import java.util.*;
import java.util.stream.Collectors;

public class SpongeInventorySerializer extends CommonInventorySerializer<Snapshot<?>, Key<?>, User, Inventory, ItemStackSnapshot> {

    private static final char SEPARATOR = '_';
    private static final char PERIOD_REPLACEMENT = '-';
    private static final char PERIOD = '.';
    private static final int INVENTORY_SLOTS = 41;

    @Override
    @SuppressWarnings("unchecked")
    public boolean serializeInventory(Snapshot<?> snapshot, Inventory inventory, int maxSlots) {
        try {
            boolean success = true;
            List<SerializedItemStack> itemStacks = new ArrayList<>();
            Iterator<Inventory> iterator = inventory.slots().iterator();

            Class<SerializedItemStack> clazz = (Class<SerializedItemStack>) snapshotManager.getPrimaryComponent()
                .getDataStoreContext().getEntityClassUnsafe("serializeditemstack");

            for (int i = 0; i < maxSlots; i++) {
                if (!iterator.hasNext()) break;
                Inventory slot = iterator.next();
                SerializedItemStack serializedItemStack = clazz.newInstance();
                ItemStack stack = slot.peek().orElse(ItemStack.empty());
                DataContainer dc = stack.toContainer();
                try {
                    serializedItemStack.setProperties(serialize(dc.getValues(false)));
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    System.err.println("[MSDataSync] There was an error while serializing slot " + i + " with item " + stack.getType().getId() + "! Will not add this item to snapshot!");
                    success = false;
                    continue;
                }
                itemStacks.add(serializedItemStack);
            }
            snapshot.setItemStacks(itemStacks);
            return success;
        } catch (RuntimeException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
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
        try {
            inventory.clear();
            Iterator<SerializedItemStack> stacks = snapshot.getItemStacks().iterator();
            Iterator<Inventory> slots = inventory.slots().iterator();
            while (slots.hasNext()) {
                Inventory slot = slots.next();
                if (stacks.hasNext()) {
                    SerializedItemStack stack = stacks.next();
                    try {
                        DataContainer dc = DataContainer.createNew(DataView.SafetyMode.ALL_DATA_CLONED);
                        deserialize(stack.getProperties()).forEach(dc::set);
                        ItemStack is = ItemStack.builder().fromContainer(dc).build();
                        slot.set(is);
                    } catch (RuntimeException e) {
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
        } catch (RuntimeException e) {
            e.printStackTrace();
            return false;
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

    private static Map<String, Object> serialize(Map<DataQuery, Object> values) {
        Map<String, Object> result = new HashMap<>();
        values.forEach((dq, o) -> {
            String s = dq.asString(SEPARATOR).replace(PERIOD, PERIOD_REPLACEMENT);
            if (o instanceof Map) {
                Object m = serialize((Map<DataQuery, Object>) o);
                result.put(s, m);
            } else if (o instanceof List) {
                List<?> list = (List<?>) o;
                List<Object> r1 = new ArrayList<>();
                list.forEach(li -> {
                    if (li instanceof DataContainer) {
                        r1.add(serialize(((DataContainer) li).getValues(false)));
                    } else if (li instanceof String) {
                        r1.add(((String) li).replace(PERIOD, PERIOD_REPLACEMENT));
                    }
                });
                result.put(s, r1);
            } else {
                result.put(s, o);
            }
        });
        return result;
    }

    private static Map<DataQuery, Object> deserialize(Map<String, Object> values) {
        Map<DataQuery, Object> result = new HashMap<>();
        for (Map.Entry<String, Object> e : values.entrySet()) {
            String s = e.getKey();
            Object o = e.getValue();
            if (o == null) {
                continue;
            }
            s = s.replace(PERIOD_REPLACEMENT, PERIOD);
            DataQuery dq = DataQuery.of(SEPARATOR, s);
            if (o instanceof Map) {
                Map<String, Object> m = (Map<String, Object>) o;
                Map<DataQuery, Object> r1 = new HashMap<>();
                for (Map.Entry<String, Object> mapEntry : m.entrySet()) {
                    String s1 = mapEntry.getKey();
                    Object m1 = mapEntry.getValue();
                    if (m1 == null) {
                        continue;
                    } else if (m1 instanceof List) {
                        m1 = ((List<?>) m1).stream().filter(Objects::nonNull).collect(Collectors.toList());
                    }
                    Object value = m1;
                    s1 = s1.replace(PERIOD_REPLACEMENT, PERIOD);
                    if (m1 instanceof Map) {
                        try {
                            Map<DataQuery, Object> v = deserialize((Map<String, Object>) m1);
                            DataContainer dc = DataContainer.createNew(DataView.SafetyMode.ALL_DATA_CLONED);
                            v.forEach(dc::set);
                            if (s1.equals("ench")) {
                                value = ImmutableList.of(dc);
                            } else {
                                value = dc;
                            }
                        } catch (Exception ignored) {
                        }
                    }
                    r1.put(DataQuery.of(SEPARATOR, s1), value);
                }
                result.put(dq, r1);
            } else if (!s.equals("ItemType") && o instanceof String) {
                String n = o.toString().replace(PERIOD_REPLACEMENT, PERIOD);
                result.put(dq, n);
            } else if (o instanceof List) {
                result.put(dq, ((List<?>) o).stream().filter(Objects::nonNull).collect(Collectors.toList()));
            } else {
                result.put(dq, o);
            }
        }
        return result;
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
