package rocks.milspecsg.msdatasync.listeners;

import com.google.inject.Inject;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import rocks.milspecsg.msdatasync.api.data.PlayerSerializer;
import rocks.milspecsg.msdatasync.api.member.MemberRepository;
import rocks.milspecsg.msdatasync.model.core.Member;

public class PlayerListener {


    @Inject
    PlayerSerializer<Member, Player> memberSerializer;

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join joinEvent) {

    }

    @Listener
    public void onPlayerDisconnect(ClientConnectionEvent.Disconnect disconnectEvent) {
    }
}
