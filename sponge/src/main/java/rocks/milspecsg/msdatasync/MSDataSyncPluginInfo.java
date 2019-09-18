package rocks.milspecsg.msdatasync;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import rocks.milspecsg.msrepository.SpongePluginInfo;

import javax.inject.Singleton;

@Singleton
public final class MSDataSyncPluginInfo implements SpongePluginInfo {
    public static final String id = "msdatasync";
    public static final String name = "MSDataSync";
    public static final String version = "0.6.2";
    public static final String description = "A plugin to synchronize player inventories with a database";
    public static final String url = "https://milspecsg.rocks";
    public static final String authors = "Cableguy20";
    public static final Text pluginPrefix = Text.of(TextColors.GREEN, "[MSDataSync] ");

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getURL() {
        return url;
    }

    @Override
    public String getAuthors() {
        return authors;
    }

    @Override
    public Text getPrefix() {
        return pluginPrefix;
    }
}
