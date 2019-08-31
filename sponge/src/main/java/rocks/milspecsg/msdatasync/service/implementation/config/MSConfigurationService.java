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
        nodeTypeMap.put(ConfigKeys.ENABLED_SERIALIZERS_LIST, new TypeToken<List<String>>() {});

        nodeTypeMap.put(ConfigKeys.SERIALIZE_ON_JOIN_LEAVE, new TypeToken<Boolean>() {});

        nodeTypeMap.put(ConfigKeys.SERIALIZATION_TASK_INTERVAL_SECONDS, new TypeToken<Integer>() {});
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
//        listVerificationMap.put(ConfigKeys.ENABLED_SERIALIZERS_LIST, enabledSerializersVerificationMap);

    }

    @Override
    protected void initDefaultMaps() {
        defaultListMap.put(ConfigKeys.ENABLED_SERIALIZERS_LIST, Arrays.asList("Experience", "GameMode", "Health", "Hunger", "Inventory"));

        defaultBooleanMap.put(ConfigKeys.SERIALIZE_ON_JOIN_LEAVE, true);

        defaultIntegerMap.put(ConfigKeys.SERIALIZATION_TASK_INTERVAL_SECONDS, 300);
    }

    @Override
    protected void initNodeNameMap() {
        nodeNameMap.put(ConfigKeys.ENABLED_SERIALIZERS_LIST, "enabledSerializers");

        nodeNameMap.put(ConfigKeys.SERIALIZE_ON_JOIN_LEAVE, "serializeOnJoinLeave");

        nodeNameMap.put(ConfigKeys.SERIALIZATION_TASK_INTERVAL_SECONDS, "serializationTaskIntervalSeconds");
    }

    @Override
    protected void initNodeDescriptionMap() {
        nodeDescriptionMap.put(ConfigKeys.ENABLED_SERIALIZERS_LIST, "\nThings to sync to DB." +
            "\nAvailable: Experience, GameMode, Health, Hunger, Inventory");

        nodeDescriptionMap.put(ConfigKeys.SERIALIZE_ON_JOIN_LEAVE, "\nWhether MSDataSync should sync players to DB on join/leave");

        nodeDescriptionMap.put(ConfigKeys.SERIALIZATION_TASK_INTERVAL_SECONDS, "\nInterval for automatic serialization task");
    }
}
