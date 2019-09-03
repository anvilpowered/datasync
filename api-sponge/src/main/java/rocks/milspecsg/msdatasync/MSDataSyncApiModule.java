package rocks.milspecsg.msdatasync;

import com.google.inject.TypeLiteral;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.service.data.*;
import rocks.milspecsg.msdatasync.service.implementation.data.*;
import rocks.milspecsg.msdatasync.service.implementation.keys.MSDataKeyService;
import rocks.milspecsg.msdatasync.service.implementation.member.MSMemberRepository;
import rocks.milspecsg.msdatasync.service.keys.ApiDataKeyService;
import rocks.milspecsg.msdatasync.service.member.ApiMemberRepository;

public class MSDataSyncApiModule extends ApiModule<Member, Player, Key, User> {
    @Override
    protected void configure() {
        super.configure();

        bind(new TypeLiteral<ApiExperienceSerializer<Member, Player, Key, User>>() {
        })
            .to(new TypeLiteral<MSExperienceSerializer>() {
            });

        bind(new TypeLiteral<ApiGameModeSerializer<Member, Player, Key, User>>() {
        })
            .to(new TypeLiteral<MSGameModeSerializer>() {
            });

        bind(new TypeLiteral<ApiHealthSerializer<Member, Player, Key, User>>() {
        })
            .to(new TypeLiteral<MSHealthSerializer>() {
            });

        bind(new TypeLiteral<ApiHungerSerializer<Member, Player, Key, User>>() {
        })
            .to(new TypeLiteral<MSHungerSerializer>() {
            });

        bind(new TypeLiteral<ApiPlayerSerializer<Member, Player, Key, User>>() {
        })
            .to(new TypeLiteral<MSPlayerSerializer>() {
            });

        bind(new TypeLiteral<ApiInventorySerializer<Member, Player, Key, User>>() {
        })
            .to(new TypeLiteral<MSInventorySerializer>() {
            });

        bind(new TypeLiteral<ApiDataKeyService<Key>>() {
        })
            .to(new TypeLiteral<MSDataKeyService>() {
            });

        bind(new TypeLiteral<ApiMemberRepository<Member, Player, Key, User>>() {
        })
            .to(new TypeLiteral<MSMemberRepository>() {
            });

    }
}