/*
 *   DataSync - AnvilPowered
 *   Copyright (C) 2020 Cableguy20
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.datasync.sponge.module;

import com.google.common.reflect.TypeToken;
import com.google.inject.TypeLiteral;
import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityId;
import jetbrains.exodus.entitystore.PersistentEntityStore;
import org.anvilpowered.anvil.api.Anvil;
import org.anvilpowered.anvil.api.misc.BindingExtensions;
import org.anvilpowered.datasync.api.model.member.Member;
import org.anvilpowered.datasync.api.model.snapshot.Snapshot;
import org.anvilpowered.datasync.api.serializer.ExperienceSerializer;
import org.anvilpowered.datasync.api.serializer.GameModeSerializer;
import org.anvilpowered.datasync.api.serializer.HealthSerializer;
import org.anvilpowered.datasync.api.serializer.HungerSerializer;
import org.anvilpowered.datasync.api.serializer.InventorySerializer;
import org.anvilpowered.datasync.api.serializer.SnapshotSerializer;
import org.anvilpowered.datasync.api.serializer.user.component.UserSerializerComponent;
import org.anvilpowered.datasync.api.snapshotoptimization.component.SnapshotOptimizationService;
import org.anvilpowered.datasync.common.data.config.CommonConfigurationService;
import org.anvilpowered.datasync.common.keys.CommonDataKeyService;
import org.anvilpowered.datasync.common.module.CommonModule;
import org.anvilpowered.datasync.common.serializer.CommonExperienceSerializer;
import org.anvilpowered.datasync.common.serializer.CommonGameModeSerializer;
import org.anvilpowered.datasync.common.serializer.CommonHealthSerializer;
import org.anvilpowered.datasync.common.serializer.CommonHungerSerializer;
import org.anvilpowered.datasync.common.serializer.CommonInventorySerializer;
import org.anvilpowered.datasync.common.serializer.CommonSnapshotSerializer;
import org.anvilpowered.datasync.common.serializer.user.component.CommonUserSerializerComponent;
import org.anvilpowered.datasync.common.snapshotoptimization.component.CommonSnapshotOptimizationService;
import org.anvilpowered.datasync.common.tasks.CommonSerializationTaskService;
import org.anvilpowered.datasync.sponge.data.config.SpongeConfigurationService;
import org.anvilpowered.datasync.sponge.keys.CommonSpongeDataKeyService;
import org.anvilpowered.datasync.sponge.serializer.SpongeExperienceSerializer;
import org.anvilpowered.datasync.sponge.serializer.SpongeGameModeSerializer;
import org.anvilpowered.datasync.sponge.serializer.SpongeHealthSerializer;
import org.anvilpowered.datasync.sponge.serializer.SpongeHungerSerializer;
import org.anvilpowered.datasync.sponge.serializer.SpongeInventorySerializer;
import org.anvilpowered.datasync.sponge.serializer.SpongeSnapshotSerializer;
import org.anvilpowered.datasync.sponge.serializer.user.component.SpongeUserSerializerComponent;
import org.anvilpowered.datasync.sponge.snapshotoptimization.component.SpongeSnapshotOptimizationService;
import org.anvilpowered.datasync.sponge.tasks.SpongeSerializationTaskService;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;

@SuppressWarnings({"unchecked", "UnstableApiUsage"})
public class SpongeModule extends CommonModule<
    Key<?>,
    Player,
    User,
    Text,
    CommandSource> {

    @Override
    protected void configure() {
        super.configure();

        BindingExtensions be = Anvil.getBindingExtensions(binder());

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
        bind(new TypeLiteral<CommonDataKeyService<Key<?>>>() {
        }).to(CommonSpongeDataKeyService.class);
        bind(new TypeLiteral<CommonSerializationTaskService<User, Text, CommandSource>>() {
        }).to(SpongeSerializationTaskService.class);

        bind(new TypeLiteral<UserSerializerComponent<ObjectId, User, Datastore>>() {
        }).to(new TypeLiteral<SpongeUserSerializerComponent<ObjectId, Datastore>>() {
        });

        bind(new TypeLiteral<UserSerializerComponent<EntityId, User, PersistentEntityStore>>() {
        }).to(new TypeLiteral<SpongeUserSerializerComponent<EntityId, PersistentEntityStore>>() {
        });

        bind(new TypeLiteral<SnapshotOptimizationService<ObjectId, User, CommandSource, Datastore>>() {
        }).to(new TypeLiteral<SpongeSnapshotOptimizationService<ObjectId, Datastore>>() {
        });

        bind(new TypeLiteral<SnapshotOptimizationService<EntityId, User, CommandSource, PersistentEntityStore>>() {
        }).to(new TypeLiteral<SpongeSnapshotOptimizationService<EntityId, PersistentEntityStore>>() {
        });
    }
}