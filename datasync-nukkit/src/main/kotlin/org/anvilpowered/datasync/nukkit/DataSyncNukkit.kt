package org.anvilpowered.datasync.nukkit

import cn.nukkit.Server
import cn.nukkit.plugin.Plugin
import cn.nukkit.plugin.PluginBase
import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import org.anvilpowered.anvil.api.Environment
import org.anvilpowered.datasync.api.DataSyncImpl
import org.anvilpowered.datasync.nukkit.listener.NukkitPlayerListener
import org.anvilpowered.datasync.nukkit.module.NukkitModule

class DataSyncNukkit : PluginBase() {
    private val inner: Inner

    init {
        val module: AbstractModule = object : AbstractModule() {
            override fun configure() {
                bind(Plugin::class.java).toInstance(this@DataSyncNukkit)
                bind(PluginBase::class.java).toInstance(this@DataSyncNukkit)
                bind(DataSyncNukkit::class.java).toInstance(this@DataSyncNukkit)
            }
        }
        val injector = Guice.createInjector(module)
        inner = Inner(injector)
    }

    private inner class Inner(rootInjector: Injector) : DataSyncImpl<String>(rootInjector, NukkitModule()) {
        override fun applyToBuilder(builder: Environment.Builder) {
            super.applyToBuilder(builder)
            builder.setLoggerSupplier(this@DataSyncNukkit::getLogger)
            builder.addEarlyServices(NukkitPlayerListener::class.java) { t: NukkitPlayerListener ->
                Server.getInstance().pluginManager.registerEvents(t, this@DataSyncNukkit)
            }
        }
    }
}
