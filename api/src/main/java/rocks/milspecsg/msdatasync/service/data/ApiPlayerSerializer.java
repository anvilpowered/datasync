package rocks.milspecsg.msdatasync.service.data;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import rocks.milspecsg.msdatasync.api.data.*;
import rocks.milspecsg.msdatasync.model.core.Member;
import rocks.milspecsg.msdatasync.service.config.ConfigKeys;
import rocks.milspecsg.msdatasync.utils.ApiUtils;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public abstract class ApiPlayerSerializer<M extends Member, P, K, U> extends ApiSerializer<M, P, K, U> implements PlayerSerializer<M, P> {

    @Override
    public String getName() {
        return "PlayerSerializer";
    }

    private List<Serializer<M, P>> serializers;
//
//    public void addSerializer(Provider<? extends Serializer<M, P>> serializerProvider) {
//        serializers.add(serializerProvider);
//    }
//
//    public void clearSerializers() {
//        serializers.clear();
//    }

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

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private Injector injector;

    public ApiPlayerSerializer() {
    }

    public void loadConfig() {
//        List<String> enabledSerializers = configurationService.getConfigList(ConfigKeys.ENABLED_SERIALIZERS_LIST, new TypeToken<List<String>>() {
//        });
        serializers = new ArrayList<>();
        serializers.add(experienceSerializerProvider.get());
        serializers.add(gameModeSerializerProvider.get());
        serializers.add(healthSerializerProvider.get());
        serializers.add(hungerSerializerProvider.get());
        serializers.add(inventorySerializerProvider.get());
    }

    @Override
    public CompletableFuture<Boolean> serialize(M member, P player) {
        if (serializers.isEmpty()) {
            System.err.println("[MSDataSync] No enabled serializers");
            return CompletableFuture.completedFuture(true);
        }
//        return ApiUtils.combineTasks(serializers.stream().map(serializer -> serializer.serialize(member, player)))
//            // Reduce to single boolean with (a,b) -> a && b accumulator. Only true of all values in stream are true.
//            // Only true when ALL serializers successfully ran
//            .thenApplyAsync(stream -> {
//                System.out.println("Tasks finished");
//                return stream.reduce(true, (a, b) -> a && b);
//            }).thenApplyAsync(result -> {
//                if (result) {
//                    System.out.println("successful serialization: " + member.getId());
//                    return memberRepository.insertOne(member).join().isPresent();
//                } else {
//                    System.out.println("unsuccessful serialization: " + member.getId());
//                    return false;
//                }
//            });

        return CompletableFuture.supplyAsync(() -> {
            for (Serializer<M, P> serializer : serializers) {
                if (!serializer.serialize(member, player).join()) {
                    System.err.println("[MSDataSync] Serialization FAILED for player uuid " + member.userUUID + " : " + serializer.getName());
                    return false;
                } else {
                }
            }

            return memberRepository.insertOne(member).join().isPresent();
        });
    }

    @Override
    public CompletableFuture<Boolean> deserialize(M member, P player) {
        if (serializers.isEmpty()) {
            System.err.println("[MSDataSync] No enabled deserializers");
            return CompletableFuture.completedFuture(true);
        }
        return CompletableFuture.supplyAsync(() -> {
            for (Serializer<M, P> serializer : serializers) {
                if (!serializer.deserialize(member, player).join()) {
                    System.err.println("[MSDataSync] Deserialization FAILED for player uuid " + member.userUUID + " : " + serializer.getName());
                    return false;
                }
            }
            return true;
        });
    }
}
