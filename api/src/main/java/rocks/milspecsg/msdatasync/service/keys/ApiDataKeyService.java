package rocks.milspecsg.msdatasync.service.keys;

import com.google.inject.Singleton;
import rocks.milspecsg.msdatasync.api.keys.DataKeyService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Singleton
public abstract class ApiDataKeyService<K> implements DataKeyService<K> {

    private Map<K, String> nameMap;

    public ApiDataKeyService() {
        nameMap = new HashMap<>();
    }

    @Override
    public void addMapping(K key, String name) {
        if (key != null && name != null) nameMap.put(key, name);
    }

    @Override
    public void removeMapping(K key) {
        nameMap.remove(key);
    }

    @Override
    public Optional<String> getName(K key) {
        return Optional.ofNullable(nameMap.get(key));
    }
}
