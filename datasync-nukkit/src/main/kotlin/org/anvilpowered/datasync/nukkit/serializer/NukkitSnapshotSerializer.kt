/*
 *   DataSync - AnvilPowered
 *   Copyright (C) 2020 Cableguy20
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.datasync.nukkit.serializer

import cn.nukkit.Player
import cn.nukkit.command.CommandSender
import cn.nukkit.inventory.Inventory
import cn.nukkit.item.Item
import com.google.inject.Inject
import com.google.inject.Singleton
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.anvil.api.util.TextService
import org.anvilpowered.datasync.common.serializer.CommonSnapshotSerializer

@Singleton
class NukkitSnapshotSerializer @Inject constructor(
    registry: Registry
) : CommonSnapshotSerializer<String, Player, Player, Inventory, Item>(registry) {

    @Inject
    private lateinit var textService: TextService<String, CommandSender>

    override fun postLoadedEvent() {
    }

    override fun announceEnabled(name: String) {
        textService.builder()
            .appendPrefix()
            .yellow().append("Enabling $name serializer")
            .sendToConsole()
    }
}
