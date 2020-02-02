package rocks.milspecsg.msdatasync.common.data.registry;

import com.google.inject.Singleton;
import rocks.milspecsg.msrepository.api.data.key.Keys;
import rocks.milspecsg.msrepository.common.data.registry.CommonExtendedRegistry;

@Singleton
public class MSDataSyncRegistry extends CommonExtendedRegistry {

    public MSDataSyncRegistry() {
        defaultMap.put(Keys.BASE_SCAN_PACKAGE, "rocks.milspecsg.msdatasync.common.model");
        defaultMap.put(Keys.DATA_DIRECTORY, "msdatasync");
    }
}
