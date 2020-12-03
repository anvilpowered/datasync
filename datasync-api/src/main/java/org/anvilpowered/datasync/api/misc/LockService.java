package org.anvilpowered.datasync.api.misc;

import java.util.List;
import java.util.UUID;

public interface LockService {

    boolean assertUnlocked(Object source);

    List<UUID> getUnlockedPlayers();

    void add(UUID userUUID);

    void remove(int index);
}
