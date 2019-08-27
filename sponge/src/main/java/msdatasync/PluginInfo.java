package msdatasync;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public abstract class PluginInfo {
    public static final String Id = "msdatasync";
    public static final String Name = "rocks.milspecsg.msdatasync.MSDataSync";
    public static final String Version = "0.1.0-dev";
    public static final String Description = "A plugin to synchronize player inventories with a database";
    public static final String Url = "https://milspecsg.rocks";
    public static final String Authors = "Cableguy20";
    public static final Text PluginPrefix = Text.of(TextColors.GREEN, "[rocks.milspecsg.msdatasync.MSDataSync] ");
}
