package rocks.milspecsg.msdatasync.service.data;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Provider;
import rocks.milspecsg.msdatasync.api.data.*;
import rocks.milspecsg.msdatasync.model.core.Snapshot;
import rocks.milspecsg.msdatasync.service.config.ConfigKeys;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;

import java.util.ArrayList;
import java.util.List;

/**
 * @param <S> {@link Snapshot} or subclass. Default implementation by MSDataSync as {@link Snapshot}
 * @param <U> Player class to get data from
 * @param <K> Key class
 */
public abstract class ApiSnapshotSerializer<S extends Snapshot, K, U, I, F> extends ApiSerializer<S, K, U> implements SnapshotSerializer<S, U> {

    @Override
    public String getName() {
        return "msdatasync:snapshot";
    }

    private List<Serializer<S, U>> serializers;

    private List<Serializer<S, U>> externalSerializers;

    @Inject
    private Provider<ExperienceSerializer<S, U>> experienceSerializerProvider;

    @Inject
    private Provider<GameModeSerializer<S, U>> gameModeSerializerProvider;

    @Inject
    private Provider<HealthSerializer<S, U>> healthSerializerProvider;

    @Inject
    private Provider<HungerSerializer<S, U>> hungerSerializerProvider;

    @Inject
    private Provider<InventorySerializer<S, U, I, F>> inventorySerializerProvider;

    private ConfigurationService configurationService;

    @Inject
    public ApiSnapshotSerializer(ConfigurationService configurationService) {
        this.configurationService = configurationService;
        configurationService.addConfigLoadedListener(this::loadConfig);
        externalSerializers = new ArrayList<>();
    }

    @Override
    public boolean isSerializerEnabled(String name) {
        return serializers.stream().anyMatch(serializer -> serializer.getName().equals(name));
    }

    @Override
    public void registerSerializer(Serializer<S, U> serializer) {
        externalSerializers.add(serializer);
    }

    private void loadConfig(Object plugin) {

        postLoadedEvent(plugin);

        verifyExternalSerializers();

        List<String> enabledSerializers = new ArrayList<>(configurationService.getConfigList(ConfigKeys.ENABLED_SERIALIZERS_LIST, new TypeToken<List<String>>() {
        }));
        serializers = new ArrayList<>();

        if (enabledSerializers.remove("msdatasync:experience")) {
            announceEnabled("msdatasync:experience");
            serializers.add(experienceSerializerProvider.get());
        }

        if (enabledSerializers.remove("msdatasync:gameMode")) {
            announceEnabled("msdatasync:gameMode");
            serializers.add(gameModeSerializerProvider.get());
        }

        if (enabledSerializers.remove("msdatasync:health")) {
            announceEnabled("msdatasync:health");
            serializers.add(healthSerializerProvider.get());
        }

        if (enabledSerializers.remove("msdatasync:hunger")) {
            announceEnabled("msdatasync:hunger");
            serializers.add(hungerSerializerProvider.get());
        }

        if (enabledSerializers.remove("msdatasync:inventory")) {
            announceEnabled("msdatasync:inventory");
            serializers.add(inventorySerializerProvider.get());
        }

        externalSerializers.stream()
            .filter(serializer -> enabledSerializers.remove(serializer.getName()))
            .forEach(serializer -> serializers.add(serializer));
    }

    protected abstract void postLoadedEvent(Object plugin);

    private void verifyExternalSerializers() {
        List<Serializer<S, U>> externalSerializersToRemove = new ArrayList<>();
        externalSerializers.forEach(serializer -> {
            String name = serializer.getName();
            if (name.startsWith("msdatasync") || name.split(":").length != 2) {
                System.err.println("[MSDataSync] External serialization module name \"" + serializer.getName() + "\" is invalid. Ignoring!");
                externalSerializersToRemove.add(serializer);
            }
        });

        externalSerializersToRemove.forEach(externalSerializers::remove);
    }

    protected abstract void announceEnabled(String name);

    protected abstract String getUsername(U user);

    @Override
    public boolean serialize(S snapshot, U user) {
        if (serializers.isEmpty()) {
            System.err.println("[MSDataSync] No enabled serializers");
            return false;
        }
        boolean success = true;
        if (snapshot.modulesUsed == null) {
            snapshot.modulesUsed = new ArrayList<>();
        }

        if (snapshot.modulesFailed == null) {
            snapshot.modulesFailed = new ArrayList<>();
        }

        for (Serializer<S, U> serializer : serializers) {
            // will still try to keep going even if one module fails
            try {
                snapshot.modulesUsed.add(serializer.getName());
                if (!serializer.serialize(snapshot, user)) {
                    System.err.println("[MSDataSync] Serialization module \"" + serializer.getName() + "\" failed for user " + getUsername(user) +"! All valid data was still uploaded!");
                    success = false;
                    snapshot.modulesFailed.add(serializer.getName());
                }
            } catch (Exception e) {
                success = false;
            }
        }
        return success;
    }

    @Override
    public boolean deserialize(S snapshot, U user) {
        if (serializers.isEmpty()) {
            System.err.println("[MSDataSync] No enabled deserializers");
            return false;
        }
        boolean success = true;
        List<String> serializersToUse = new ArrayList<>(snapshot.modulesUsed);
        for (Serializer<S, U> serializer : serializers) {
            // will still try to keep going even if one module fails
            try {
                if (serializersToUse.remove(serializer.getName())) {
                    // only use modules that were used to upload
                    if (!serializer.deserialize(snapshot, user)) {
                        System.err.println("[MSDataSync] Deserialization module \"" + serializer.getName() + "\" failed for snapshot " + snapshot.getId() + " for user " + getUsername(user));
                        success = false;
                    }
                } else {
                    System.err.println("[MSDataSync] Deserialization module \"" + serializer.getName() + "\" was not used in snapshot " + snapshot.getId() + " for user " + getUsername(user) + " but it is enabled in the config, skipping!");
                }
            } catch (Exception e) {
                success = false;
            }
        }

        serializersToUse.forEach(moduleName -> System.err.println(
            "[MSDataSync] Deserialization module \""
                + moduleName +
                "\" was used in snapshot "
                + snapshot.getId() +
                " but it is not enabled in the config! Not all data from snapshot could be added to user "
                + getUsername(user)
        ));
        return success;
    }
}
