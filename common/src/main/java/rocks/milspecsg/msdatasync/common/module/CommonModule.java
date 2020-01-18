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

package rocks.milspecsg.msdatasync.common.module;

import com.google.common.reflect.TypeToken;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityId;
import jetbrains.exodus.entitystore.PersistentEntityStore;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import rocks.milspecsg.msdatasync.api.keys.DataKeyService;
import rocks.milspecsg.msdatasync.api.member.MemberManager;
import rocks.milspecsg.msdatasync.api.member.repository.MemberRepository;
import rocks.milspecsg.msdatasync.api.misc.SyncUtils;
import rocks.milspecsg.msdatasync.api.serializer.ExperienceSerializer;
import rocks.milspecsg.msdatasync.api.serializer.GameModeSerializer;
import rocks.milspecsg.msdatasync.api.serializer.HealthSerializer;
import rocks.milspecsg.msdatasync.api.serializer.HungerSerializer;
import rocks.milspecsg.msdatasync.api.serializer.InventorySerializer;
import rocks.milspecsg.msdatasync.api.serializer.SnapshotSerializer;
import rocks.milspecsg.msdatasync.api.serializer.user.UserSerializerManager;
import rocks.milspecsg.msdatasync.api.serializer.user.component.UserSerializerComponent;
import rocks.milspecsg.msdatasync.api.snapshot.SnapshotManager;
import rocks.milspecsg.msdatasync.api.snapshot.repository.SnapshotRepository;
import rocks.milspecsg.msdatasync.api.snapshotoptimization.SnapshotOptimizationManager;
import rocks.milspecsg.msdatasync.api.snapshotoptimization.component.SnapshotOptimizationService;
import rocks.milspecsg.msdatasync.api.tasks.SerializationTaskService;
import rocks.milspecsg.msdatasync.api.model.member.Member;
import rocks.milspecsg.msdatasync.api.model.snapshot.Snapshot;
import rocks.milspecsg.msdatasync.common.data.config.MSDataSyncConfigurationService;
import rocks.milspecsg.msdatasync.common.data.registry.MSDataSyncRegistry;
import rocks.milspecsg.msdatasync.common.keys.CommonDataKeyService;
import rocks.milspecsg.msdatasync.common.member.CommonMemberManager;
import rocks.milspecsg.msdatasync.common.member.repository.CommonMongoMemberRepository;
import rocks.milspecsg.msdatasync.common.member.repository.CommonXodusMemberRepository;
import rocks.milspecsg.msdatasync.common.misc.CommonSyncUtils;
import rocks.milspecsg.msdatasync.common.plugin.MSDataSyncPluginInfo;
import rocks.milspecsg.msdatasync.common.serializer.CommonExperienceSerializer;
import rocks.milspecsg.msdatasync.common.serializer.CommonGameModeSerializer;
import rocks.milspecsg.msdatasync.common.serializer.CommonHealthSerializer;
import rocks.milspecsg.msdatasync.common.serializer.CommonHungerSerializer;
import rocks.milspecsg.msdatasync.common.serializer.CommonInventorySerializer;
import rocks.milspecsg.msdatasync.common.serializer.CommonSnapshotSerializer;
import rocks.milspecsg.msdatasync.common.serializer.user.CommonUserSerializerManager;
import rocks.milspecsg.msdatasync.common.serializer.user.component.CommonUserSerializerComponent;
import rocks.milspecsg.msdatasync.common.snapshot.CommonSnapshotManager;
import rocks.milspecsg.msdatasync.common.snapshot.repository.CommonMongoSnapshotRepository;
import rocks.milspecsg.msdatasync.common.snapshot.repository.CommonXodusSnapshotRepository;
import rocks.milspecsg.msdatasync.common.snapshotoptimization.CommonSnapshotOptimizationManager;
import rocks.milspecsg.msdatasync.common.snapshotoptimization.component.CommonSnapshotOptimizationService;
import rocks.milspecsg.msdatasync.common.tasks.CommonSerializationTaskService;
import rocks.milspecsg.msrepository.api.data.config.ConfigurationService;
import rocks.milspecsg.msrepository.api.data.registry.Registry;
import rocks.milspecsg.msrepository.api.misc.BindingExtensions;
import rocks.milspecsg.msrepository.api.util.BasicPluginInfo;
import rocks.milspecsg.msrepository.api.util.PluginInfo;
import rocks.milspecsg.msrepository.common.misc.CommonBindingExtensions;
import rocks.milspecsg.msrepository.api.manager.annotation.MongoDBComponent;
import rocks.milspecsg.msrepository.api.manager.annotation.XodusComponent;
import rocks.milspecsg.msrepository.api.datastore.DataStoreContext;
import rocks.milspecsg.msrepository.api.datastore.MongoContext;
import rocks.milspecsg.msrepository.api.datastore.XodusContext;
import rocks.milspecsg.msrepository.api.model.Mappable;

