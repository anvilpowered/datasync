package rocks.milspecsg.msdatasync.utils;

import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import rocks.milspecsg.msdatasync.api.member.MemberRepository;
import rocks.milspecsg.msdatasync.model.core.Member;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class Utils {

    public static <E> CompletableFuture<Boolean> serialize(MemberRepository<Member, Player, Key, User> memberRepository, Member member, Player player, Key<? extends BaseValue<E>> key) {
        return memberRepository.setMemberKey(member, key, player.get(key));
    }

    public static <E> CompletableFuture<Boolean> deserialize(MemberRepository<Member, Player, Key, User> memberRepository, Member member, Player player, Key<? extends BaseValue<E>> key) {
        return memberRepository.getMemberKey(member, key, key.getValueToken()).thenApplyAsync(optional -> {
            if (!optional.isPresent()) return false;
            player.offer(key, optional.get().get());
            return true;
        });
    }
}
