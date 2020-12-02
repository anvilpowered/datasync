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

package org.anvilpowered.datasync.spigot;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.anvilpowered.anvil.api.Environment;
import org.anvilpowered.datasync.api.DataSyncImpl;
import org.anvilpowered.datasync.spigot.listener.SpigotPlayerListener;
import org.anvilpowered.datasync.spigot.module.SpigotModule;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class DataSyncSpigot extends JavaPlugin {

    protected final Inner inner;

    public DataSyncSpigot() {
        AbstractModule module = new AbstractModule() {
            @Override
            protected void configure() {
                bind(Plugin.class).toInstance(DataSyncSpigot.this);
                bind(JavaPlugin.class).toInstance(DataSyncSpigot.this);
                bind(DataSyncSpigot.class).toInstance(DataSyncSpigot.this);
            }
        };
        Injector injector = Guice.createInjector(module);
        inner = new Inner(injector);
    }

    private final class Inner extends DataSyncImpl<String> {
        private Inner(Injector rootInjector) {
            super(rootInjector, new SpigotModule());
        }

        @Override
        protected void applyToBuilder(Environment.Builder builder) {
            super.applyToBuilder(builder);
            builder.addEarlyServices(SpigotPlayerListener.class, t->
                Bukkit.getPluginManager().registerEvents(t, DataSyncSpigot.this));
        }
    }
}
