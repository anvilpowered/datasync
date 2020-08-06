/*
 *   DataSync - AnvilPowered
 *   Copyright (C) 2020 Cableguy20
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

package org.anvilpowered.datasync.sponge;

import com.google.inject.Inject;
import com.google.inject.Injector;
import org.anvilpowered.anvil.api.Environment;
import org.anvilpowered.datasync.api.DataSyncImpl;
import org.anvilpowered.datasync.common.plugin.DataSyncPluginInfo;
import org.anvilpowered.datasync.sponge.listener.SpongePlayerListener;
import org.anvilpowered.datasync.sponge.module.SpongeModule;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;

@Plugin(
    id = DataSyncPluginInfo.id,
    name = DataSyncPluginInfo.name,
    version = DataSyncPluginInfo.version,
    dependencies = @Dependency(
        id = "anvil",
        version = "0.2"
    ),
    description = DataSyncPluginInfo.description,
    url = DataSyncPluginInfo.url,
    authors = "Cableguy20"
)
public class DataSyncSponge extends DataSyncImpl<Key<?>> {

    @Inject
    public DataSyncSponge(Injector injector) {
        super(injector, new SpongeModule());
    }

    @Override
    protected void applyToBuilder(Environment.Builder builder) {
        super.applyToBuilder(builder);
        builder.addEarlyServices(SpongePlayerListener.class, t ->
            Sponge.getEventManager().registerListeners(this, t));
    }

    @Listener
    public void reload(GameReloadEvent event) {
        environment.reload();
    }
}
