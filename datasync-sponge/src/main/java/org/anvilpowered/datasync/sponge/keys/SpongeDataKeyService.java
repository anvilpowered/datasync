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

package org.anvilpowered.datasync.sponge.keys;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.anvilpowered.datasync.common.key.CommonDataKeyService;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;

@Singleton
public class SpongeDataKeyService extends CommonDataKeyService<Key<?>> {

    @Inject
    public SpongeDataKeyService() {
        addMapping(Keys.FOOD_LEVEL, "food_level");
        addMapping(Keys.SATURATION, "saturation");
        addMapping(Keys.HEALTH, "health");
        addMapping(Keys.TOTAL_EXPERIENCE, "total_experience");
        addMapping(Keys.GAME_MODE, "game_mode");
    }
}
