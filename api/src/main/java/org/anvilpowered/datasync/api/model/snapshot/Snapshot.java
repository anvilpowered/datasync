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

package org.anvilpowered.datasync.api.model.snapshot;

import org.anvilpowered.anvil.api.model.ObjectWithId;

import java.util.List;
import java.util.Map;

public interface Snapshot<TKey> extends ObjectWithId<TKey> {

    String getName();
    void setName(String name);

    String getServer();
    void setServer(String server);

    List<String> getModulesUsed();
    void setModulesUsed(List<String> modulesUsed);

    List<String> getModulesFailed();
    void setModulesFailed(List<String> modulesFailed);

    Map<String, Object> getKeys();
    void setKeys(Map<String, Object> keys);

    byte[] getInventory();
    void setInventory(byte[] inventory);
}
