package org.anvilpowered.datasync.nukkit.module

import cn.nukkit.Player
import cn.nukkit.command.CommandSender
import cn.nukkit.inventory.Inventory
import cn.nukkit.item.Item
import com.google.inject.TypeLiteral
import dev.morphia.Datastore
import jetbrains.exodus.entitystore.EntityId
import jetbrains.exodus.entitystore.PersistentEntityStore
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
import org.anvilpowered.datasync.nukkit.command.NukkitSyncCommandNode
import org.anvilpowered.datasync.nukkit.keys.NukkitDataKeyService
import org.anvilpowered.datasync.nukkit.serializer.NukkitExperienceSerializer
import org.anvilpowered.datasync.nukkit.serializer.NukkitGameModeSerializer
import org.anvilpowered.datasync.nukkit.serializer.NukkitHealthSerializer
import org.anvilpowered.datasync.nukkit.serializer.NukkitHungerSerializer
import org.anvilpowered.datasync.nukkit.serializer.NukkitInventorySerializer
import org.anvilpowered.datasync.nukkit.serializer.NukkitSnapshotSerializer
import org.anvilpowered.datasync.nukkit.serializer.user.NukkitUserSerializerComponent
import org.anvilpowered.datasync.nukkit.snapshotoptimization.NukkitSnapshotOptimizationService
import org.anvilpowered.datasync.nukkit.task.NukkitSerializationTaskService
import org.bson.types.ObjectId
import java.nio.file.Paths

class NukkitModule : CommonModule<String, Player, Player, String, CommandSender>() {

    override fun configure() {
        super.configure()
        val configFilesLocation = Paths.get("plugins/" + DataSyncPluginInfo.id).toFile()
        if (!configFilesLocation.exists()) {
            check(configFilesLocation.mkdirs()) { "Unable to create config directory" }
        }
        bind(object : TypeLiteral<ConfigurationLoader<CommentedConfigurationNode>>() {
        }).toInstance(HoconConfigurationLoader.builder().setPath(Paths.get("$configFilesLocation/datasync.conf")).build())
        bind(object : TypeLiteral<ExperienceSerializer<Player>>() {
        }).to(NukkitExperienceSerializer::class.java)
        bind(object : TypeLiteral<GameModeSerializer<Player>>() {
        }).to(NukkitGameModeSerializer::class.java)
        bind(object : TypeLiteral<HealthSerializer<Player>>() {
        }).to(NukkitHealthSerializer::class.java)
        bind(object : TypeLiteral<HungerSerializer<Player>>() {
        }).to(NukkitHungerSerializer::class.java)
        bind(object : TypeLiteral<InventorySerializer<Player, Inventory, Item>>() {
        }).to(NukkitInventorySerializer::class.java)
        bind(object : TypeLiteral<SnapshotSerializer<Player>>() {
        }).to(NukkitSnapshotSerializer::class.java)
        bind(object : TypeLiteral<DataKeyService<String>>() {
        }).to(NukkitDataKeyService::class.java)
        bind(SerializationTaskService::class.java).to(NukkitSerializationTaskService::class.java)
        bind(object : TypeLiteral<CommonUserSerializerComponent<ObjectId, Player, Player, String, Datastore>>() {
        }).to(object : TypeLiteral<NukkitUserSerializerComponent<ObjectId, Datastore>>() {})
        bind(object : TypeLiteral<CommonUserSerializerComponent<EntityId, Player, Player, String, PersistentEntityStore>>() {
        }).to(object : TypeLiteral<NukkitUserSerializerComponent<EntityId, PersistentEntityStore>>() {})
        bind(object : TypeLiteral<CommonSnapshotOptimizationService<ObjectId, Player, Player, CommandSender, String, Datastore>>() {
        }).to(object : TypeLiteral<NukkitSnapshotOptimizationService<ObjectId, Datastore>>() {})
        bind(object : TypeLiteral<CommonSnapshotOptimizationService<EntityId, Player, Player, CommandSender, String, PersistentEntityStore>>() {
        }).to(object : TypeLiteral<NukkitSnapshotOptimizationService<EntityId, PersistentEntityStore>>() {})
        bind(object : TypeLiteral<CommandNode<CommandSender>>(){
        }).to(NukkitSyncCommandNode::class.java)
    }
}
