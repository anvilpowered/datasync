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
import org.anvilpowered.datasync.api.model.member.MappableMember;
import org.anvilpowered.datasync.api.model.member.Member;
import org.anvilpowered.datasync.api.model.snapshot.MappableSnapshot;
import org.anvilpowered.datasync.api.model.snapshot.Snapshot;
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
    Member<ObjectId>,
    MappableMember<EntityId, Entity>,
    Snapshot<ObjectId>,
    MappableSnapshot<EntityId, Entity>,
    Key<?>,
    Player,
    User,
    Text,
    Inventory,
    ItemStackSnapshot,
    CommandSource> {

    @Override
    protected void configure() {
        super.configure();

        BindingExtensions be = Anvil.getBindingExtensions(binder());

        bind(CommonConfigurationService.class).to(SpongeConfigurationService.class);

        bind(
            (TypeLiteral<CommonExperienceSerializer<Snapshot<?>, Key<?>, User>>) TypeLiteral.get(new TypeToken<CommonExperienceSerializer<Snapshot<?>, Key<?>, User>>(getClass()) {
            }.getType())
        ).to(
            (TypeLiteral<SpongeExperienceSerializer>) TypeLiteral.get(new TypeToken<SpongeExperienceSerializer>(getClass()) {
            }.getType())
        );

        bind(
            (TypeLiteral<CommonGameModeSerializer<Snapshot<?>, Key<?>, User>>) TypeLiteral.get(new TypeToken<CommonGameModeSerializer<Snapshot<?>, Key<?>, User>>(getClass()) {
            }.getType())
        ).to(
            (TypeLiteral<SpongeGameModeSerializer>) TypeLiteral.get(new TypeToken<SpongeGameModeSerializer>(getClass()) {
            }.getType())
        );

        bind(
            (TypeLiteral<CommonHealthSerializer<Snapshot<?>, Key<?>, User>>) TypeLiteral.get(new TypeToken<CommonHealthSerializer<Snapshot<?>, Key<?>, User>>(getClass()) {
            }.getType())
        ).to(
            (TypeLiteral<SpongeHealthSerializer>) TypeLiteral.get(new TypeToken<SpongeHealthSerializer>(getClass()) {
            }.getType())
        );

        bind(
            (TypeLiteral<CommonHungerSerializer<Snapshot<?>, Key<?>, User>>) TypeLiteral.get(new TypeToken<CommonHungerSerializer<Snapshot<?>, Key<?>, User>>(getClass()) {
            }.getType())
        ).to(
            (TypeLiteral<SpongeHungerSerializer>) TypeLiteral.get(new TypeToken<SpongeHungerSerializer>(getClass()) {
            }.getType())
        );

        bind(
            (TypeLiteral<CommonInventorySerializer<Snapshot<?>, Key<?>, User, Inventory, ItemStackSnapshot>>) TypeLiteral.get(new TypeToken<CommonInventorySerializer<Snapshot<?>, Key<?>, User, Inventory, ItemStackSnapshot>>(getClass()) {
            }.getType())
        ).to(
            (TypeLiteral<SpongeInventorySerializer>) TypeLiteral.get(new TypeToken<SpongeInventorySerializer>(getClass()) {
            }.getType())
        );

        bind(
            (TypeLiteral<CommonSnapshotSerializer<Snapshot<?>, Key<?>, User, Player, Inventory, ItemStackSnapshot>>) TypeLiteral.get(new TypeToken<CommonSnapshotSerializer<Snapshot<?>, Key<?>, User, Player, Inventory, ItemStackSnapshot>>(getClass()) {
            }.getType())
        ).to(
            (TypeLiteral<SpongeSnapshotSerializer>) TypeLiteral.get(new TypeToken<SpongeSnapshotSerializer>(getClass()) {
            }.getType())
        );

        bind(
            new TypeLiteral<CommonDataKeyService<Key<?>>>() {
            }
        ).to(
            new TypeLiteral<CommonSpongeDataKeyService>() {
            }
        );

        be.bind(
            new TypeToken<CommonSerializationTaskService<User, Text, CommandSource>>(getClass()) {
            },
            new TypeToken<SpongeSerializationTaskService>(getClass()) {
            }
        );

        be.bind(
            new TypeToken<CommonUserSerializerComponent<ObjectId, Member<ObjectId>, Snapshot<ObjectId>, User, Player, Key<?>, Datastore>>(getClass()) {
            },
            new TypeToken<SpongeUserSerializerComponent<ObjectId, Member<ObjectId>, Snapshot<ObjectId>, Datastore>>(getClass()) {
            }
        );

        be.bind(
            new TypeToken<CommonUserSerializerComponent<EntityId, MappableMember<EntityId, Entity>, MappableSnapshot<EntityId, Entity>, User, Player, Key<?>, PersistentEntityStore>>(getClass()) {
            },
            new TypeToken<SpongeUserSerializerComponent<EntityId, MappableMember<EntityId, Entity>, MappableSnapshot<EntityId, Entity>, PersistentEntityStore>>(getClass()) {
            }
        );

        be.bind(
            new TypeToken<CommonSnapshotOptimizationService<ObjectId, Member<ObjectId>, Snapshot<ObjectId>, Player, User, CommandSource, Key<?>, Datastore>>(getClass()) {
            },
            new TypeToken<SpongeSnapshotOptimizationService<ObjectId, Member<ObjectId>, Snapshot<ObjectId>, Datastore>>(getClass()) {
            }
        );

        be.bind(
            new TypeToken<CommonSnapshotOptimizationService<EntityId, MappableMember<EntityId, Entity>, MappableSnapshot<EntityId, Entity>, Player, User, CommandSource, Key<?>, PersistentEntityStore>>(getClass()) {
            },
            new TypeToken<SpongeSnapshotOptimizationService<EntityId, MappableMember<EntityId, Entity>, MappableSnapshot<EntityId, Entity>, PersistentEntityStore>>(getClass()) {
            }
        );
    }
}