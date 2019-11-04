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
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import rocks.milspecsg.msdatasync.api.config.ConfigKeys;
import rocks.milspecsg.msdatasync.api.member.MemberManager;
import rocks.milspecsg.msdatasync.api.member.repository.MemberRepository;
import rocks.milspecsg.msdatasync.api.serializer.*;
import rocks.milspecsg.msdatasync.api.keys.DataKeyService;
import rocks.milspecsg.msdatasync.api.misc.DateFormatService;
import rocks.milspecsg.msdatasync.api.misc.SyncUtils;
import rocks.milspecsg.msdatasync.api.serializer.ExperienceSerializer;
import rocks.milspecsg.msdatasync.api.serializer.user.UserSerializerManager;
import rocks.milspecsg.msdatasync.api.serializer.user.component.UserSerializerComponent;
import rocks.milspecsg.msdatasync.api.snapshot.SnapshotManager;
import rocks.milspecsg.msdatasync.api.snapshot.repository.SnapshotRepository;
import rocks.milspecsg.msdatasync.api.snapshotoptimization.SnapshotOptimizationManager;
import rocks.milspecsg.msdatasync.api.snapshotoptimization.component.SnapshotOptimizationService;
import rocks.milspecsg.msdatasync.api.tasks.SerializationTaskService;
import rocks.milspecsg.msdatasync.model.core.member.Member;
import rocks.milspecsg.msdatasync.model.core.snapshot.Snapshot;
import rocks.milspecsg.msdatasync.service.common.member.CommonMemberManager;
import rocks.milspecsg.msdatasync.service.common.member.repository.CommonMongoMemberRepository;
import rocks.milspecsg.msdatasync.service.common.serializer.*;
import rocks.milspecsg.msdatasync.service.common.serializer.user.CommonUserSerializerManager;
import rocks.milspecsg.msdatasync.service.common.serializer.user.component.CommonUserSerializerComponent;
import rocks.milspecsg.msdatasync.service.common.snapshot.CommonSnapshotManager;
import rocks.milspecsg.msdatasync.service.common.snapshot.repository.CommonMongoSnapshotRepository;
import rocks.milspecsg.msdatasync.service.common.snapshotoptimization.CommonSnapshotOptimizationManager;
import rocks.milspecsg.msdatasync.service.common.snapshotoptimization.component.CommonSnapshotOptimizationService;
import rocks.milspecsg.msdatasync.service.common.keys.CommonDataKeyService;
import rocks.milspecsg.msdatasync.service.common.misc.CommonDateFormatService;
import rocks.milspecsg.msdatasync.service.common.misc.CommonSyncUtils;
import rocks.milspecsg.msdatasync.service.common.tasks.CommonSerializationTaskService;
import rocks.milspecsg.msrepository.BindingExtensions;
import rocks.milspecsg.msrepository.CommonBindingExtensions;
import rocks.milspecsg.msrepository.api.manager.annotation.MongoDBComponent;
import rocks.milspecsg.msrepository.datastore.DataStoreContext;
import rocks.milspecsg.msrepository.datastore.mongodb.MongoConfig;
import rocks.milspecsg.msrepository.datastore.mongodb.MongoContext;

