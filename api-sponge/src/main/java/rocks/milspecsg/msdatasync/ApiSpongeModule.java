package rocks.milspecsg.msdatasync;

import com.google.common.reflect.TypeToken;
import com.google.inject.TypeLiteral;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.model.core.Snapshot;
import rocks.milspecsg.msdatasync.service.data.*;
import rocks.milspecsg.msdatasync.service.implementation.data.*;
import rocks.milspecsg.msdatasync.service.implementation.keys.ApiSpongeDataKeyService;
import rocks.milspecsg.msdatasync.service.implementation.member.ApiSpongeMemberRepository;
import rocks.milspecsg.msdatasync.service.implementation.snapshot.ApiSpongeSnapshotRepository;
import rocks.milspecsg.msdatasync.service.keys.ApiDataKeyService;
import rocks.milspecsg.msdatasync.service.member.ApiMemberRepository;
import rocks.milspecsg.msdatasync.service.snapshot.ApiSnapshotRepository;

@SuppressWarnings({"unchecked", "UnstableApiUsage"})
public class ApiSpongeModule<M extends Member, S extends Snapshot> extends ApiModule<M, S, Player, Key, User> {

    @Override
    protected void configure() {
        super.configure();

        bind(
            (TypeLiteral<ApiExperienceSerializer<S, Player, Key>>) TypeLiteral.get(new TypeToken<ApiExperienceSerializer<S, Player, Key>>(getClass()) {}.getType())
        ).to(
            (TypeLiteral<ApiSpongeExperienceSerializer<S>>) TypeLiteral.get(new TypeToken<ApiSpongeExperienceSerializer<S>>(getClass()) {}.getType())
        );

        bind(
            (TypeLiteral<ApiGameModeSerializer<S, Player, Key>>) TypeLiteral.get(new TypeToken<ApiGameModeSerializer<S, Player, Key>>(getClass()) {}.getType())
        ).to(
            (TypeLiteral<ApiSpongeGameModeSerializer<S>>) TypeLiteral.get(new TypeToken<ApiSpongeGameModeSerializer<S>>(getClass()) {}.getType())
        );

        bind(
            (TypeLiteral<ApiHealthSerializer<S, Player, Key>>) TypeLiteral.get(new TypeToken<ApiHealthSerializer<S, Player, Key>>(getClass()) {}.getType())
        ).to(
            (TypeLiteral<ApiSpongeHealthSerializer<S>>) TypeLiteral.get(new TypeToken<ApiSpongeHealthSerializer<S>>(getClass()) {}.getType())
        );

        bind(
            (TypeLiteral<ApiHungerSerializer<S, Player, Key>>) TypeLiteral.get(new TypeToken<ApiHungerSerializer<S, Player, Key>>(getClass()) {}.getType())
        ).to(
            (TypeLiteral<ApiSpongeHungerSerializer<S>>) TypeLiteral.get(new TypeToken<ApiSpongeHungerSerializer<S>>(getClass()) {}.getType())
        );

        bind(
            (TypeLiteral<ApiInventorySerializer<S, Player, Key>>) TypeLiteral.get(new TypeToken<ApiInventorySerializer<S, Player, Key>>(getClass()) {}.getType())
        ).to(
            (TypeLiteral<ApiSpongeInventorySerializer<S>>) TypeLiteral.get(new TypeToken<ApiSpongeInventorySerializer<S>>(getClass()) {}.getType())
        );

        bind(
            (TypeLiteral<ApiPlayerSerializer<M, S, Player, Key, User>>) TypeLiteral.get(new TypeToken<ApiPlayerSerializer<M, S, Player, Key, User>>(getClass()) {}.getType())
        ).to(
            (TypeLiteral<ApiSpongePlayerSerializer<M, S>>) TypeLiteral.get(new TypeToken<ApiSpongePlayerSerializer<M, S>>(getClass()) {}.getType())
        );

        bind(
            (TypeLiteral<ApiSnapshotSerializer<S, Player, Key>>) TypeLiteral.get(new TypeToken<ApiSnapshotSerializer<S, Player, Key>>(getClass()) {}.getType())
        ).to(
            (TypeLiteral<ApiSpongeSnapshotSerializer<S>>) TypeLiteral.get(new TypeToken<ApiSpongeSnapshotSerializer<S>>(getClass()) {}.getType())
        );

        bind(
            (TypeLiteral<ApiMemberRepository<M, S, Key, User>>) TypeLiteral.get(new TypeToken<ApiMemberRepository<M, S, Key, User>>(getClass()) {}.getType())
        ).to(
            (TypeLiteral<ApiSpongeMemberRepository<M, S>>) TypeLiteral.get(new TypeToken<ApiSpongeMemberRepository<M, S>>(getClass()) {}.getType())
        );

        bind(
            (TypeLiteral<ApiSnapshotRepository<S, Key>>) TypeLiteral.get(new TypeToken<ApiSnapshotRepository<S, Key>>(getClass()) {}.getType())
        ).to(
            (TypeLiteral<ApiSpongeSnapshotRepository<S>>) TypeLiteral.get(new TypeToken<ApiSpongeSnapshotRepository<S>>(getClass()) {}.getType())
        );

        bind(new TypeLiteral<ApiDataKeyService<Key>>() {})
            .to(new TypeLiteral<ApiSpongeDataKeyService>() {});

    }
}