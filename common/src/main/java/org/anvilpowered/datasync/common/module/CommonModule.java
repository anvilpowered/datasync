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

package org.anvilpowered.datasync.common.module;

import com.google.common.reflect.TypeToken;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityId;
import jetbrains.exodus.entitystore.PersistentEntityStore;
import org.anvilpowered.anvil.api.Anvil;
import org.anvilpowered.anvil.api.data.config.ConfigurationService;
import org.anvilpowered.anvil.api.data.registry.Registry;
import org.anvilpowered.anvil.api.datastore.DataStoreContext;
import org.anvilpowered.anvil.api.datastore.MongoContext;
import org.anvilpowered.anvil.api.datastore.XodusContext;
import org.anvilpowered.anvil.api.manager.annotation.MongoDBComponent;
import org.anvilpowered.anvil.api.manager.annotation.XodusComponent;
import org.anvilpowered.anvil.api.misc.BindingExtensions;
import org.anvilpowered.anvil.api.model.Mappable;
import org.anvilpowered.anvil.api.plugin.BasicPluginInfo;
import org.anvilpowered.anvil.api.plugin.PluginInfo;
import org.anvilpowered.datasync.common.plugin.DataSyncPluginInfo;
import org.anvilpowered.datasync.common.serializer.CommonExperienceSerializer;
import org.anvilpowered.datasync.common.serializer.CommonGameModeSerializer;
import org.anvilpowered.datasync.common.serializer.CommonHealthSerializer;
import org.anvilpowered.datasync.common.serializer.CommonHungerSerializer;
import org.anvilpowered.datasync.common.serializer.CommonInventorySerializer;
import org.anvilpowered.datasync.common.serializer.CommonSnapshotSerializer;
import org.anvilpowered.datasync.common.snapshot.CommonSnapshotManager;
import org.anvilpowered.datasync.common.snapshot.repository.CommonMongoSnapshotRepository;
import org.anvilpowered.datasync.common.snapshot.repository.CommonXodusSnapshotRepository;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.anvilpowered.datasync.api.keys.DataKeyService;
import org.anvilpowered.datasync.api.member.MemberManager;
import org.anvilpowered.datasync.api.member.repository.MemberRepository;
import org.anvilpowered.datasync.api.misc.SyncUtils;
import org.anvilpowered.datasync.api.model.member.Member;
import org.anvilpowered.datasync.api.model.snapshot.Snapshot;
import org.anvilpowered.datasync.api.serializer.ExperienceSerializer;
import org.anvilpowered.datasync.api.serializer.GameModeSerializer;
import org.anvilpowered.datasync.api.serializer.HealthSerializer;
import org.anvilpowered.datasync.api.serializer.HungerSerializer;
import org.anvilpowered.datasync.api.serializer.InventorySerializer;
import org.anvilpowered.datasync.api.serializer.SnapshotSerializer;
import org.anvilpowered.datasync.api.serializer.user.UserSerializerManager;
import org.anvilpowered.datasync.api.serializer.user.component.UserSerializerComponent;
import org.anvilpowered.datasync.api.snapshot.SnapshotManager;
import org.anvilpowered.datasync.api.snapshot.repository.SnapshotRepository;
import org.anvilpowered.datasync.api.snapshotoptimization.SnapshotOptimizationManager;
import org.anvilpowered.datasync.api.snapshotoptimization.component.SnapshotOptimizationService;
import org.anvilpowered.datasync.api.tasks.SerializationTaskService;
import org.anvilpowered.datasync.common.data.config.DataSyncConfigurationService;
import org.anvilpowered.datasync.common.data.registry.DataSyncRegistry;
import org.anvilpowered.datasync.common.keys.CommonDataKeyService;
import org.anvilpowered.datasync.common.member.CommonMemberManager;
import org.anvilpowered.datasync.common.member.repository.CommonMongoMemberRepository;
import org.anvilpowered.datasync.common.member.repository.CommonXodusMemberRepository;
import org.anvilpowered.datasync.common.misc.CommonSyncUtils;
import org.anvilpowered.datasync.common.serializer.user.CommonUserSerializerManager;
import org.anvilpowered.datasync.common.serializer.user.component.CommonUserSerializerComponent;
import org.anvilpowered.datasync.common.snapshotoptimization.CommonSnapshotOptimizationManager;
import org.anvilpowered.datasync.common.snapshotoptimization.component.CommonSnapshotOptimizationService;
import org.anvilpowered.datasync.common.tasks.CommonSerializationTaskService;

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

        BindingExtensions be = Anvil.getBindingExtensions(binder());

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
            new TypeToken<CommonSnapshotSerializer<Snapshot<?>, TDataKey, TUser, TPlayer, TInventory, TItemStackSnapshot>>(getClass()) {
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

        bind(ConfigurationService.class).to(DataSyncConfigurationService.class);

        bind(Registry.class).to(DataSyncRegistry.class);

        be.bind(
            new TypeToken<BasicPluginInfo>(getClass()) {
            },
            new TypeToken<DataSyncPluginInfo<TString, TCommandSource>>(getClass()) {
            }
        );

        be.bind(
            new TypeToken<PluginInfo<TString>>(getClass()) {
            },
            new TypeToken<DataSyncPluginInfo<TString, TCommandSource>>(getClass()) {
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
