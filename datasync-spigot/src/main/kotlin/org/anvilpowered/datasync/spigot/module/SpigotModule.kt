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
package org.anvilpowered.datasync.spigot.module

import com.google.inject.TypeLiteral
import dev.morphia.Datastore
import jetbrains.exodus.entitystore.EntityId
import jetbrains.exodus.entitystore.PersistentEntityStore
import net.md_5.bungee.api.chat.TextComponent
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import ninja.leaping.configurate.loader.ConfigurationLoader
import org.anvilpowered.anvil.api.command.CommandNode
import org.anvilpowered.datasync.api.key.DataKeyService
import org.anvilpowered.datasync.api.serializer.ExperienceSerializer
import org.anvilpowered.datasync.api.serializer.GameModeSerializer
import org.anvilpowered.datasync.api.serializer.HealthSerializer
import org.anvilpowered.datasync.api.serializer.HungerSerializer
import org.anvilpowered.datasync.api.serializer.InventorySerializer
import org.anvilpowered.datasync.api.serializer.SnapshotSerializer
import org.anvilpowered.datasync.api.task.SerializationTaskService
import org.anvilpowered.datasync.common.module.CommonModule
import org.anvilpowered.datasync.common.plugin.DataSyncPluginInfo
import org.anvilpowered.datasync.common.serializer.user.CommonUserSerializerComponent
import org.anvilpowered.datasync.common.snapshotoptimization.CommonSnapshotOptimizationService
import org.anvilpowered.datasync.spigot.command.SpigotSyncCommandNode
import org.anvilpowered.datasync.spigot.keys.SpigotDataKeyService
import org.anvilpowered.datasync.spigot.serializer.SpigotExperienceSerializer
import org.anvilpowered.datasync.spigot.serializer.SpigotGameModeSerializer
import org.anvilpowered.datasync.spigot.serializer.SpigotHealthSerializer
import org.anvilpowered.datasync.spigot.serializer.SpigotHungerSerializer
import org.anvilpowered.datasync.spigot.serializer.SpigotInventorySerializer1122
import org.anvilpowered.datasync.spigot.serializer.SpigotInventorySerializer1152
import org.anvilpowered.datasync.spigot.serializer.SpigotInventorySerializer1161
import org.anvilpowered.datasync.spigot.serializer.SpigotInventorySerializer1164
import org.anvilpowered.datasync.spigot.serializer.SpigotSnapshotSerializer
import org.anvilpowered.datasync.spigot.serializer.user.SpigotUserSerializerComponent
import org.anvilpowered.datasync.spigot.snapshotoptimization.SpigotSnapshotOptimizationService
import org.anvilpowered.datasync.spigot.task.SpigotSerializationTaskService
import org.bson.types.ObjectId
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.nio.file.Paths

class SpigotModule : CommonModule<String, Player, Player, TextComponent, CommandSender>() {

    override fun configure() {
        super.configure()
        val configFilesLocation = Paths.get("plugins/" + DataSyncPluginInfo.id).toFile()
        if (!configFilesLocation.exists()) {
            check(configFilesLocation.mkdirs()) { "Unable to create config directory" }
        }

        bind(object : TypeLiteral<ConfigurationLoader<CommentedConfigurationNode>>() {
        }).toInstance(HoconConfigurationLoader.builder().setPath(Paths.get("$configFilesLocation/datasync.conf")).build())
        bind(object : TypeLiteral<ExperienceSerializer<Player>>() {
        }).to(SpigotExperienceSerializer::class.java)
        bind(object : TypeLiteral<GameModeSerializer<Player>>() {
        }).to(SpigotGameModeSerializer::class.java)
        bind(object : TypeLiteral<HealthSerializer<Player>>() {
        }).to(SpigotHealthSerializer::class.java)
        bind(object : TypeLiteral<HungerSerializer<Player>>() {
        }).to(SpigotHungerSerializer::class.java)
        when (Bukkit.getServer().version.substring(
            Bukkit.getVersion().indexOf("MC: ") + 4,
            Bukkit.getVersion().length - 1)) {
            "1.16.4" -> bind(object : TypeLiteral<InventorySerializer<Player, Inventory, ItemStack>>() {
            }).to(SpigotInventorySerializer1164::class.java)
            "1.16.1" -> bind(object : TypeLiteral<InventorySerializer<Player, Inventory, ItemStack>>() {
            }).to(SpigotInventorySerializer1161::class.java)
            "1.12.2" -> bind(object : TypeLiteral<InventorySerializer<Player, Inventory, ItemStack>>() {
            }).to(SpigotInventorySerializer1122::class.java)
            "1.15.2" -> bind(object : TypeLiteral<InventorySerializer<Player, Inventory, ItemStack>>() {

            }).to(SpigotInventorySerializer1152::class.java)
            else -> System.err.println("Could not bind the inventory serializer to your MC version!")
        }
        bind(object : TypeLiteral<SnapshotSerializer<Player>>() {
        }).to(SpigotSnapshotSerializer::class.java)
        bind(object : TypeLiteral<DataKeyService<String>>() {
        }).to(SpigotDataKeyService::class.java)
        bind(SerializationTaskService::class.java).to(SpigotSerializationTaskService::class.java)
        bind(object : TypeLiteral<CommonUserSerializerComponent<ObjectId, Player, Player, String, Datastore>>() {
        }).to(object : TypeLiteral<SpigotUserSerializerComponent<ObjectId, Datastore>>() {})
        bind(object : TypeLiteral<CommonUserSerializerComponent<EntityId, Player, Player, String, PersistentEntityStore>>() {
        }).to(object : TypeLiteral<SpigotUserSerializerComponent<EntityId, PersistentEntityStore>>() {})
        bind(object : TypeLiteral<CommonSnapshotOptimizationService<ObjectId, Player, Player, CommandSender, String, Datastore>>() {
        }) to object : TypeLiteral<SpigotSnapshotOptimizationService<ObjectId, Datastore>>() {}
        bind(object : TypeLiteral<CommonSnapshotOptimizationService<EntityId, Player, Player, CommandSender, String, PersistentEntityStore>>() {
        }) to object : TypeLiteral<SpigotSnapshotOptimizationService<EntityId, PersistentEntityStore>>() {}
        bind(object : TypeLiteral<CommandNode<CommandSender>>() {
        }).to(SpigotSyncCommandNode::class.java)
    }
}
