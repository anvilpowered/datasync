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

package org.anvilpowered.datasync.api;

import com.google.common.reflect.TypeToken;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.anvilpowered.anvil.api.Environment;
import org.anvilpowered.datasync.api.key.DataKeyService;
import org.anvilpowered.datasync.api.task.SerializationTaskService;
import org.anvilpowered.datasync.common.plugin.DataSyncPluginInfo;

@SuppressWarnings("UnstableApiUsage")
public class DataSyncImpl<TDataKey> extends DataSync {

    protected DataSyncImpl(Injector injector, Module module) {
        super(DataSyncPluginInfo.id, injector, module);
    }

    protected void applyToBuilder(Environment.Builder builder) {
        builder.addEarlyServices(
            new TypeToken<DataKeyService<TDataKey>>(getClass()) {
            })
            .addEarlyServices(SerializationTaskService.class)
            .withRootCommand();
    }

    @Override
    protected void whenReady(Environment environment) {
        super.whenReady(environment);
        DataSync.environment = environment;
    }
}
