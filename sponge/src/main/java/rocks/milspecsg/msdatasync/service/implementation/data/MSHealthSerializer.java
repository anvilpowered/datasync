package rocks.milspecsg.msdatasync.service.implementation.data;

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.entity.HealthData;
import org.spongepowered.api.data.persistence.DataTranslators;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.service.data.ApiHealthSerializer;
import rocks.milspecsg.msdatasync.utils.Utils;

import java.util.concurrent.CompletableFuture;

public class MSHealthSerializer extends ApiHealthSerializer<Member, Player, Key, User> {

    @Override
    public CompletableFuture<Boolean> serialize(Member member, Player player) {
        return Utils.serialize(memberRepository, member, player, Keys.HEALTH);
    }

    @Override
    public CompletableFuture<Boolean> deserialize(Member member, Player player) {
        return Utils.deserialize(memberRepository, member, player, Keys.HEALTH);
    }
}
