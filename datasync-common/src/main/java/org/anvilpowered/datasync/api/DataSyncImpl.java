package org.anvilpowered.datasync.api;

import com.google.common.reflect.TypeToken;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.anvilpowered.anvil.api.Environment;
import org.anvilpowered.datasync.api.keys.DataKeyService;
import org.anvilpowered.datasync.api.tasks.SerializationTaskService;
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
