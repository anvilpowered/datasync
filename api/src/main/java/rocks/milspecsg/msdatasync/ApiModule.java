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
public class ApiModule<M extends Member, S extends Snapshot, P, K, U, I> extends AbstractModule {

    @Override
    protected void configure() {
        bind(
            (TypeLiteral<ExperienceSerializer<S, P>>) TypeLiteral.get(new TypeToken<ExperienceSerializer<S, P>>(getClass()) {}.getType())
        ).to(
            (TypeLiteral<ApiExperienceSerializer<S, P, K>>) TypeLiteral.get(new TypeToken<ApiExperienceSerializer<S, P, K>>(getClass()) {}.getType())
        );

        bind(
            (TypeLiteral<GameModeSerializer<S, P>>) TypeLiteral.get(new TypeToken<GameModeSerializer<S, P>>(getClass()) {}.getType())
        ).to(
            (TypeLiteral<ApiGameModeSerializer<S, P, K>>) TypeLiteral.get(new TypeToken<ApiGameModeSerializer<S, P, K>>(getClass()) {}.getType())
        );

        bind(
            (TypeLiteral<HealthSerializer<S, P>>) TypeLiteral.get(new TypeToken<HealthSerializer<S, P>>(getClass()) {}.getType())
        ).to(
            (TypeLiteral<ApiHealthSerializer<S, P, K>>) TypeLiteral.get(new TypeToken<ApiHealthSerializer<S, P, K>>(getClass()) {}.getType())
        );

        bind(
            (TypeLiteral<HungerSerializer<S, P>>) TypeLiteral.get(new TypeToken<HungerSerializer<S, P>>(getClass()) {}.getType())
        ).to(
            (TypeLiteral<ApiHungerSerializer<S, P, K>>) TypeLiteral.get(new TypeToken<ApiHungerSerializer<S, P, K>>(getClass()) {}.getType())
        );

        bind(
            (TypeLiteral<InventorySerializer<S, P, I>>) TypeLiteral.get(new TypeToken<InventorySerializer<S, P, I>>(getClass()) {}.getType())
        ).to(
            (TypeLiteral<ApiInventorySerializer<S, P, K, I>>) TypeLiteral.get(new TypeToken<ApiInventorySerializer<S, P, K, I>>(getClass()) {}.getType())
        );

        bind(
            (TypeLiteral<PlayerSerializer<S, P>>) TypeLiteral.get(new TypeToken<PlayerSerializer<S, P>>(getClass()) {}.getType())
        ).to(
            (TypeLiteral<ApiPlayerSerializer<M, S, P, K, U>>) TypeLiteral.get(new TypeToken<ApiPlayerSerializer<M, S, P, K, U>>(getClass()) {}.getType())
        );

        bind(
            (TypeLiteral<SnapshotSerializer<S, P>>) TypeLiteral.get(new TypeToken<SnapshotSerializer<S, P>>(getClass()) {}.getType())
        ).to(
            (TypeLiteral<ApiSnapshotSerializer<S, P, K, I>>) TypeLiteral.get(new TypeToken<ApiSnapshotSerializer<S, P, K, I>>(getClass()) {}.getType())
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
            (TypeLiteral<ApiSerializationTaskService<S, P>>) TypeLiteral.get(new TypeToken<ApiSerializationTaskService<S, P>>(getClass()) {}.getType())
        );

        bind(MongoContext.class).to(ApiMongoContext.class);
    }
}
