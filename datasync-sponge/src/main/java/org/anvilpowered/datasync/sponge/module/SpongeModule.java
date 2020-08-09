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

package org.anvilpowered.datasync.sponge.module;

import com.google.inject.TypeLiteral;
import jetbrains.exodus.entitystore.EntityId;
import jetbrains.exodus.entitystore.PersistentEntityStore;
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
import org.anvilpowered.datasync.common.registry.CommonConfigurationService;
import org.anvilpowered.datasync.common.serializer.user.CommonUserSerializerComponent;
import org.anvilpowered.datasync.common.snapshotoptimization.CommonSnapshotOptimizationService;
import org.anvilpowered.datasync.sponge.command.SpongeSyncCommandNode;
import org.anvilpowered.datasync.sponge.keys.SpongeDataKeyService;
import org.anvilpowered.datasync.sponge.registry.SpongeConfigurationService;
import org.anvilpowered.datasync.sponge.serializer.SpongeExperienceSerializer;
import org.anvilpowered.datasync.sponge.serializer.SpongeGameModeSerializer;
import org.anvilpowered.datasync.sponge.serializer.SpongeHealthSerializer;
import org.anvilpowered.datasync.sponge.serializer.SpongeHungerSerializer;
import org.anvilpowered.datasync.sponge.serializer.SpongeInventorySerializer;
import org.anvilpowered.datasync.sponge.serializer.SpongeSnapshotSerializer;
import org.anvilpowered.datasync.sponge.serializer.user.SpongeUserSerializerComponent;
import org.anvilpowered.datasync.sponge.snapshotoptimization.SpongeSnapshotOptimizationService;
import org.anvilpowered.datasync.sponge.task.SpongeSerializationTaskService;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;

public class SpongeModule extends CommonModule<
    Key<?>,
    Player,
    User,
    Text,
    CommandSource> {

    @Override
    protected void configure() {
        super.configure();

        bind(CommonConfigurationService.class).to(SpongeConfigurationService.class);

        bind(new TypeLiteral<ExperienceSerializer<User>>() {
        }).to(SpongeExperienceSerializer.class);
        bind(new TypeLiteral<GameModeSerializer<User>>() {
        }).to(SpongeGameModeSerializer.class);
        bind(new TypeLiteral<HealthSerializer<User>>() {
        }).to(SpongeHealthSerializer.class);
        bind(new TypeLiteral<HealthSerializer<User>>() {
        }).to(SpongeHealthSerializer.class);
        bind(new TypeLiteral<HungerSerializer<User>>() {
        }).to(SpongeHungerSerializer.class);
        bind(new TypeLiteral<InventorySerializer<User, Inventory, ItemStackSnapshot>>() {
        }).to(SpongeInventorySerializer.class);
        bind(new TypeLiteral<SnapshotSerializer<User>>() {
        }).to(SpongeSnapshotSerializer.class);
        bind(new TypeLiteral<DataKeyService<Key<?>>>() {
        }).to(SpongeDataKeyService.class);
        bind(SerializationTaskService.class).to(SpongeSerializationTaskService.class);

        bind(new TypeLiteral<CommonUserSerializerComponent<ObjectId, User, Player, Key<?>, Datastore>>() {
        }).to(new TypeLiteral<SpongeUserSerializerComponent<ObjectId, Datastore>>() {
        });

        bind(new TypeLiteral<CommonUserSerializerComponent<EntityId, User, Player, Key<?>, PersistentEntityStore>>() {
        }).to(new TypeLiteral<SpongeUserSerializerComponent<EntityId, PersistentEntityStore>>() {
        });

        bind(new TypeLiteral<CommonSnapshotOptimizationService<ObjectId, User, Player, CommandSource, Key<?>, Datastore>>() {
        }).to(new TypeLiteral<SpongeSnapshotOptimizationService<ObjectId, Datastore>>() {
        });

        bind(new TypeLiteral<CommonSnapshotOptimizationService<EntityId, User, Player, CommandSource, Key<?>, PersistentEntityStore>>() {
        }).to(new TypeLiteral<SpongeSnapshotOptimizationService<EntityId, PersistentEntityStore>>() {
        });

        bind(new TypeLiteral<CommandNode<CommandSource>>() {
        }).to(SpongeSyncCommandNode.class);
    }
}