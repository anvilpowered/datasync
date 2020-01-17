/*
 *     MSDataSync - MilSpecSG
 *     Copyright (C) 2019 Cableguy20
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

package rocks.milspecsg.msdatasync.sponge.module;

import com.google.common.reflect.TypeToken;
import com.google.inject.TypeLiteral;
import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityId;
import jetbrains.exodus.entitystore.PersistentEntityStore;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;
import rocks.milspecsg.msdatasync.api.model.member.MappableMember;
import rocks.milspecsg.msdatasync.api.model.member.Member;
import rocks.milspecsg.msdatasync.api.model.snapshot.MappableSnapshot;
import rocks.milspecsg.msdatasync.api.model.snapshot.Snapshot;
import rocks.milspecsg.msdatasync.common.data.config.MSDataSyncConfigurationService;
import rocks.milspecsg.msdatasync.common.keys.CommonDataKeyService;
import rocks.milspecsg.msdatasync.common.module.*;
import rocks.milspecsg.msdatasync.common.serializer.*;
import rocks.milspecsg.msdatasync.common.serializer.user.component.CommonUserSerializerComponent;
import rocks.milspecsg.msdatasync.common.snapshotoptimization.component.CommonSnapshotOptimizationService;
import rocks.milspecsg.msdatasync.common.tasks.CommonSerializationTaskService;
import rocks.milspecsg.msdatasync.sponge.data.config.MSDataSyncSpongeConfigurationService;
import rocks.milspecsg.msdatasync.sponge.keys.CommonSpongeDataKeyService;
import rocks.milspecsg.msdatasync.sponge.serializer.*;
import rocks.milspecsg.msdatasync.sponge.serializer.user.component.SpongeUserSerializerComponent;
import rocks.milspecsg.msdatasync.sponge.snapshotoptimization.component.SpongeSnapshotOptimizationService;
import rocks.milspecsg.msdatasync.sponge.tasks.SpongeSerializationTaskService;
import rocks.milspecsg.msrepository.api.misc.BindingExtensions;
import rocks.milspecsg.msrepository.common.misc.CommonBindingExtensions;
import rocks.milspecsg.msrepository.api.util.UserService;
import rocks.milspecsg.msrepository.api.util.StringResult;
import rocks.milspecsg.msrepository.sponge.util.SpongeUserService;
import rocks.milspecsg.msrepository.sponge.util.SpongeStringResult;

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

        BindingExtensions be = new CommonBindingExtensions(binder());

        bind(MSDataSyncConfigurationService.class).to(MSDataSyncSpongeConfigurationService.class);

        bind(new TypeLiteral<UserService<User, Player>>() {
        }).to(SpongeUserService.class);

        bind(new TypeLiteral<StringResult<Text, CommandSource>>() {
        }).to(new TypeLiteral<SpongeStringResult>() {
        });

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
            (TypeLiteral<CommonSnapshotSerializer<Snapshot<?>, Key<?>, User, Inventory, ItemStackSnapshot>>) TypeLiteral.get(new TypeToken<CommonSnapshotSerializer<Snapshot<?>, Key<?>, User, Inventory, ItemStackSnapshot>>(getClass()) {
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