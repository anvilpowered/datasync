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
import com.google.inject.name.Names;
import jetbrains.exodus.entitystore.EntityId;
import jetbrains.exodus.entitystore.PersistentEntityStore;
import org.anvilpowered.anvil.api.Anvil;
import org.anvilpowered.anvil.api.data.config.ConfigurationService;
import org.anvilpowered.anvil.api.data.registry.Registry;
import org.anvilpowered.anvil.api.misc.BindingExtensions;
import org.anvilpowered.anvil.api.plugin.BasicPluginInfo;
import org.anvilpowered.anvil.api.plugin.PluginInfo;
import org.anvilpowered.datasync.api.member.MemberManager;
import org.anvilpowered.datasync.api.member.repository.MemberRepository;
import org.anvilpowered.datasync.api.misc.SyncUtils;
import org.anvilpowered.datasync.api.serializer.user.UserSerializerManager;
import org.anvilpowered.datasync.api.serializer.user.component.UserSerializerComponent;
import org.anvilpowered.datasync.api.snapshot.SnapshotManager;
import org.anvilpowered.datasync.api.snapshot.repository.SnapshotRepository;
import org.anvilpowered.datasync.api.snapshotoptimization.SnapshotOptimizationManager;
import org.anvilpowered.datasync.api.snapshotoptimization.component.SnapshotOptimizationService;
import org.anvilpowered.datasync.common.data.config.CommonConfigurationService;
import org.anvilpowered.datasync.common.data.registry.CommonRegistry;
import org.anvilpowered.datasync.common.member.CommonMemberManager;
import org.anvilpowered.datasync.common.member.repository.CommonMongoMemberRepository;
import org.anvilpowered.datasync.common.member.repository.CommonXodusMemberRepository;
import org.anvilpowered.datasync.common.misc.CommonSyncUtils;
import org.anvilpowered.datasync.common.plugin.DataSyncPluginInfo;
import org.anvilpowered.datasync.common.serializer.user.CommonUserSerializerManager;
import org.anvilpowered.datasync.common.serializer.user.component.CommonUserSerializerComponent;
import org.anvilpowered.datasync.common.snapshot.CommonSnapshotManager;
import org.anvilpowered.datasync.common.snapshot.repository.CommonMongoSnapshotRepository;
import org.anvilpowered.datasync.common.snapshot.repository.CommonXodusSnapshotRepository;
import org.anvilpowered.datasync.common.snapshotoptimization.CommonSnapshotOptimizationManager;
import org.anvilpowered.datasync.common.snapshotoptimization.component.CommonSnapshotOptimizationService;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;

