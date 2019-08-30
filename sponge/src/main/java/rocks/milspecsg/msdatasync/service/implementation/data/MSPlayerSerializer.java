package rocks.milspecsg.msdatasync.service.implementation.data;

import com.google.inject.Singleton;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.service.data.ApiPlayerSerializer;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

@Singleton
public class MSPlayerSerializer extends ApiPlayerSerializer<Member, Player, Key, User> {

    @Override
    public CompletableFuture<Boolean> serialize(Player player) {
        return memberRepository.getOneOrGenerate(player.getUniqueId()).thenApplyAsync(optionalMember -> {
            if (!optionalMember.isPresent()) return false;
            Member member = optionalMember.get();
            if (member.keys == null) member.keys = new HashMap<>();
            return serialize(member, player).join();
        });
    }

    @Override
    public CompletableFuture<Boolean> deserialize(Player player) {
        return memberRepository.getOne(player.getUniqueId()).thenApplyAsync(optionalMember -> {
            if (!optionalMember.isPresent()) return false;
            Member member = optionalMember.get();
            if (member.keys == null) member.keys = new HashMap<>();
            return deserialize(member, player).join();
        });
    }
}
