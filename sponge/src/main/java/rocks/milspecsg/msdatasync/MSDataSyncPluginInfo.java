/*
 *     MSDataSync - MilSpecSG
 *     Copyright (C) 2019 Cableguy20
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package rocks.milspecsg.msdatasync;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import rocks.milspecsg.msrepository.PluginInfo;

import javax.inject.Singleton;

@Singleton
public final class MSDataSyncPluginInfo implements PluginInfo<Text> {
    public static final String id = "msdatasync";
    public static final String name = "MSDataSync";
    public static final String version = "1.0.0-SNAPSHOT";
    public static final String description = "A plugin to synchronize player inventories with a database";
    public static final String url = "https://github.com/MilSpecSG/MSDataSync";
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
