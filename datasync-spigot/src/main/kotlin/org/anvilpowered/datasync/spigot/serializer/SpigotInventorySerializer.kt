/*
 * DataSync - AnvilPowered
 *   Copyright (C) 2020
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
 *     along with this program.  If not, see https://www.gnu.org/licenses/.
 */

package org.anvilpowered.datasync.spigot.serializer

import org.anvilpowered.datasync.api.model.snapshot.Snapshot
import org.anvilpowered.datasync.common.serializer.CommonInventorySerializer
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

abstract class SpigotInventorySerializer : CommonInventorySerializer<String, Player, Inventory, ItemStack>() {

    override fun getDefaultFallbackItemStackSnapshot(): ItemStack {
        val itemStack = ItemStack(Material.BARRIER, 1)
        val meta = itemStack.itemMeta
        meta!!.setDisplayName("Not an actual slot")
        itemStack.itemMeta = meta
        return itemStack
    }

    override fun getExitWithoutSavingItemStackSnapshot(): ItemStack {
        val itemStack = ItemStack(Material.GOLD_INGOT, 1)
        val meta = itemStack.itemMeta
        meta!!.setDisplayName("Exit without saving")
        itemStack.itemMeta = meta
        return itemStack
    }

    override fun serialize(snapshot: Snapshot<*>, player: Player): Boolean = serializeInventory(snapshot, player.inventory)
    override fun serializeInventory(snapshot: Snapshot<*>, inventory: Inventory): Boolean = serializeInventory(snapshot, inventory, INVENTORY_SLOTS)
    override fun deserialize(snapshot: Snapshot<*>, player: Player): Boolean = deserializeInventory(snapshot, player.inventory)
    override fun deserializeInventory(snapshot: Snapshot<*>, itemStacks: Inventory): Boolean = deserializeInventory(
        snapshot,
        itemStacks,
        defaultFallbackItemStackSnapshot
    )
}
