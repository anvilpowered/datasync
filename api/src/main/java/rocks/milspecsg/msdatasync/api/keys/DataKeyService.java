package rocks.milspecsg.msdatasync.api.keys;

import java.util.Map;
import java.util.Optional;

public interface DataKeyService<K> {

    void addMapping(K key, String name);

    void removeMapping(K key);

    Optional<String> getName(K key);

    void initializeDefaultMappings();
}
