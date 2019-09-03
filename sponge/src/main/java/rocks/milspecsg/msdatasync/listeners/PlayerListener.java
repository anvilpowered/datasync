package rocks.milspecsg.msdatasync.listeners;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import rocks.milspecsg.msdatasync.MSDataSync;
import rocks.milspecsg.msdatasync.MSDataSyncPluginInfo;
import rocks.milspecsg.msdatasync.api.data.PlayerSerializer;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.service.config.ConfigKeys;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;

@Singleton
public class PlayerListener {

    private ConfigurationService configurationService;

    @Inject
    PlayerSerializer<Member, Player> playerSerializer;

    private boolean enabled = true;

    @Inject
    public PlayerListener(ConfigurationService configurationService) {
        this.configurationService = configurationService;
        this.configurationService.addConfigLoadedListener(this::loadConfig);
    }

    private void loadConfig() {
        enabled = configurationService.getConfigBoolean(ConfigKeys.SERIALIZE_ON_JOIN_LEAVE);
        if (!enabled) {
            Sponge.getServer().getConsole().sendMessage(
                Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.RED,
                    "Attention! You have opted to disable join/leave syncing.\n" +
                        "If you would like to enable this, set `serializeOnJoinLeave=true` in the config and restart your server or run /sync reload")
            );
        }
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join joinEvent) {
        if (enabled) {
            Player player = joinEvent.getTargetEntity();
            playerSerializer.serialize(player, MSDataSync.plugin).thenAcceptAsync(success -> {
                if (success) {
                    Sponge.getServer().getConsole().sendMessage(
                        Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.YELLOW, "Successfully serialized ", player.getName(), " on join!")
                    );
                } else {
                    Sponge.getServer().getConsole().sendMessage(
                        Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.RED, "An error occurred while serializing ", player.getName(), " on join!")
                    );
                }
            });
        }
    }

    @Listener
    public void onPlayerDisconnect(ClientConnectionEvent.Disconnect disconnectEvent) {
        if (enabled) {
            Player player = disconnectEvent.getTargetEntity();
            playerSerializer.deserialize(player, MSDataSync.plugin).thenAcceptAsync(success -> {
                if (success) {
                    Sponge.getServer().getConsole().sendMessage(
                        Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.YELLOW, "Successfully deserialized ", player.getName(), " on disconnect!")
                    );
                } else {
                    Sponge.getServer().getConsole().sendMessage(
                        Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.RED, "An error occurred while deserializing ", player.getName(), " on disconnect!")
                    );
                }
            });
        }
    }
}
