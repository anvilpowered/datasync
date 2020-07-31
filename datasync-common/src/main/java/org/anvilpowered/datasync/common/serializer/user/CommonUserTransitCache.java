package org.anvilpowered.datasync.common.serializer.user;

import com.google.inject.Singleton;
import org.anvilpowered.datasync.api.serializer.user.UserTransitCache;

import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

@Singleton
public class CommonUserTransitCache implements UserTransitCache {

    private final Set<UUID> uuids;

    public CommonUserTransitCache() {
        uuids = new TreeSet<>();
    }

    @Override
    public void joinStart(UUID userUUID) {
        synchronized (uuids) {
            uuids.add(userUUID);
        }
    }

    @Override
    public void joinEnd(UUID userUUID) {
        synchronized (uuids) {
            uuids.remove(userUUID);
        }
    }

    @Override
    public boolean isJoining(UUID userUUID) {
        synchronized (uuids) {
            return uuids.contains(userUUID);
        }
    }
}
