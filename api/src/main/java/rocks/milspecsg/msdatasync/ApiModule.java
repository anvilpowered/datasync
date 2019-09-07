package rocks.milspecsg.msdatasync;

import com.google.common.reflect.TypeToken;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import rocks.milspecsg.msdatasync.api.data.*;
import rocks.milspecsg.msdatasync.api.keys.DataKeyService;
import rocks.milspecsg.msdatasync.api.member.MemberRepository;
import rocks.milspecsg.msdatasync.api.snapshot.SnapshotRepository;
import rocks.milspecsg.msdatasync.api.tasks.SerializationTaskService;
import rocks.milspecsg.msdatasync.db.mongodb.ApiMongoContext;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.model.core.Snapshot;
import rocks.milspecsg.msdatasync.service.data.*;
import rocks.milspecsg.msdatasync.service.keys.ApiDataKeyService;
import rocks.milspecsg.msdatasync.service.member.ApiMemberRepository;
import rocks.milspecsg.msdatasync.service.snapshot.ApiSnapshotRepository;
import rocks.milspecsg.msdatasync.service.tasks.ApiSerializationTaskService;
import rocks.milspecsg.msrepository.db.mongodb.MongoContext;

@SuppressWarnings({"unchecked", "UnstableApiUsage"})
public class ApiModule<M extends Member, S extends Snapshot, K, U, I> extends AbstractModule {

    @Override
    protected void configure() {
        bind(
            (TypeLiteral<ExperienceSerializer<S, U>>) TypeLiteral.get(new TypeToken<ExperienceSerializer<S, U>>(getClass()) {}.getType())
        ).to(
            (TypeLiteral<ApiExperienceSerializer<S, K, U>>) TypeLiteral.get(new TypeToken<ApiExperienceSerializer<S, K, U>>(getClass()) {}.getType())
        );

        bind(
            (TypeLiteral<GameModeSerializer<S, U>>) TypeLiteral.get(new TypeToken<GameModeSerializer<S, U>>(getClass()) {}.getType())
        ).to(
            (TypeLiteral<ApiGameModeSerializer<S, K, U>>) TypeLiteral.get(new TypeToken<ApiGameModeSerializer<S, K, U>>(getClass()) {}.getType())
        );

        bind(
            (TypeLiteral<HealthSerializer<S, U>>) TypeLiteral.get(new TypeToken<HealthSerializer<S, U>>(getClass()) {}.getType())
        ).to(
            (TypeLiteral<ApiHealthSerializer<S, K, U>>) TypeLiteral.get(new TypeToken<ApiHealthSerializer<S, K, U>>(getClass()) {}.getType())
        );

        bind(
            (TypeLiteral<HungerSerializer<S, U>>) TypeLiteral.get(new TypeToken<HungerSerializer<S, U>>(getClass()) {}.getType())
        ).to(
            (TypeLiteral<ApiHungerSerializer<S, K, U>>) TypeLiteral.get(new TypeToken<ApiHungerSerializer<S, K, U>>(getClass()) {}.getType())
        );

        bind(
            (TypeLiteral<InventorySerializer<S, U, I>>) TypeLiteral.get(new TypeToken<InventorySerializer<S, U, I>>(getClass()) {}.getType())
        ).to(
            (TypeLiteral<ApiInventorySerializer<S, K, U, I>>) TypeLiteral.get(new TypeToken<ApiInventorySerializer<S, K, U, I>>(getClass()) {}.getType())
        );

        bind(
            (TypeLiteral<UserSerializer<S, U>>) TypeLiteral.get(new TypeToken<UserSerializer<S, U>>(getClass()) {}.getType())
        ).to(
            (TypeLiteral<ApiUserSerializer<M, S, K, U>>) TypeLiteral.get(new TypeToken<ApiUserSerializer<M, S, K, U>>(getClass()) {}.getType())
        );

        bind(
            (TypeLiteral<SnapshotSerializer<S, U>>) TypeLiteral.get(new TypeToken<SnapshotSerializer<S, U>>(getClass()) {}.getType())
        ).to(
            (TypeLiteral<ApiSnapshotSerializer<S, K, U, I>>) TypeLiteral.get(new TypeToken<ApiSnapshotSerializer<S, K, U, I>>(getClass()) {}.getType())
        );

        bind(
            (TypeLiteral<MemberRepository<M, S, U>>) TypeLiteral.get(new TypeToken<MemberRepository<M, S, U>>(getClass()) {}.getType())
        ).to(
            (TypeLiteral<ApiMemberRepository<M, S, K, U>>) TypeLiteral.get(new TypeToken<ApiMemberRepository<M, S, K, U>>(getClass()) {}.getType())
        );

        bind(
            (TypeLiteral<SnapshotRepository<S, K>>) TypeLiteral.get(new TypeToken<SnapshotRepository<S, K>>(getClass()) {}.getType())
        ).to(
            (TypeLiteral<ApiSnapshotRepository<S, K>>) TypeLiteral.get(new TypeToken<ApiSnapshotRepository<S, K>>(getClass()) {}.getType())
        );

        bind(
            (TypeLiteral<DataKeyService<K>>) TypeLiteral.get(new TypeToken<DataKeyService<K>>(getClass()) {}.getType())
        ).to(
            (TypeLiteral<ApiDataKeyService<K>>) TypeLiteral.get(new TypeToken<ApiDataKeyService<K>>(getClass()) {}.getType())
        );

        bind(
            (TypeLiteral<SerializationTaskService>) TypeLiteral.get(new TypeToken<SerializationTaskService>(getClass()) {}.getType())
        ).to(
            (TypeLiteral<ApiSerializationTaskService<M, S, U>>) TypeLiteral.get(new TypeToken<ApiSerializationTaskService<M, S, U>>(getClass()) {}.getType())
        );

        bind(MongoContext.class).to(ApiMongoContext.class);
    }
}
