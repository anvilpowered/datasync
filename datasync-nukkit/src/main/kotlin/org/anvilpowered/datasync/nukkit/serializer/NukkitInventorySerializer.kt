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

package org.anvilpowered.datasync.nukkit.serializer

import cn.nukkit.Player
import cn.nukkit.inventory.Inventory
import cn.nukkit.item.Item
import cn.nukkit.nbt.NBTIO
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
            val contents: Map<Int, Item> = inventory.contents
            root.put("slot", slot)
            for (i in contents.keys.iterator()) {
                if (contents[i] == null) {
                    continue
                }
                slot.put(i.toString(), NBTIO.putItemHelper(contents[i], i))
            }
            CompoundTag.writeNamedTag(root, nbtOut)
            snapshot.setInventory(outputStream.toByteArray())
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
            if (snapshot.inventory == null) {
                return false
            }
            root = CompoundTag.readNamedTag(NBTInputStream(ByteArraySizedInputStream(snapshot.inventory))) as CompoundTag
            val slot = root.get("slot") as CompoundTag
            for (i in 0 until INVENTORY_SLOTS) {
                if (slot[i.toString()] == null) {
                    continue
                }
                inventory.setItem(i, NBTIO.getItemHelper(slot.get(i.toString()) as CompoundTag))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }


    override fun serialize(snapshot: Snapshot<*>, user: Player): Boolean = serializeInventory(snapshot, user.inventory)
    override fun deserialize(snapshot: Snapshot<*>, user: Player): Boolean = deserializeInventory(snapshot, user.inventory)
    override fun serializeInventory(snapshot: Snapshot<*>, inventory: Inventory): Boolean = serializeInventory(snapshot,
        inventory, INVENTORY_SLOTS)

    override fun deserializeInventory(snapshot: Snapshot<*>, inventory: Inventory)
        : Boolean = deserializeInventory(snapshot, inventory, defaultFallbackItemStackSnapshot)

    override fun getDefaultFallbackItemStackSnapshot(): Item = Item(334).setCustomName("Not an actual slot")
    override fun getExitWithoutSavingItemStackSnapshot(): Item = Item(266).setCustomName("Exit without saving")
}
