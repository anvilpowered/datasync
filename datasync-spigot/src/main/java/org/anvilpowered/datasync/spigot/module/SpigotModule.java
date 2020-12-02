package org.anvilpowered.datasync.spigot.module;

import com.google.inject.TypeLiteral;
import dev.morphia.Datastore;
import jetbrains.exodus.entitystore.EntityId;
import jetbrains.exodus.entitystore.PersistentEntityStore;
import net.md_5.bungee.api.chat.TextComponent;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.anvilpowered.anvil.api.command.CommandNode;
import org.anvilpowered.datasync.api.key.DataKeyService;
import org.anvilpowered.datasync.api.serializer.ExperienceSerializer;
import org.anvilpowered.datasync.api.serializer.GameModeSerializer;
import org.anvilpowered.datasync.api.serializer.HealthSerializer;
import org.anvilpowered.datasync.api.serializer.HungerSerializer;
import org.anvilpowered.datasync.api.serializer.InventorySerializer;
import org.anvilpowered.datasync.api.serializer.SnapshotSerializer;
import org.anvilpowered.datasync.api.task.SerializationTaskService;
import org.anvilpowered.datasync.common.module.CommonModule;
import org.anvilpowered.datasync.common.plugin.DataSyncPluginInfo;
import org.anvilpowered.datasync.common.serializer.user.CommonUserSerializerComponent;
import org.anvilpowered.datasync.common.snapshotoptimization.CommonSnapshotOptimizationService;
import org.anvilpowered.datasync.spigot.command.SpigotSyncCommandNode;
import org.anvilpowered.datasync.spigot.keys.SpigotDataKeyService;
import org.anvilpowered.datasync.spigot.serializer.SpigotExperienceSerializer;
import org.anvilpowered.datasync.spigot.serializer.SpigotGameModeSerializer;
import org.anvilpowered.datasync.spigot.serializer.SpigotHealthSerializer;
import org.anvilpowered.datasync.spigot.serializer.SpigotHungerSerializer;
import org.anvilpowered.datasync.spigot.serializer.SpigotInventorySerializer;
import org.anvilpowered.datasync.spigot.serializer.SpigotSnapshotSerializer;
import org.anvilpowered.datasync.spigot.serializer.user.SpigotUserSerializerComponent;
import org.anvilpowered.datasync.spigot.snapshotoptimization.SpigotSnapshotOptimizationService;
import org.anvilpowered.datasync.spigot.task.SpigotSerializationTaskService;
import org.bson.types.ObjectId;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.nio.file.Paths;

public class SpigotModule extends CommonModule<
    String,
    Player,
    Player,
    TextComponent,
    CommandSender> {

    @Override
    protected void configure() {
        super.configure();

        File configFilesLocation = Paths.get("plugins/" + DataSyncPluginInfo.id).toFile();
        if (!configFilesLocation.exists()) {
            if (!configFilesLocation.mkdirs()) {
                throw new IllegalStateException("Unable to create config directory");
            }
        }

        bind(new TypeLiteral<ConfigurationLoader<CommentedConfigurationNode>>(){
        }).toInstance(HoconConfigurationLoader.builder().setPath(Paths.get(configFilesLocation + "/datasync.conf")).build());

        bind(new TypeLiteral<ExperienceSerializer<Player>>() {
        }).to(SpigotExperienceSerializer.class);
        bind(new TypeLiteral<GameModeSerializer<Player>>() {
        }).to(SpigotGameModeSerializer.class);
        bind(new TypeLiteral<HealthSerializer<Player>>() {
        }).to(SpigotHealthSerializer.class);
        bind(new TypeLiteral<HungerSerializer<Player>>() {
        }).to(SpigotHungerSerializer.class);
        bind(new TypeLiteral<InventorySerializer<Player, Inventory, ItemStack>>() {
        }).to(SpigotInventorySerializer.class);
        bind(new TypeLiteral<SnapshotSerializer<Player>>() {
        }).to(SpigotSnapshotSerializer.class);
        bind(new TypeLiteral<DataKeyService<String>>() {
        }).to(SpigotDataKeyService.class);
        bind(SerializationTaskService.class).to(SpigotSerializationTaskService.class);

        bind(new TypeLiteral<CommonUserSerializerComponent<ObjectId, Player, Player, String, Datastore>>() {
        }).to(new TypeLiteral<SpigotUserSerializerComponent<ObjectId, Datastore>>() {
        });

        bind(new TypeLiteral<CommonUserSerializerComponent<EntityId, Player, Player, String, PersistentEntityStore>>() {
        }).to(new TypeLiteral<SpigotUserSerializerComponent<EntityId, PersistentEntityStore>>() {
        });

        bind(new TypeLiteral<CommonSnapshotOptimizationService<ObjectId, Player, Player, CommandSender, String, Datastore>>() {
        }).to(new TypeLiteral<SpigotSnapshotOptimizationService<ObjectId, Datastore>>() {
        });

        bind(new TypeLiteral<CommonSnapshotOptimizationService<EntityId, Player, Player, CommandSender, String, PersistentEntityStore>>() {
        }).to(new TypeLiteral<SpigotSnapshotOptimizationService<EntityId, PersistentEntityStore>>() {
        });

        bind(new TypeLiteral<CommandNode<CommandSender>>(){
        }).to(SpigotSyncCommandNode.class);
    }
}