@SuppressWarnings({"unchecked", "UnstableApiUsage"})
public class CommonModule<
    TDataKey,
    TPlayer,
    TUser,
    TString,
    TCommandSource>
    extends AbstractModule {

    @Override
    protected void configure() {

        BindingExtensions be = Anvil.getBindingExtensions(binder());

        bind(SyncUtils.class).to(CommonSyncUtils.class);

        bind(ConfigurationService.class).to(CommonConfigurationService.class);

        bind(Registry.class).to(CommonRegistry.class);

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
            new TypeToken<UserSerializerComponent<?, ?, ?>>(getClass()) {
            },
            new TypeToken<UserSerializerComponent<?, TUser, ?>>(getClass()) {
            },
            new TypeToken<UserSerializerComponent<ObjectId, TUser, Datastore>>(getClass()) {
            },
            new TypeToken<CommonUserSerializerComponent<ObjectId, TUser, TPlayer, TDataKey, Datastore>>(getClass()) {
            },
            Names.named("mongodb")
        );

        be.bind(
            new TypeToken<UserSerializerComponent<?, ?, ?>>(getClass()) {
            },
            new TypeToken<UserSerializerComponent<?, TUser, ?>>(getClass()) {
            },
            new TypeToken<UserSerializerComponent<EntityId, TUser, PersistentEntityStore>>(getClass()) {
            },
            new TypeToken<CommonUserSerializerComponent<EntityId, TUser, TPlayer, TDataKey, PersistentEntityStore>>(getClass()) {
            },
            Names.named("xodus")
        );

        be.bind(
            new TypeToken<UserSerializerManager<TUser, TString>>(getClass()) {
            },
            new TypeToken<CommonUserSerializerManager<TUser, TPlayer, TString, TCommandSource>>(getClass()) {
            }
        );

        be.bind(
            new TypeToken<SnapshotOptimizationService<?, ?, ?, ?>>(getClass()) {
            },
            new TypeToken<SnapshotOptimizationService<?, TUser, TCommandSource, ?>>(getClass()) {
            },
            new TypeToken<SnapshotOptimizationService<ObjectId, TUser, TCommandSource, Datastore>>(getClass()) {
            },
            new TypeToken<CommonSnapshotOptimizationService<ObjectId, TUser, TPlayer, TCommandSource, TDataKey, Datastore>>(getClass()) {
            },
            Names.named("mongodb")
        );

        be.bind(
            new TypeToken<SnapshotOptimizationService<?, ?, ?, ?>>(getClass()) {
            },
            new TypeToken<SnapshotOptimizationService<?, TUser, TCommandSource, ?>>(getClass()) {
            },
            new TypeToken<SnapshotOptimizationService<EntityId, TUser, TCommandSource, PersistentEntityStore>>(getClass()) {
            },
            new TypeToken<CommonSnapshotOptimizationService<EntityId, TUser, TPlayer, TCommandSource, TDataKey, PersistentEntityStore>>(getClass()) {
            },
            Names.named("xodus")
        );

        be.bind(
            new TypeToken<SnapshotOptimizationManager<TUser, TString, TCommandSource>>(getClass()) {
            },
            new TypeToken<CommonSnapshotOptimizationManager<TUser, TString, TCommandSource>>(getClass()) {
            }
        );

        be.bind(
            new TypeToken<MemberRepository<?, ?>>(getClass()) {
            },
            new TypeToken<MemberRepository<?, ?>>(getClass()) {
            },
            new TypeToken<MemberRepository<ObjectId,  Datastore>>(getClass()) {
            },
            new TypeToken<CommonMongoMemberRepository<TDataKey>>(getClass()) {
            },
            Names.named("mongodb")
        );

        be.bind(
            new TypeToken<MemberRepository<?, ?>>(getClass()) {
            },
            new TypeToken<MemberRepository<?, ?>>(getClass()) {
            },
            new TypeToken<MemberRepository<EntityId, PersistentEntityStore>>(getClass()) {
            },
            new TypeToken<CommonXodusMemberRepository<TDataKey>>(getClass()) {
            },
            Names.named("xodus")
        );

        be.bind(
            new TypeToken<MemberManager<TString>>(getClass()) {
            },
            new TypeToken<CommonMemberManager<TUser, TPlayer, TString, TCommandSource>>(getClass()) {
            }
        );

        be.bind(
            new TypeToken<SnapshotRepository<?, ?, ?>>(getClass()) {
            },
            new TypeToken<SnapshotRepository<?, TDataKey, ?>>(getClass()) {
            },
            new TypeToken<SnapshotRepository<ObjectId, TDataKey, Datastore>>(getClass()) {
            },
            new TypeToken<CommonMongoSnapshotRepository<TDataKey>>(getClass()) {
            },
            Names.named("mongodb")
        );

        be.bind(
            new TypeToken<SnapshotRepository<?, ?, ?>>(getClass()) {
            },
            new TypeToken<SnapshotRepository<?, TDataKey, ?>>(getClass()) {
            },
            new TypeToken<SnapshotRepository<EntityId, TDataKey, PersistentEntityStore>>(getClass()) {
            },
            new TypeToken<CommonXodusSnapshotRepository<TDataKey>>(getClass()) {
            },
            Names.named("xodus")
        );

        be.bind(
            new TypeToken<SnapshotManager<TDataKey>>(getClass()) {
            },
            new TypeToken<CommonSnapshotManager<TDataKey>>(getClass()) {
            }
        );

        be.withMongoDB();
        be.withXodus();
    }
}
