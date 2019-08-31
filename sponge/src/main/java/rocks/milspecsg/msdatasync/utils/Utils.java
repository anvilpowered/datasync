package rocks.milspecsg.msdatasync.utils;

import com.google.common.reflect.TypeToken;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.generator.dummy.DummyObjectProvider;
import rocks.milspecsg.msdatasync.MSDataSync;
import rocks.milspecsg.msdatasync.api.member.MemberRepository;
import rocks.milspecsg.msdatasync.model.core.Member;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class Utils {

    public static <E> CompletableFuture<Boolean> serialize(MemberRepository<Member, Player, Key, User> memberRepository, Member member, Player player, Key<? extends BaseValue<E>> key) {
        return memberRepository.setMemberKey(member, key, player.get(key));
    }

    public static <E> CompletableFuture<Boolean> deserialize(MemberRepository<Member, Player, Key, User> memberRepository, Member member, Player player, Key<? extends BaseValue<E>> key) {
        return memberRepository.getMemberKey(member, key).thenApplyAsync(optional -> {
            if (!optional.isPresent()) {
                return false;
            }

            Task.builder().execute(() -> {
                Sponge.getServer().getPlayer(player.getUniqueId()).ifPresent(p -> p.offer(key, (E) decode(optional.get(), key)));
            }).submit(MSDataSync.plugin);
            return true;
        });
    }

    private static Object decode(Object value, Key<? extends BaseValue<?>> key) {
        TypeToken<?> typeToken = key.getElementToken();

        if (typeToken.isSubtypeOf(GameMode.class)) {
            switch (value.toString()) {
                case "ADVENTURE":
                    return GameModes.ADVENTURE;
                case "CREATIVE":
                    return GameModes.CREATIVE;
                case "SPECTATOR":
                    return GameModes.SPECTATOR;
                case "SURVIVAL":
                    return GameModes.SURVIVAL;
                // "NOT_SET"
                default:
                    return GameModes.NOT_SET;
            }
        }
        return value;
    }


}
