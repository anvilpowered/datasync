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

package org.anvilpowered.datasync.sponge.serializer;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.anvilpowered.anvil.api.data.registry.Registry;
import org.anvilpowered.anvil.api.plugin.PluginInfo;
import org.anvilpowered.datasync.common.serializer.CommonSnapshotSerializer;
import org.anvilpowered.datasync.sponge.DataSyncSponge;
import org.anvilpowered.datasync.sponge.events.SerializerInitializationEvent;
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

@Singleton
public class SpongeSnapshotSerializer extends CommonSnapshotSerializer<Key<?>, User, Player, Inventory, ItemStackSnapshot> {

    @Inject
    public SpongeSnapshotSerializer(Registry registry) {
        super(registry);
    }

    @Inject
    private PluginInfo<Text> pluginInfo;

    @Inject
    private DataSyncSponge dataSyncSponge;

    @Override
    protected void postLoadedEvent() {
        Sponge.getPluginManager().fromInstance(dataSyncSponge).ifPresent(container -> {
            EventContext eventContext = EventContext.builder().add(EventContextKeys.PLUGIN, container).build();
            Sponge.getEventManager().post(new SerializerInitializationEvent(this, snapshotManager, Cause.of(eventContext, dataSyncSponge)));
        });
    }

    @Override
    protected void announceEnabled(String name) {
        Sponge.getServer().getConsole().sendMessage(Text.of(pluginInfo.getPrefix(), TextColors.YELLOW, "Enabling ", name, " serializer"));
    }
}
