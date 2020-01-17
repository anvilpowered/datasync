package rocks.milspecsg.msdatasync.sponge.data.config;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.config.DefaultConfig;
import rocks.milspecsg.msdatasync.common.data.config.MSDataSyncConfigurationService;

@Singleton
public class MSDataSyncSpongeConfigurationService extends MSDataSyncConfigurationService {

    @Inject
    public MSDataSyncSpongeConfigurationService(@DefaultConfig(sharedRoot = false) ConfigurationLoader<CommentedConfigurationNode> configLoader) {
        super(configLoader);
    }
}
