package rocks.milspecsg.msdatasync.service.implementation.data;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import rocks.milspecsg.msdatasync.events.SerializerInitializationEvent;
import rocks.milspecsg.msdatasync.model.core.Snapshot;
import rocks.milspecsg.msdatasync.service.data.ApiSnapshotSerializer;
import rocks.milspecsg.msrepository.SpongePluginInfo;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;

@Singleton
public class ApiSpongeSnapshotSerializer extends ApiSnapshotSerializer<Snapshot, Key, User, Inventory, ItemStackSnapshot> {

    @Inject
    public ApiSpongeSnapshotSerializer(ConfigurationService configurationService) {
        super(configurationService);
    }

    @Inject
    SpongePluginInfo pluginInfo;

    @Override
    protected void postLoadedEvent(Object plugin) {
        Sponge.getPluginManager().fromInstance(plugin).ifPresent(container -> {
            EventContext eventContext = EventContext.builder().add(EventContextKeys.PLUGIN, container).build();
            SerializerInitializationEvent<Snapshot> event = new SerializerInitializationEvent<>(this, snapshotRepository, Cause.of(eventContext, plugin));
            Sponge.getEventManager().post(event);
        });
    }

    @Override
    protected void announceEnabled(String name) {
        Sponge.getServer().getConsole().sendMessage(Text.of(pluginInfo.getPrefix(), TextColors.YELLOW, "Enabling ", name, " serializer"));
    }

    @Override
    protected String getUsername(User user) {
        return user.getName();
    }

}
