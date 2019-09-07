package rocks.milspecsg.msdatasync.service.implementation.data;

import com.google.common.collect.ImmutableList;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;
import rocks.milspecsg.msdatasync.model.core.SerializedItemStack;
import rocks.milspecsg.msdatasync.model.core.Snapshot;
import rocks.milspecsg.msdatasync.service.data.ApiInventorySerializer;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class ApiSpongeInventorySerializer extends ApiInventorySerializer<Snapshot, Player, Key, Inventory> {

    private static char SEPARATOR = '_';

    @Override
    public boolean serializeInventory(Snapshot snapshot, Inventory inventory) {
        try {
            List<SerializedItemStack> itemStacks = new ArrayList<>();
            for (Inventory slot : inventory.slots()) {
                SerializedItemStack serializedItemStack = new SerializedItemStack();
                ItemStack before = slot.peek().orElse(ItemStack.empty());
                DataContainer dc = before.toContainer();
//                        System.out.println("SDC: " + dc.toString());
                serializedItemStack.properties = serialize(dc.getValues(false));
                itemStacks.add(serializedItemStack);
            }
            snapshot.itemStacks = itemStacks;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;    }

    @Override
    public boolean serialize(Snapshot snapshot, Player player) {
        return serializeInventory(snapshot, player.getInventory());
    }

    @Override
    public boolean deserializeInventory(Snapshot snapshot, Inventory inventory) {
        try {
            inventory.clear();
            Iterator<Inventory> slots = inventory.slots().iterator();
            for (SerializedItemStack stack : snapshot.itemStacks) {
                if (slots.hasNext()) {
                    DataContainer dc = DataContainer.createNew(DataView.SafetyMode.ALL_DATA_CLONED);
                    deserialize(stack.properties).forEach(dc::set);
                    //System.out.println("DDC: " + dc.toString());
                    ItemStack is = ItemStack.builder().fromContainer(dc).build();
                    slots.next().offer(is);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;    }

    @Override
    public boolean deserialize(Snapshot snapshot, Player player) {
      return deserializeInventory(snapshot, player.getInventory());
    }

    private static Map<String, Object> serialize(Map<DataQuery, Object> values) {
        Map<String, Object> result = new HashMap<>();
        values.forEach((dq, o) -> {
            String s = dq.asString(SEPARATOR);
//            System.out.println("Class for " + s + ": " + o.getClass().getCanonicalName());
            if (o instanceof Map) {
                Object m = serialize((Map<DataQuery, Object>) o);
//                System.out.println("Finished (M), S: " + s + ", M: " + m.toString());
                result.put(s, m);
            } else if (o instanceof List) {
                List<?> list = (List<?>) o;
                List<Object> r1 = new ArrayList<>();
                list.forEach(li -> {
                    if (li instanceof DataContainer) {
                        r1.add(serialize(((DataContainer) li).getValues(false)));
                    } else if (li instanceof String) {
                        r1.add(li);
                    }
                });
                result.put(s, r1);
            } else {
//                System.out.println("Not going deeper, S: " + s + ", O: " + o.toString());
                result.put(s, o);
            }
        });
        return result;
    }

    private static Map<DataQuery, Object> deserialize(Map<String, Object> values) {
        Map<DataQuery, Object> result = new HashMap<>();
        values.forEach((s, o) -> {
            DataQuery dq = DataQuery.of(SEPARATOR, s);
            if (o instanceof Map) {
                Map<String, Object> m = (Map<String, Object>) o;
                Map<DataQuery, Object> r1 = new HashMap<>();
                m.forEach((s1, m1) -> {
                    Object value = m1;
                    try {
                        Map<DataQuery, Object> v = deserialize((Map<String, Object>) m1);
                        DataContainer dc = DataContainer.createNew(DataView.SafetyMode.ALL_DATA_CLONED);
                        v.forEach(dc::set);
                        if (s1.equals("ench")) {
                            value = ImmutableList.of(dc);
                        } else {
                            value = dc;
                        }
                    } catch (ClassCastException ignored) {
                    }
                    r1.put(DataQuery.of(SEPARATOR, s1), value);
                });
                result.put(dq, r1);
            }
//            else if (o instanceof List) {
//                List<String> list = (List<String>) o;
//
//                List<String> r1 = new ArrayList<>(list.size());
//                r1.addAll(list);
//                result.put(dq, r1);
//            }
            else if (!s.equals("ItemType") && o instanceof String) {
                String n = o.toString();
//                System.out.println(("This is a string: " + n));
                result.put(dq, n);
                //result.put(dq, TextSerializers.LEGACY_FORMATTING_CODE.deserializeUnchecked(n));
            } else {
                result.put(dq, o);
            }
        });
        return result;
    }
}
