package org.anvilpowered.datasync.api.serializer.user;

import java.util.UUID;

/**
 * All methods are thread-safe
 */
public interface UserTransitCache {

    /**
     * Declares that the user with the provided {@link UUID} has started the joining process.
     *
     * @param userUUID The {@link UUID} of the joining user
     */
    void joinStart(UUID userUUID);

    /**
     * Declares that the user with the provided {@link UUID} has ended the joining process.
     *
     * @param userUUID The {@link UUID} of the joining user
     */
    void joinEnd(UUID userUUID);

    /**
     * Check whether the user with the provided {@link UUID} is in the process of joining.
     *
     * @param userUUID The {@link UUID} of the user to check
     * @return Whether the user with the provided {@link UUID} is in the process of joining
     */
    boolean isJoining(UUID userUUID);
}
