package rocks.milspecsg.msdatasync.service.implementation.data;

import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.service.data.ApiHungerSerializer;
import rocks.milspecsg.msdatasync.utils.Utils;

import java.util.concurrent.CompletableFuture;

public class MSHungerSerializer extends ApiHungerSerializer<Member, Player, Key, User> {

    @Override
    public CompletableFuture<Boolean> serialize(Member member, Player player, Object plugin) {
        return Utils.serialize(memberRepository, member, player, Keys.FOOD_LEVEL, plugin)
            .thenCombineAsync(Utils.serialize(memberRepository, member, player, Keys.SATURATION, plugin), (a, b) -> a && b);
    }

    @Override
    public CompletableFuture<Boolean> deserialize(Member member, Player player, Object plugin) {
        return Utils.deserialize(memberRepository, member, player, Keys.FOOD_LEVEL, plugin)
            .thenCombineAsync(Utils.deserialize(memberRepository, member, player, Keys.SATURATION, plugin), (a,b) -> a && b);
    }
}
