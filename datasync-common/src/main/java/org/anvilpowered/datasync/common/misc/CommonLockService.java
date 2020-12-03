package org.anvilpowered.datasync.common.misc;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.anvilpowered.anvil.api.util.TextService;
import org.anvilpowered.anvil.api.util.UserService;
import org.anvilpowered.datasync.api.misc.LockService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Singleton
public class CommonLockService<
    TString,
    TUser,
    TPlayer,
    TCommandSource> implements LockService {

    @Inject
    private TextService<TString, TCommandSource> textService;

    @Inject
    private UserService<TUser, TPlayer> userService;

    private final List<UUID> unlockedPlayers = new ArrayList<>();

    @Override
    public boolean assertUnlocked(Object source) {
        if (!unlockedPlayers.contains(userService.getUUIDSafe(source))) {
            textService.builder()
                .appendPrefix()
                .yellow().append("You must first unlock this command with /sync lock off")
                .sendTo((TCommandSource) source);
            return false;
        }
        return true;
    }

    @Override
    public List<UUID> getUnlockedPlayers() {
        return unlockedPlayers;
    }

    @Override
    public void add(UUID userUUID) {
        unlockedPlayers.add(userUUID);
    }

    @Override
    public void remove(int index) {
        unlockedPlayers.remove(index);
    }
}