@SuppressWarnings({"unchecked", "UnstableApiUsage"})
public class CommonModule<
    TMongoMember extends Member<ObjectId>,
    TXodusMember extends Member<EntityId> & Mappable<Entity>,
    TMongoSnapshot extends Snapshot<ObjectId>,
    TXodusSnapshot extends Snapshot<EntityId> & Mappable<Entity>,
    TDataKey,
    TPlayer extends TCommandSource,
    TUser,
    TString,
    TInventory,
    TItemStackSnapshot,
    TCommandSource>
    extends AbstractModule {

    @Override
    protected void configure() {

        BindingExtensions be = new CommonBindingExtensions(binder());

        be.bind(
            new TypeToken<ExperienceSerializer<Snapshot<?>, TUser>>(getClass()) {
            },
            new TypeToken<CommonExperienceSerializer<Snapshot<?>, TDataKey, TUser>>(getClass()) {
            }
        );

        be.bind(
            new TypeToken<GameModeSerializer<Snapshot<?>, TUser>>(getClass()) {
            },
            new TypeToken<CommonGameModeSerializer<Snapshot<?>, TDataKey, TUser>>(getClass()) {
            }
        );

        be.bind(
            new TypeToken<HealthSerializer<Snapshot<?>, TUser>>(getClass()) {
            },
            new TypeToken<CommonHealthSerializer<Snapshot<?>, TDataKey, TUser>>(getClass()) {
            }
        );

        be.bind(
            new TypeToken<HungerSerializer<Snapshot<?>, TUser>>(getClass()) {
            },
            new TypeToken<CommonHungerSerializer<Snapshot<?>, TDataKey, TUser>>(getClass()) {
            }
        );

        be.bind(
            new TypeToken<InventorySerializer<Snapshot<?>, TUser, TInventory, TItemStackSnapshot>>(getClass()) {
            },
            new TypeToken<CommonInventorySerializer<Snapshot<?>, TDataKey, TUser, TInventory, TItemStackSnapshot>>(getClass()) {
            }
        );

        be.bind(
            new TypeToken<SnapshotSerializer<Snapshot<?>, TUser>>(getClass()) {
            },
            new TypeToken<CommonSnapshotSerializer<Snapshot<?>, TDataKey, TUser, TInventory, TItemStackSnapshot>>(getClass()) {
            }
        );

        be.bind(
            new TypeToken<DataKeyService<TDataKey>>(getClass()) {
            },
            new TypeToken<CommonDataKeyService<TDataKey>>(getClass()) {
            }
        );

        be.bind(
            new TypeToken<SerializationTaskService>(getClass()) {
            },
            new TypeToken<CommonSerializationTaskService<TUser, TString, TCommandSource>>(getClass()) {
            }
        );

        bind(SyncUtils.class).to(CommonSyncUtils.class);

        bind(ConfigurationService.class).to(MSDataSyncConfigurationService.class);

        bind(Registry.class).to(MSDataSyncRegistry.class);

        be.bind(
            new TypeToken<BasicPluginInfo>(getClass()) {
            },
            new TypeToken<MSDataSyncPluginInfo<TString, TCommandSource>>(getClass()) {
            }
        );

        be.bind(
            new TypeToken<PluginInfo<TString>>(getClass()) {
            },
            new TypeToken<MSDataSyncPluginInfo<TString, TCommandSource>>(getClass()) {
            }
        );

        be.bind(
            new TypeToken<UserSerializerComponent<?, ?, ?, ?>>(getClass()) {
            },
            new TypeToken<UserSerializerComponent<?, Snapshot<?>, TUser, ?>>(getClass()) {
            },
            new TypeToken<UserSerializerComponent<ObjectId, TMongoSnapshot, TUser, Datastore>>(getClass()) {
            },
            new TypeToken<CommonUserSerializerComponent<ObjectId, TMongoMember, TMongoSnapshot, TUser, TPlayer, TDataKey, Datastore>>(getClass()) {
            },
            MongoDBComponent.class
        );

        be.bind(
            new TypeToken<UserSerializerComponent<?, ?, ?, ?>>(getClass()) {
            },
            new TypeToken<UserSerializerComponent<?, Snapshot<?>, TUser, ?>>(getClass()) {
            },
            new TypeToken<UserSerializerComponent<EntityId, TXodusSnapshot, TUser, PersistentEntityStore>>(getClass()) {
            },
            new TypeToken<CommonUserSerializerComponent<EntityId, TXodusMember, TXodusSnapshot, TUser, TPlayer, TDataKey, PersistentEntityStore>>(getClass()) {
            },
            XodusComponent.class
        );

        be.bind(
            new TypeToken<UserSerializerManager<Snapshot<?>, TUser, TString>>(getClass()) {
            },
            new TypeToken<CommonUserSerializerManager<Member<?>, Snapshot<?>, TUser, TPlayer, TString, TCommandSource>>(getClass()) {
            }
        );

        be.bind(
            new TypeToken<SnapshotOptimizationService<?, ?, ?, ?>>(getClass()) {
            },
            new TypeToken<SnapshotOptimizationService<?, TUser, TCommandSource, ?>>(getClass()) {
            },
            new TypeToken<SnapshotOptimizationService<ObjectId, TUser, TCommandSource, Datastore>>(getClass()) {
            },
            new TypeToken<CommonSnapshotOptimizationService<ObjectId, TMongoMember, TMongoSnapshot, TPlayer, TUser, TCommandSource, TDataKey, Datastore>>(getClass()) {
            },
            MongoDBComponent.class
        );

        be.bind(
            new TypeToken<SnapshotOptimizationService<?, ?, ?, ?>>(getClass()) {
            },
            new TypeToken<SnapshotOptimizationService<?, TUser, TCommandSource, ?>>(getClass()) {
            },
            new TypeToken<SnapshotOptimizationService<EntityId, TUser, TCommandSource, PersistentEntityStore>>(getClass()) {
            },
            new TypeToken<CommonSnapshotOptimizationService<EntityId, TXodusMember, TXodusSnapshot, TPlayer, TUser, TCommandSource, TDataKey, PersistentEntityStore>>(getClass()) {
            },
            XodusComponent.class
        );

        be.bind(
            new TypeToken<SnapshotOptimizationManager<TUser, TString, TCommandSource>>(getClass()) {
            },
            new TypeToken<CommonSnapshotOptimizationManager<TUser, TString, TCommandSource>>(getClass()) {
            }
        );

        be.bind(
            new TypeToken<MemberRepository<?, ?, ?, ?, ?>>(getClass()) {
            },
            new TypeToken<MemberRepository<?, Member<?>, Snapshot<?>, TUser, ?>>(getClass()) {
            },
            new TypeToken<MemberRepository<ObjectId, TMongoMember, TMongoSnapshot, TUser, Datastore>>(getClass()) {
            },
            new TypeToken<CommonMongoMemberRepository<TMongoMember, TMongoSnapshot, TUser, TDataKey>>(getClass()) {
            },
            MongoDBComponent.class
        );

        be.bind(
            new TypeToken<MemberRepository<?, ?, ?, ?, ?>>(getClass()) {
            },
            new TypeToken<MemberRepository<?, Member<?>, Snapshot<?>, TUser, ?>>(getClass()) {
            },
            new TypeToken<MemberRepository<EntityId, TXodusMember, TXodusSnapshot, TUser, PersistentEntityStore>>(getClass()) {
            },
            new TypeToken<CommonXodusMemberRepository<TXodusMember, TXodusSnapshot, TUser, TDataKey>>(getClass()) {
            },
            XodusComponent.class
        );

        be.bind(
            new TypeToken<MemberManager<Member<?>, Snapshot<?>, TUser, TString>>(getClass()) {
            },
            new TypeToken<CommonMemberManager<Member<?>, Snapshot<?>, TUser, TPlayer, TString, TCommandSource>>(getClass()) {
            }
        );

        be.bind(
            new TypeToken<SnapshotRepository<?, ?, ?, ?>>(getClass()) {
            },
            new TypeToken<SnapshotRepository<?, Snapshot<?>, TDataKey, ?>>(getClass()) {
            },
            new TypeToken<SnapshotRepository<ObjectId, TMongoSnapshot, TDataKey, Datastore>>(getClass()) {
            },
            new TypeToken<CommonMongoSnapshotRepository<TMongoSnapshot, TDataKey>>(getClass()) {
            },
            MongoDBComponent.class
        );

        be.bind(
            new TypeToken<SnapshotRepository<?, ?, ?, ?>>(getClass()) {
            },
            new TypeToken<SnapshotRepository<?, Snapshot<?>, TDataKey, ?>>(getClass()) {
            },
            new TypeToken<SnapshotRepository<EntityId, TXodusSnapshot, TDataKey, PersistentEntityStore>>(getClass()) {
            },
            new TypeToken<CommonXodusSnapshotRepository<TXodusSnapshot, TDataKey>>(getClass()) {
            },
            XodusComponent.class
        );

        be.bind(
            new TypeToken<SnapshotManager<Snapshot<?>, TDataKey>>(getClass()) {
            },
            new TypeToken<CommonSnapshotManager<Snapshot<?>, TDataKey>>(getClass()) {
            }
        );

        bind(new TypeLiteral<DataStoreContext<ObjectId, Datastore>>() {
        }).to(new TypeLiteral<MongoContext>() {
        });

        bind(new TypeLiteral<DataStoreContext<EntityId, PersistentEntityStore>>() {
        }).to(new TypeLiteral<XodusContext>() {
        });
    }
}
