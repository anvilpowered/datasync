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
