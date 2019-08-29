package rocks.milspecsg.msdatasync.service.data;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import rocks.milspecsg.msdatasync.api.data.PlayerSerializer;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.service.config.ConfigKeys;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Singleton
public abstract class ApiPlayerSerializer<M extends Member, P, K, U> extends ApiSerializer<M, P, K, U> implements PlayerSerializer<M, P> {
    @Override
    public String getName() {
        return "PlayerSerializer";
    }

    //    private List<Provider<? extends Serializer<M, P>>> serializers;
//
//    public void addSerializer(Provider<? extends Serializer<M, P>> serializerProvider) {
//        serializers.add(serializerProvider);
//    }
//
//    public void clearSerializers() {
//        serializers.clear();
//    }

//    @Inject
//    private Provider<ExperienceSerializer<M, P>> experienceSerializerProvider;
//
//    @Inject
//    private Provider<GameModeSerializer<M, P>> gameModeSerializerProvider;
//
//    @Inject
//    private Provider<HealthSerializer<M, P>> healthSerializerProvider;
//
//    @Inject
//    private Provider<HungerSerializer<M, P>> hungerSerializerProvider;
//
//    @Inject
//    private Provider<InventorySerializer<M, P>> inventorySerializerProvider;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private Injector injector;

    public ApiPlayerSerializer() {
    }

    void loadConfig() {
        List<String> enabledSerializers = configurationService.getConfigList(ConfigKeys.ENABLED_SERIALIZERS, new TypeToken<List<String>>() {
        });
    }

    @Override
    public CompletableFuture<Boolean> serialize(M member, P player) {
//        serializers.forEach(provider -> provider.get().serialize(member, player));
    }

    @Override
    public CompletableFuture<Boolean> deserialize(M member, P player) {
//        serializers.forEach(provider -> provider.get().deserialize(member, player));
    }
}
