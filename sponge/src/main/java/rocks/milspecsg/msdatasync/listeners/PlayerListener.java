package rocks.milspecsg.msdatasync.listeners;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import rocks.milspecsg.msdatasync.api.data.PlayerSerializer;
import rocks.milspecsg.msdatasync.api.member.MemberRepository;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.service.config.ConfigKeys;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;

@Singleton
public class PlayerListener {

    @Inject
    ConfigurationService configurationService;

    @Inject
    PlayerSerializer<Member, Player> playerSerializer;

    private boolean enabled = true;

    public void loadConfig() {
        enabled = configurationService.getConfigBoolean(ConfigKeys.SERIALIZE_ON_JOIN_LEAVE);
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join joinEvent) {
        playerSerializer.serialize(joinEvent.getTargetEntity());
    }

    @Listener
    public void onPlayerDisconnect(ClientConnectionEvent.Disconnect disconnectEvent) {
        playerSerializer.deserialize(disconnectEvent.getTargetEntity());
    }
}
