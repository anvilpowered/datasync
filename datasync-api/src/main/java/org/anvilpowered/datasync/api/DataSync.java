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

package org.anvilpowered.datasync.api;

import com.google.common.base.Preconditions;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.anvilpowered.anvil.api.Environment;
import org.anvilpowered.anvil.api.data.registry.Registry;
import org.anvilpowered.anvil.base.plugin.BasePlugin;

public class DataSync extends BasePlugin {

    protected static Environment environment;
    private static final String NOT_LOADED = "DataSync has not been loaded yet!";

    DataSync(String name, Injector injector, Module module) {
        super(name, injector, module);
    }

    public static Environment getEnvironment() {
        return Preconditions.checkNotNull(environment, NOT_LOADED);
    }

    public static Registry getRegistry() {
        return getEnvironment().getInjector().getInstance(Registry.class);
    }
}