@SuppressWarnings({"unchecked", "UnstableApiUsage"})
public class CommonModule<
    TMongoMember extends Member<ObjectId>,
    TMongoSnapshot extends Snapshot<ObjectId>,
    TDataKey,
    TPlayer extends TUser,
    TUser,
    TString,
    TInventory,
    TItemStackSnapshot,
    TCommandSource>
    extends AbstractModule {

    @Override
    protected void configure() {

        BindingExtensions be = new CommonBindingExtensions(binder());

        bind(
            (TypeLiteral<ExperienceSerializer<Snapshot<?>, TUser>>) TypeLiteral.get(new TypeToken<ExperienceSerializer<Snapshot<?>, TUser>>(getClass()) {
            }.getType())
        ).to(
            (TypeLiteral<CommonExperienceSerializer<Snapshot<?>, TDataKey, TUser>>) TypeLiteral.get(new TypeToken<CommonExperienceSerializer<Snapshot<?>, TDataKey, TUser>>(getClass()) {
            }.getType())
        );

        bind(
            (TypeLiteral<GameModeSerializer<Snapshot<?>, TUser>>) TypeLiteral.get(new TypeToken<GameModeSerializer<Snapshot<?>, TUser>>(getClass()) {
            }.getType())
        ).to(
            (TypeLiteral<CommonGameModeSerializer<Snapshot<?>, TDataKey, TUser>>) TypeLiteral.get(new TypeToken<CommonGameModeSerializer<Snapshot<?>, TDataKey, TUser>>(getClass()) {
            }.getType())
        );

        bind(
            (TypeLiteral<HealthSerializer<Snapshot<?>, TUser>>) TypeLiteral.get(new TypeToken<HealthSerializer<Snapshot<?>, TUser>>(getClass()) {
            }.getType())
        ).to(
            (TypeLiteral<CommonHealthSerializer<Snapshot<?>, TDataKey, TUser>>) TypeLiteral.get(new TypeToken<CommonHealthSerializer<Snapshot<?>, TDataKey, TUser>>(getClass()) {
            }.getType())
        );

        bind(
            (TypeLiteral<HungerSerializer<Snapshot<?>, TUser>>) TypeLiteral.get(new TypeToken<HungerSerializer<Snapshot<?>, TUser>>(getClass()) {
            }.getType())
        ).to(
            (TypeLiteral<CommonHungerSerializer<Snapshot<?>, TDataKey, TUser>>) TypeLiteral.get(new TypeToken<CommonHungerSerializer<Snapshot<?>, TDataKey, TUser>>(getClass()) {
            }.getType())
        );

        bind(
            (TypeLiteral<InventorySerializer<Snapshot<?>, TUser, TInventory, TItemStackSnapshot>>) TypeLiteral.get(new TypeToken<InventorySerializer<Snapshot<?>, TUser, TInventory, TItemStackSnapshot>>(getClass()) {
            }.getType())
        ).to(
            (TypeLiteral<CommonInventorySerializer<Snapshot<?>, TDataKey, TUser, TInventory, TItemStackSnapshot>>) TypeLiteral.get(new TypeToken<CommonInventorySerializer<Snapshot<?>, TDataKey, TUser, TInventory, TItemStackSnapshot>>(getClass()) {
            }.getType())
        );


        bind(
            (TypeLiteral<SnapshotSerializer<Snapshot<?>, TUser>>) TypeLiteral.get(new TypeToken<SnapshotSerializer<Snapshot<?>, TUser>>(getClass()) {
            }.getType())
        ).to(
            (TypeLiteral<CommonSnapshotSerializer<Snapshot<?>, TDataKey, TUser, TInventory, TItemStackSnapshot>>) TypeLiteral.get(new TypeToken<CommonSnapshotSerializer<Snapshot<?>, TDataKey, TUser, TInventory, TItemStackSnapshot>>(getClass()) {
            }.getType())
        );

        bind(
            (TypeLiteral<DataKeyService<TDataKey>>) TypeLiteral.get(new TypeToken<DataKeyService<TDataKey>>(getClass()) {
            }.getType())
        ).to(
            (TypeLiteral<CommonDataKeyService<TDataKey>>) TypeLiteral.get(new TypeToken<CommonDataKeyService<TDataKey>>(getClass()) {
            }.getType())
        );

        bind(
            (TypeLiteral<SerializationTaskService>) TypeLiteral.get(new TypeToken<SerializationTaskService>(getClass()) {
            }.getType())
        ).to(
            (TypeLiteral<CommonSerializationTaskService<TUser, TCommandSource>>) TypeLiteral.get(new TypeToken<CommonSerializationTaskService<TUser, TCommandSource>>(getClass()) {
            }.getType())
        );


        bind(DateFormatService.class).to(CommonDateFormatService.class);

        bind(SyncUtils.class).to(CommonSyncUtils.class);

        bind(MongoConfig.class).toInstance(
            new MongoConfig(
                "rocks.milspecsg.msdatasync.model.core",
                ConfigKeys.DATA_STORE_NAME,
                ConfigKeys.MONGODB_HOSTNAME,
                ConfigKeys.MONGODB_PORT,
                ConfigKeys.MONGODB_DBNAME,
                ConfigKeys.MONGODB_USERNAME,
                ConfigKeys.MONGODB_PASSWORD,
                ConfigKeys.MONGODB_USE_AUTH
            )
        );

        be.bind(
            new TypeToken<UserSerializerComponent<?, ?, ?, ?, ?>>(getClass()) {
            },
            new TypeToken<UserSerializerComponent<?, Snapshot<?>, TUser, ?, ?>>(getClass()) {
            },
            new TypeToken<UserSerializerComponent<ObjectId, TMongoSnapshot, TUser, Datastore, MongoConfig>>(getClass()) {
            },
            new TypeToken<CommonUserSerializerComponent<ObjectId, TMongoMember, TMongoSnapshot, TUser, TDataKey, Datastore, MongoConfig>>(getClass()) {
            },
            MongoDBComponent.class
        );

        be.bind(
            new TypeToken<UserSerializerManager<Snapshot<?>, TUser>>(getClass()) {
            },
            new TypeToken<CommonUserSerializerManager<Snapshot<?>, TUser>>(getClass()) {
            }
        );

        be.bind(
            new TypeToken<SnapshotOptimizationService<?, ?, ?, ?, ?>>(getClass()) {
            },
            new TypeToken<SnapshotOptimizationService<?, TUser, TCommandSource, ?, ?>>(getClass()) {
            },
            new TypeToken<SnapshotOptimizationService<ObjectId, TUser, TCommandSource, Datastore, MongoConfig>>(getClass()) {
            },
            new TypeToken<CommonSnapshotOptimizationService<ObjectId, TMongoMember, TMongoSnapshot, TPlayer, TUser, TCommandSource, TDataKey, Datastore, MongoConfig>>(getClass()) {
            },
            MongoDBComponent.class
        );

        be.bind(
            new TypeToken<SnapshotOptimizationManager<TUser, TCommandSource>>(getClass()) {
            },
            new TypeToken<CommonSnapshotOptimizationManager<TUser, TCommandSource>>(getClass()) {
            }
        );

        be.bind(
            new TypeToken<MemberRepository<?, ?, ?, ?, ?, ?>>(getClass()) {
            },
            new TypeToken<MemberRepository<?, Member<?>, Snapshot<?>, TUser, ?, ?>>(getClass()) {
            },
            new TypeToken<MemberRepository<ObjectId, TMongoMember, TMongoSnapshot, TUser, Datastore, MongoConfig>>(getClass()) {
            },
            new TypeToken<CommonMongoMemberRepository<TMongoMember, TMongoSnapshot, TUser, TDataKey>>(getClass()) {
            },
            MongoDBComponent.class
        );

        be.bind(
            new TypeToken<MemberManager<Member<?>, Snapshot<?>, TUser, TString>>(getClass()) {
            },
            new TypeToken<CommonMemberManager<Member<?>, Snapshot<?>, TUser, TString>>(getClass()) {
            }
        );

        be.bind(
            new TypeToken<SnapshotRepository<?, ?, ?, ?, ?>>(getClass()) {
            },
            new TypeToken<SnapshotRepository<?, Snapshot<?>, TDataKey, ?, ?>>(getClass()) {
            },
            new TypeToken<SnapshotRepository<ObjectId, TMongoSnapshot, TDataKey, Datastore, MongoConfig>>(getClass()) {
            },
            new TypeToken<CommonMongoSnapshotRepository<TMongoSnapshot, TDataKey>>(getClass()) {
            },
            MongoDBComponent.class
        );

        be.bind(
            new TypeToken<SnapshotManager<Snapshot<?>, TDataKey>>(getClass()) {
            },
            new TypeToken<CommonSnapshotManager<Snapshot<?>, TDataKey>>(getClass()) {
            }
        );

        bind(new TypeLiteral<DataStoreContext<ObjectId, Datastore, MongoConfig>>() {
        }).to(new TypeLiteral<MongoContext>() {
        });

    }
}
