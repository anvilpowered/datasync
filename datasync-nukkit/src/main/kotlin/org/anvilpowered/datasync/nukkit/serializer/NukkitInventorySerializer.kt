package org.anvilpowered.datasync.nukkit.serializer

import cn.nukkit.Player
import cn.nukkit.inventory.Inventory
import cn.nukkit.item.Item
import cn.nukkit.nbt.stream.NBTInputStream
import cn.nukkit.nbt.stream.NBTOutputStream
import cn.nukkit.nbt.tag.CompoundTag
import jetbrains.exodus.util.ByteArraySizedInputStream
import jetbrains.exodus.util.LightByteArrayOutputStream
import org.anvilpowered.datasync.api.model.snapshot.Snapshot
import org.anvilpowered.datasync.common.serializer.CommonInventorySerializer

class NukkitInventorySerializer : CommonInventorySerializer<String, Player, Inventory, Item>() {

    override fun serializeInventory(snapshot: Snapshot<*>, inventory: Inventory, maxSlots: Int): Boolean {
        var success = true
        try {
            val outputStream = LightByteArrayOutputStream()
            val nbtOut = NBTOutputStream(outputStream)
            val root = CompoundTag()
            val slot = CompoundTag()
            root.put("slot", slot)
            for (content in inventory.contents) {
                val itemNBT = CompoundTag()
                itemNBT.putCompound(content.value.name, content.value.namedTag)
                slot.putCompound(content.key.toString(), itemNBT)
            }
            CompoundTag.writeNamedTag(root, nbtOut)
        } catch (e: Exception) {
            e.printStackTrace()
            success = false
        }
        return success
    }

    override fun deserializeInventory(snapshot: Snapshot<*>, inventory: Inventory, fallbackItemStackSnapshot: Item): Boolean {
        inventory.clearAll()
        val root: CompoundTag
        try {
            root = CompoundTag.readNamedTag(NBTInputStream(ByteArraySizedInputStream(snapshot.inventory))) as CompoundTag
            val slot = root.get("slot") as CompoundTag
            for (i in 0 until INVENTORY_SLOTS) {
                val item = Item(0)
                item.customBlockData = slot[i.toString()] as CompoundTag
                inventory.setItem(i, item)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }

    override fun serialize(snapshot: Snapshot<*>, user: Player): Boolean = serializeInventory(snapshot, user.inventory)
    override fun deserialize(snapshot: Snapshot<*>, user: Player): Boolean = deserializeInventory(snapshot, user.inventory)
    override fun serializeInventory(snapshot: Snapshot<*>, inventory: Inventory): Boolean = serializeInventory(snapshot, inventory)
    override fun deserializeInventory(snapshot: Snapshot<*>, inventory: Inventory)
        : Boolean = deserializeInventory(snapshot, inventory, defaultFallbackItemStackSnapshot)

    override fun getDefaultFallbackItemStackSnapshot(): Item = Item(334).setCustomName("Not an actual slot")
    override fun getExitWithoutSavingItemStackSnapshot(): Item = Item(266).setCustomName("Exit without saving")
}
