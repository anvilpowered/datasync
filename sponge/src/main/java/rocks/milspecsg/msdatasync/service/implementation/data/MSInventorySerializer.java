package rocks.milspecsg.msdatasync.service.implementation.data;

import com.google.common.collect.ImmutableList;
import com.mongodb.BasicDBObject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;
import rocks.milspecsg.msdatasync.MSDataSync;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.model.core.SerializedItemStack;
import rocks.milspecsg.msdatasync.service.data.ApiInventorySerializer;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class MSInventorySerializer extends ApiInventorySerializer<Member, Player, Key, User> {

    @Override
    public CompletableFuture<Boolean> serialize(Member member, Player player) {
        CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {
            try {
                // this really should not take longer than 10 seconds
                // usually less than 1
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return false;
        });
        Task.builder().execute(() -> {
            try {
                List<SerializedItemStack> itemStacks = new ArrayList<>();
                Sponge.getServer().getPlayer(player.getUniqueId()).ifPresent(p -> {
                    for (Inventory slot : p.getInventory().slots()) {
                        SerializedItemStack serializedItemStack = new SerializedItemStack();
                        Map<DataQuery, Object> values = slot.peek().orElse(ItemStack.empty()).toContainer().getValues(false);
                        serializedItemStack.properties = serialize(values);
                        itemStacks.add(serializedItemStack);
                    }
                });
                member.itemStacks = itemStacks;
            } catch (Exception e) {
                e.printStackTrace();
                future.complete(false);
                return;
            }
            future.complete(true);
        }).submit(MSDataSync.plugin);
        return future;
    }

    private static Map<String, Object> serialize(Map<DataQuery, Object> values) {
        Map<String, Object> result = new HashMap<>();
        values.forEach((dq, o) -> {
            String s = dq.asString("_");
//            System.out.println("Class for " + s + ": " + o.getClass().getCanonicalName());
            if (o instanceof Map) {
                Object m = serialize((Map<DataQuery, Object>) o);
//                System.out.println("Finished (M), S: " + s + ", M: " + m.toString());
                result.put(s, m);
            } else if (o instanceof List) { // com.google.common.collect.SingletonImmutableList
                Object dc = serialize(((List<DataContainer>) o).get(0).getValues(false));
//                System.out.println("Finished (DC), S: " + s + ", DC: " + dc.toString());
                result.put(s, dc);
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
            DataQuery dq = DataQuery.of('_', s);
            if (o instanceof Map) {
                Map<String, Map<String, Object>> m = (Map<String, Map<String, Object>>) o;
                Map<DataQuery, DataContainer> r1 = new HashMap<>();
                m.forEach((s1, m1) -> {
                    // convert each value to DataContainer
                    Map<DataQuery, Object> v = deserialize(m1);
                    DataContainer dc = DataContainer.createNew(DataView.SafetyMode.ALL_DATA_CLONED);
                    v.forEach(dc::set);
                    r1.put(DataQuery.of('_', s1), dc);
                });
                result.put(dq, r1);
            } else {
                result.put(dq, o);
            }
        });
        return result;
    }

    @Override
    public CompletableFuture<Boolean> deserialize(Member member, Player player) {
        CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {
            try {
                // this really should not take longer than 30 seconds
                // usually less than 1
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return false;
        });
        Task.builder().execute(() -> {
            try {
                Sponge.getServer().getPlayer(player.getUniqueId()).ifPresent(p -> {
                    p.getInventory().clear();
                    Iterator<Inventory> slots = p.getInventory().slots().iterator();
                    for (SerializedItemStack stack : member.itemStacks) {
                        if (slots.hasNext()) {
                            DataContainer dc = DataContainer.createNew(DataView.SafetyMode.CLONED_ON_SET);

                            deserialize(stack.properties).forEach(dc::set);

                            ItemStack is = ItemStack.builder().fromContainer(dc).build();

                            slots.next().offer(is);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                future.complete(false);
                return;
            }
            future.complete(true);
        }).submit(MSDataSync.plugin);
        return future;
    }
}
