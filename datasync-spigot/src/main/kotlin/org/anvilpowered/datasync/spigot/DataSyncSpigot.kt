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
package org.anvilpowered.datasync.spigot

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import org.anvilpowered.anvil.api.Environment
import org.anvilpowered.datasync.api.DataSyncImpl
import org.anvilpowered.datasync.spigot.listener.SpigotPlayerListener
import org.anvilpowered.datasync.spigot.module.SpigotModule
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin

class DataSyncSpigot : JavaPlugin() {
    private val inner: Inner

    init {
        val module: AbstractModule = object : AbstractModule() {
            override fun configure() {
                bind(Plugin::class.java).toInstance(this@DataSyncSpigot)
                bind(JavaPlugin::class.java).toInstance(this@DataSyncSpigot)
                bind(DataSyncSpigot::class.java).toInstance(this@DataSyncSpigot)
            }
        }
        val injector = Guice.createInjector(module)
        inner = Inner(injector)
    }

    private inner class Inner(rootInjector: Injector) : DataSyncImpl<String>(rootInjector, SpigotModule()) {
        override fun applyToBuilder(builder: Environment.Builder) {
            super.applyToBuilder(builder)
            builder.setLoggerSupplier(this@DataSyncSpigot::getLogger)
            builder.addEarlyServices(SpigotPlayerListener::class.java) { t: SpigotPlayerListener ->
                Bukkit.getPluginManager().registerEvents(t, this@DataSyncSpigot)
            }
        }
    }
}
