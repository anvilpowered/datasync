package org.anvilpowered.datasync.spigot.serializer;

import jetbrains.exodus.util.ByteArraySizedInputStream;
import jetbrains.exodus.util.LightByteArrayOutputStream;
import net.minecraft.server.v1_16_R1.NBTCompressedStreamTools;
import net.minecraft.server.v1_16_R1.NBTTagCompound;
import org.anvilpowered.datasync.api.model.snapshot.Snapshot;
import org.anvilpowered.datasync.common.serializer.CommonInventorySerializer;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.util.Objects;

public class SpigotInventorySerializer
    extends CommonInventorySerializer<String, Player, Inventory, ItemStack> {

    @Override
    public boolean serializeInventory(Snapshot<?> snapshot, Inventory inventory, int maxSlots) {
        boolean success = true;
        System.out.println("Serialization in progress....");
        try {
            LightByteArrayOutputStream outputStream = new LightByteArrayOutputStream();
            DataOutputStream dataOutput = new DataOutputStream(outputStream);
            ItemStack[] contents = inventory.getContents();
            NBTTagCompound root = new NBTTagCompound();
            NBTTagCompound slot = new NBTTagCompound();
            root.set("slot", slot);
            for (int i = 0; i < contents.length; i++) {
                if (contents[i] == null || contents[i].getType().isAir()) {
                    continue;
                }
                NBTTagCompound itemNBT = new NBTTagCompound();
                CraftItemStack.asNMSCopy(contents[i]).save(itemNBT);
                slot.set(Integer.toString(i), itemNBT);
            }
            NBTCompressedStreamTools.a(root, (DataOutput) dataOutput);
            snapshot.setInventory(outputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        }
        System.out.println("Serialization complete.");
        return success;
    }

    @Override
    public boolean serializeInventory(Snapshot<?> snapshot, Inventory inventory) {
        return serializeInventory(snapshot, inventory, INVENTORY_SLOTS);
    }

    @Override
    public boolean deserializeInventory(Snapshot<?> snapshot, Inventory inventory, ItemStack fallbackItemStack) {
        inventory.clear();
        System.out.println("Deserialization in progress..");
        NBTTagCompound root;
        try {
            root = NBTCompressedStreamTools.a(new DataInputStream(new ByteArraySizedInputStream(snapshot.getInventory())));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        try {
            NBTTagCompound slot = (NBTTagCompound) root.get("slot");
            for (int i = 0; i < INVENTORY_SLOTS; i++) {
                if (slot.get(String.valueOf(i)) == null) {
                    continue;
                }
                inventory.setItem(i,
                    CraftItemStack.asBukkitCopy(net.minecraft.server.v1_16_R1.ItemStack.a(
                        (NBTTagCompound) slot.get(String.valueOf(i)))
                    ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        System.out.println("Deserialization complete.");
        return true;
    }

    @Override
    public boolean deserializeInventory(Snapshot<?> snapshot, Inventory itemStacks) {
        return deserializeInventory(snapshot, itemStacks, getDefaultFallbackItemStackSnapshot());
    }

    @Override
    public ItemStack getDefaultFallbackItemStackSnapshot() {
        ItemStack itemStack = new ItemStack(Material.BARRIER, 1);
        Objects.requireNonNull(itemStack.getItemMeta()).setDisplayName("Not an actual slot");
        return itemStack;
    }


    @Override
    public ItemStack getExitWithoutSavingItemStackSnapshot() {
        ItemStack itemStack = new ItemStack(Material.GOLD_INGOT, 1);
        Objects.requireNonNull(itemStack.getItemMeta()).setDisplayName("Exit without saving");
        return itemStack;
    }

    @Override
    public boolean serialize(Snapshot<?> snapshot, Player player) {
        return serializeInventory(snapshot, player.getInventory());
    }

    @Override
    public boolean deserialize(Snapshot<?> snapshot, Player player) {
        return deserializeInventory(snapshot, player.getInventory());
    }
}
