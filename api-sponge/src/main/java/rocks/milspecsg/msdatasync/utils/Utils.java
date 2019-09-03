package rocks.milspecsg.msdatasync.utils;

import com.google.common.reflect.TypeToken;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.scheduler.Task;
import rocks.milspecsg.msdatasync.api.member.MemberRepository;
import rocks.milspecsg.msdatasync.model.core.Member;

import java.util.concurrent.CompletableFuture;

public class Utils {

    public static <E> CompletableFuture<Boolean> serialize(MemberRepository<Member, Player, Key, User> memberRepository, Member member, Player player, Key<? extends BaseValue<E>> key, Object plugin) {

        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();

        Task.builder().execute(
            () -> memberRepository.setMemberKey(member, key, player.get(key))
                .thenAcceptAsync(completableFuture::complete)
        ).submit(plugin);

        return completableFuture;
    }

    public static <E> CompletableFuture<Boolean> deserialize(MemberRepository<Member, Player, Key, User> memberRepository, Member member, Player player, Key<? extends BaseValue<E>> key, Object plugin) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();

        memberRepository.getMemberKey(member, key).thenAcceptAsync(optional -> {
            if (!optional.isPresent()) {
                completableFuture.complete(false);
            }

            Task.builder().execute(() -> Sponge.getServer().getPlayer(player.getUniqueId()).ifPresent(p -> {
                try {
                    p.offer(key, (E) decode(optional.get(), key));
                    completableFuture.complete(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    completableFuture.complete(false);
                }
            })).submit(plugin);
        });

        return completableFuture;
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
