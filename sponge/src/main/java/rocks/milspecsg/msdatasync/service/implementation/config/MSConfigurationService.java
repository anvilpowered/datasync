package rocks.milspecsg.msdatasync.service.implementation.config;

import com.google.common.reflect.TypeToken;
import rocks.milspecsg.msdatasync.service.config.ConfigKeys;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.config.DefaultConfig;
import rocks.milspecsg.msrepository.service.config.ApiConfigurationService;

import javax.inject.Inject;
import java.util.*;

public class MSConfigurationService extends ApiConfigurationService {

    @Inject
    public MSConfigurationService(@DefaultConfig(sharedRoot = false) ConfigurationLoader<CommentedConfigurationNode> configLoader) {
        super(configLoader);
    }

    @Override
    protected void initNodeTypeMap() {
        nodeTypeMap.put(ConfigKeys.ENABLED_SERIALIZERS, new TypeToken<List<String>>() {
        });
    }

    @Override
    protected void initVerificationMaps() {

//        Map<Predicate<List<?>>, Function<List<?>, List<?>>> enabledSerializersVerificationMap = new HashMap<>();
//
//        // check for duplicates
//        enabledSerializersVerificationMap.put(list -> {
//            if (list.size() > 0) {
//
//            }
//            },
//            // remove duplicates
//            list -> {
//
//            return list;
//            });
//
//        listVerificationMap.put(ConfigKeys.ENABLED_SERIALIZERS, enabledSerializersVerificationMap);

    }

    @Override
    protected void initDefaultMaps() {
        defaultListMap.put(ConfigKeys.ENABLED_SERIALIZERS, Arrays.asList("Experience", "GameMode", "Health", "Hunger", "Inventory"));
    }

    @Override
    protected void initNodeNameMap() {
        nodeNameMap.put(ConfigKeys.ENABLED_SERIALIZERS, "enabledSerializers");
    }

    @Override
    protected void initNodeDescriptionMap() {
        nodeDescriptionMap.put(ConfigKeys.ENABLED_SERIALIZERS, "\nThings to sync to DB." +
            "\nAvailable: Experience, GameMode, Health, Hunger, Inventory");
    }
}
