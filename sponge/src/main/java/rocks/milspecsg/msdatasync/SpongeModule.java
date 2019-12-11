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

package rocks.milspecsg.msdatasync;

import com.google.common.reflect.TypeToken;
import com.google.inject.TypeLiteral;
import io.jsondb.JsonDBOperations;
import org.bson.types.ObjectId;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteId;
import org.mongodb.morphia.Datastore;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;
import rocks.milspecsg.msdatasync.model.core.member.Member;
import rocks.milspecsg.msdatasync.model.core.snapshot.Snapshot;
import rocks.milspecsg.msdatasync.service.common.keys.CommonDataKeyService;
import rocks.milspecsg.msdatasync.service.common.serializer.*;
import rocks.milspecsg.msdatasync.service.common.serializer.user.component.CommonUserSerializerComponent;
import rocks.milspecsg.msdatasync.service.common.snapshotoptimization.component.CommonSnapshotOptimizationService;
import rocks.milspecsg.msdatasync.service.common.tasks.CommonSerializationTaskService;
import rocks.milspecsg.msdatasync.service.sponge.keys.CommonSpongeDataKeyService;
import rocks.milspecsg.msdatasync.service.sponge.serializer.*;
import rocks.milspecsg.msdatasync.service.sponge.serializer.user.component.SpongeUserSerializerComponent;
import rocks.milspecsg.msdatasync.service.sponge.snapshotoptimization.component.SpongeSnapshotOptimizationService;
import rocks.milspecsg.msdatasync.service.sponge.tasks.SpongeSerializationTaskService;
import rocks.milspecsg.msrepository.BasicPluginInfo;
import rocks.milspecsg.msrepository.BindingExtensions;
import rocks.milspecsg.msrepository.CommonBindingExtensions;
import rocks.milspecsg.msrepository.PluginInfo;
import rocks.milspecsg.msrepository.api.UserService;
import rocks.milspecsg.msrepository.api.tools.resultbuilder.StringResult;
import rocks.milspecsg.msrepository.datastore.json.JsonConfig;
import rocks.milspecsg.msrepository.datastore.mongodb.MongoConfig;
import rocks.milspecsg.msrepository.datastore.nitrite.NitriteConfig;
import rocks.milspecsg.msrepository.service.sponge.SpongeUserService;
import rocks.milspecsg.msrepository.service.sponge.tools.resultbuilder.SpongeStringResult;

import java.util.UUID;

@SuppressWarnings({"unchecked", "UnstableApiUsage"})
public class SpongeModule extends CommonModule<
    Member<UUID>,
    Member<ObjectId>,
    Member<NitriteId>,
    Snapshot<UUID>,
    Snapshot<ObjectId>,
    Snapshot<NitriteId>,
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

        bind(new TypeLiteral<UserService<User>>() {
        }).to(SpongeUserService.class);

        bind(BasicPluginInfo.class).to(MSDataSyncPluginInfo.class);

        bind(new TypeLiteral<PluginInfo<Text>>() {
        }).to(MSDataSyncPluginInfo.class);

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
            new TypeToken<CommonUserSerializerComponent<UUID, Member<UUID>, Snapshot<UUID>, User, Key<?>, JsonDBOperations, JsonConfig>>(getClass()) {
            },
            new TypeToken<SpongeUserSerializerComponent<UUID, Member<UUID>, Snapshot<UUID>, JsonDBOperations, JsonConfig>>(getClass()) {
            }
        );

        be.bind(
            new TypeToken<CommonUserSerializerComponent<ObjectId, Member<ObjectId>, Snapshot<ObjectId>, User, Key<?>, Datastore, MongoConfig>>(getClass()) {
            },
            new TypeToken<SpongeUserSerializerComponent<ObjectId, Member<ObjectId>, Snapshot<ObjectId>, Datastore, MongoConfig>>(getClass()) {
            }
        );

        be.bind(
            new TypeToken<CommonUserSerializerComponent<NitriteId, Member<NitriteId>, Snapshot<NitriteId>, User, Key<?>, Nitrite, NitriteConfig>>(getClass()) {
            },
            new TypeToken<SpongeUserSerializerComponent<NitriteId, Member<NitriteId>, Snapshot<NitriteId>, Nitrite, NitriteConfig>>(getClass()) {
            }
        );

        be.bind(
            new TypeToken<CommonSnapshotOptimizationService<UUID, Member<UUID>, Snapshot<UUID>, Player, User, CommandSource, Key<?>, JsonDBOperations, JsonConfig>>(getClass()) {
            },
            new TypeToken<SpongeSnapshotOptimizationService<UUID, Member<UUID>, Snapshot<UUID>, JsonDBOperations, JsonConfig>>(getClass()) {
            }
        );

        be.bind(
            new TypeToken<CommonSnapshotOptimizationService<ObjectId, Member<ObjectId>, Snapshot<ObjectId>, Player, User, CommandSource, Key<?>, Datastore, MongoConfig>>(getClass()) {
            },
            new TypeToken<SpongeSnapshotOptimizationService<ObjectId, Member<ObjectId>, Snapshot<ObjectId>, Datastore, MongoConfig>>(getClass()) {
            }
        );

        be.bind(
            new TypeToken<CommonSnapshotOptimizationService<NitriteId, Member<NitriteId>, Snapshot<NitriteId>, Player, User, CommandSource, Key<?>, Nitrite, NitriteConfig>>(getClass()) {
            },
            new TypeToken<SpongeSnapshotOptimizationService<NitriteId, Member<NitriteId>, Snapshot<NitriteId>, Nitrite, NitriteConfig>>(getClass()) {
            }
        );
    }
}