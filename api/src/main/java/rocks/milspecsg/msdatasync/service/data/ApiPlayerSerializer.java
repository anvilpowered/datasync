package rocks.milspecsg.msdatasync.service.data;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Provider;
import rocks.milspecsg.msdatasync.api.data.*;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.service.config.ConfigKeys;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class ApiPlayerSerializer<M extends Member, P, K, U> extends ApiSerializer<M, P, K, U> implements PlayerSerializer<M, P> {

    @Override
    public String getName() {
        return "PlayerSerializer";
    }

    private List<Serializer<M, P>> serializers;

    @Inject
    private Provider<ExperienceSerializer<M, P>> experienceSerializerProvider;

    @Inject
    private Provider<GameModeSerializer<M, P>> gameModeSerializerProvider;

    @Inject
    private Provider<HealthSerializer<M, P>> healthSerializerProvider;

    @Inject
    private Provider<HungerSerializer<M, P>> hungerSerializerProvider;

    @Inject
    private Provider<InventorySerializer<M, P>> inventorySerializerProvider;

    private ConfigurationService configurationService;

    @Inject
    public ApiPlayerSerializer(ConfigurationService configurationService) {
        this.configurationService = configurationService;
        configurationService.addConfigLoadedListener(this::loadConfig);
    }

    private void loadConfig() {
        List<String> enabledSerializers = configurationService.getConfigList(ConfigKeys.ENABLED_SERIALIZERS_LIST, new TypeToken<List<String>>() {});
        serializers = new ArrayList<>();

        if (enabledSerializers.contains("Experience")) {
            announceEnabled("Experience");
            serializers.add(experienceSerializerProvider.get());
        }

        if (enabledSerializers.contains("GameMode")) {
            announceEnabled("GameMode");
            serializers.add(gameModeSerializerProvider.get());
        }

        if (enabledSerializers.contains("Health")) {
            announceEnabled("Health");
            serializers.add(healthSerializerProvider.get());
        }

        if (enabledSerializers.contains("Hunger")) {
            announceEnabled("Hunger");
            serializers.add(hungerSerializerProvider.get());
        }

        if (enabledSerializers.contains("Inventory")) {
            announceEnabled("Inventory");
            serializers.add(inventorySerializerProvider.get());
        }
    }

    protected abstract void announceEnabled(String name);

    @Override
    public CompletableFuture<Boolean> serialize(M member, P player, Object plugin) {
        if (serializers.isEmpty()) {
            System.err.println("[MSDataSync] No enabled serializers");
            return CompletableFuture.completedFuture(true);
        }
        return CompletableFuture.supplyAsync(() -> {
            for (Serializer<M, P> serializer : serializers) {
                if (!serializer.serialize(member, player, plugin).join()) {
                    System.err.println("[MSDataSync] Serialization FAILED for player uuid " + member.userUUID + " : " + serializer.getName());
                    return false;
                }
            }
            return memberRepository.insertOne(member).join().isPresent();
        });
    }

    @Override
    public CompletableFuture<Boolean> deserialize(M member, P player, Object plugin) {
        if (serializers.isEmpty()) {
            System.err.println("[MSDataSync] No enabled deserializers");
            return CompletableFuture.completedFuture(true);
        }
        return CompletableFuture.supplyAsync(() -> {
            for (Serializer<M, P> serializer : serializers) {
                if (!serializer.deserialize(member, player, plugin).join()) {
                    System.err.println("[MSDataSync] Deserialization FAILED for player uuid " + member.userUUID + " : " + serializer.getName());
                    return false;
                }
            }
            return true;
        });
    }
}
