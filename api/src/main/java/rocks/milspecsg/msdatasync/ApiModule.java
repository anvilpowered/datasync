package rocks.milspecsg.msdatasync;

import com.google.common.reflect.TypeToken;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import rocks.milspecsg.msdatasync.api.data.*;
import rocks.milspecsg.msdatasync.api.keys.DataKeyService;
import rocks.milspecsg.msdatasync.api.member.MemberRepository;
import rocks.milspecsg.msdatasync.api.tasks.SerializationTaskService;
import rocks.milspecsg.msdatasync.db.mongodb.ApiMongoContext;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.service.data.*;
import rocks.milspecsg.msdatasync.service.keys.ApiDataKeyService;
import rocks.milspecsg.msdatasync.service.member.ApiMemberRepository;
import rocks.milspecsg.msdatasync.service.tasks.ApiSerializationTaskService;
import rocks.milspecsg.msrepository.db.mongodb.MongoContext;

public class ApiModule<M extends Member, P, K, U> extends AbstractModule {

    @SuppressWarnings({"unchecked", "UnstableApiUsage"})
    @Override
    protected void configure() {
        bind(
            (TypeLiteral<ExperienceSerializer<M, P>>) TypeLiteral.get(new TypeToken<ExperienceSerializer<M, P>>(getClass()) {}.getType())
        ).to(
            (TypeLiteral<ApiExperienceSerializer<M, P, K, U>>) TypeLiteral.get(new TypeToken<ApiExperienceSerializer<M, P, K, U>>(getClass()) {}.getType())
        );

        bind(
            (TypeLiteral<GameModeSerializer<M, P>>) TypeLiteral.get(new TypeToken<GameModeSerializer<M, P>>(getClass()) {}.getType())
        ).to(
            (TypeLiteral<ApiGameModeSerializer<M, P, K, U>>) TypeLiteral.get(new TypeToken<ApiGameModeSerializer<M, P, K, U>>(getClass()) {}.getType())
        );

        bind(
            (TypeLiteral<HealthSerializer<M, P>>) TypeLiteral.get(new TypeToken<HealthSerializer<M, P>>(getClass()) {}.getType())
        ).to(
            (TypeLiteral<ApiHealthSerializer<M, P, K, U>>) TypeLiteral.get(new TypeToken<ApiHealthSerializer<M, P, K, U>>(getClass()) {}.getType())
        );

        bind(
            (TypeLiteral<HungerSerializer<M, P>>) TypeLiteral.get(new TypeToken<HungerSerializer<M, P>>(getClass()) {}.getType())
        ).to(
            (TypeLiteral<ApiHungerSerializer<M, P, K, U>>) TypeLiteral.get(new TypeToken<ApiHungerSerializer<M, P, K, U>>(getClass()) {}.getType())
        );

        bind(
            (TypeLiteral<InventorySerializer<M, P>>) TypeLiteral.get(new TypeToken<InventorySerializer<M, P>>(getClass()) {}.getType())
        ).to(
            (TypeLiteral<ApiInventorySerializer<M, P, K, U>>) TypeLiteral.get(new TypeToken<ApiInventorySerializer<M, P, K, U>>(getClass()) {}.getType())
        );

        bind(
            (TypeLiteral<PlayerSerializer<M, P>>) TypeLiteral.get(new TypeToken<PlayerSerializer<M, P>>(getClass()) {}.getType())
        ).to(
            (TypeLiteral<ApiPlayerSerializer<M, P, K, U>>) TypeLiteral.get(new TypeToken<ApiPlayerSerializer<M, P, K, U>>(getClass()) {}.getType())
        );

        bind(
            (TypeLiteral<MemberRepository<M, P, K, U>>) TypeLiteral.get(new TypeToken<MemberRepository<M, P, K, U>>(getClass()) {}.getType())
        ).to(
            (TypeLiteral<ApiMemberRepository<M, P, K, U>>) TypeLiteral.get(new TypeToken<ApiMemberRepository<M, P, K, U>>(getClass()) {}.getType())
        );

        bind(
            (TypeLiteral<DataKeyService<K>>) TypeLiteral.get(new TypeToken<DataKeyService<K>>(getClass()) {}.getType())
        ).to(
            (TypeLiteral<ApiDataKeyService<K>>) TypeLiteral.get(new TypeToken<ApiDataKeyService<K>>(getClass()) {}.getType())
        );

        bind(SerializationTaskService.class).to(new TypeLiteral<ApiSerializationTaskService>() {});

        bind(MongoContext.class).to(ApiMongoContext.class);
    }
}
