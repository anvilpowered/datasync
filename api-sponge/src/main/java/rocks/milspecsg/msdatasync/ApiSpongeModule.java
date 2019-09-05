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
public class ApiSpongeModule extends ApiModule<Member, Snapshot, Player, Key, User> {

    @Override
    protected void configure() {
        super.configure();

        bind(
            (TypeLiteral<ApiExperienceSerializer<Snapshot, Player, Key>>) TypeLiteral.get(new TypeToken<ApiExperienceSerializer<Snapshot, Player, Key>>(getClass()) {}.getType())
        ).to(
            (TypeLiteral<ApiSpongeExperienceSerializer>) TypeLiteral.get(new TypeToken<ApiSpongeExperienceSerializer>(getClass()) {}.getType())
        );

        bind(
            (TypeLiteral<ApiGameModeSerializer<Snapshot, Player, Key>>) TypeLiteral.get(new TypeToken<ApiGameModeSerializer<Snapshot, Player, Key>>(getClass()) {}.getType())
        ).to(
            (TypeLiteral<ApiSpongeGameModeSerializer>) TypeLiteral.get(new TypeToken<ApiSpongeGameModeSerializer>(getClass()) {}.getType())
        );

        bind(
            (TypeLiteral<ApiHealthSerializer<Snapshot, Player, Key>>) TypeLiteral.get(new TypeToken<ApiHealthSerializer<Snapshot, Player, Key>>(getClass()) {}.getType())
        ).to(
            (TypeLiteral<ApiSpongeHealthSerializer>) TypeLiteral.get(new TypeToken<ApiSpongeHealthSerializer>(getClass()) {}.getType())
        );

        bind(
            (TypeLiteral<ApiHungerSerializer<Snapshot, Player, Key>>) TypeLiteral.get(new TypeToken<ApiHungerSerializer<Snapshot, Player, Key>>(getClass()) {}.getType())
        ).to(
            (TypeLiteral<ApiSpongeHungerSerializer>) TypeLiteral.get(new TypeToken<ApiSpongeHungerSerializer>(getClass()) {}.getType())
        );

        bind(
            (TypeLiteral<ApiInventorySerializer<Snapshot, Player, Key>>) TypeLiteral.get(new TypeToken<ApiInventorySerializer<Snapshot, Player, Key>>(getClass()) {}.getType())
        ).to(
            (TypeLiteral<ApiSpongeInventorySerializer>) TypeLiteral.get(new TypeToken<ApiSpongeInventorySerializer>(getClass()) {}.getType())
        );

        bind(
            (TypeLiteral<ApiPlayerSerializer<Member, Snapshot, Player, Key, User>>) TypeLiteral.get(new TypeToken<ApiPlayerSerializer<Member, Snapshot, Player, Key, User>>(getClass()) {}.getType())
        ).to(
            (TypeLiteral<ApiSpongePlayerSerializer>) TypeLiteral.get(new TypeToken<ApiSpongePlayerSerializer>(getClass()) {}.getType())
        );

        bind(
            (TypeLiteral<ApiSnapshotSerializer<Snapshot, Player, Key>>) TypeLiteral.get(new TypeToken<ApiSnapshotSerializer<Snapshot, Player, Key>>(getClass()) {}.getType())
        ).to(
            (TypeLiteral<ApiSpongeSnapshotSerializer>) TypeLiteral.get(new TypeToken<ApiSpongeSnapshotSerializer>(getClass()) {}.getType())
        );

        bind(
            (TypeLiteral<ApiMemberRepository<Member, Snapshot, Key, User>>) TypeLiteral.get(new TypeToken<ApiMemberRepository<Member, Snapshot, Key, User>>(getClass()) {}.getType())
        ).to(
            (TypeLiteral<ApiSpongeMemberRepository>) TypeLiteral.get(new TypeToken<ApiSpongeMemberRepository>(getClass()) {}.getType())
        );

        bind(
            (TypeLiteral<ApiSnapshotRepository<Snapshot, Key>>) TypeLiteral.get(new TypeToken<ApiSnapshotRepository<Snapshot, Key>>(getClass()) {}.getType())
        ).to(
            (TypeLiteral<ApiSpongeSnapshotRepository>) TypeLiteral.get(new TypeToken<ApiSpongeSnapshotRepository>(getClass()) {}.getType())
        );

        bind(new TypeLiteral<ApiDataKeyService<Key>>() {})
            .to(new TypeLiteral<ApiSpongeDataKeyService>() {});

    }
}