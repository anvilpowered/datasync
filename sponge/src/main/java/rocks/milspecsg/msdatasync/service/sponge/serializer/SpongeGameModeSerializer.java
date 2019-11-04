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

package rocks.milspecsg.msdatasync.service.sponge.serializer;

import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.User;
import rocks.milspecsg.msdatasync.model.core.snapshot.Snapshot;
import rocks.milspecsg.msdatasync.service.common.serializer.CommonGameModeSerializer;
import rocks.milspecsg.msdatasync.utils.Utils;

public class SpongeGameModeSerializer extends CommonGameModeSerializer<Snapshot<?>, Key<?>, User> {

    @Override
    public boolean serialize(Snapshot<?> snapshot, User user) {
        return Utils.serialize(snapshotManager, snapshot, user, Keys.GAME_MODE);
    }

    @Override
    public boolean deserialize(Snapshot<?> snapshot, User user) {
        return Utils.deserialize(snapshotManager, snapshot, user, Keys.GAME_MODE);
    }
}
