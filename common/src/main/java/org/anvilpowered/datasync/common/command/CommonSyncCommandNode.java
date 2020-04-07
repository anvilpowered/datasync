package org.anvilpowered.datasync.common.command;

import com.google.inject.Inject;
import org.anvilpowered.anvil.api.Environment;
import org.anvilpowered.anvil.api.command.CommandNode;
import org.anvilpowered.anvil.api.command.CommandService;
import org.anvilpowered.anvil.api.data.registry.Registry;
import org.anvilpowered.datasync.common.plugin.DataSyncPluginInfo;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class CommonSyncCommandNode<TCommandExecutor, TCommandSource>
    implements CommandNode<TCommandSource> {

    protected static final List<String> LOCK_ALIAS = Arrays.asList("lock", "l");
    protected static final List<String> RELOAD_ALIAS = Collections.singletonList("reload");
    protected static final List<String> UPLOAD_ALIAS = Arrays.asList("upload", "up");
    protected static final List<String> HELP_ALIAS = Collections.singletonList("help");
    protected static final List<String> VERSION_ALIAS = Collections.singletonList("version");

    protected static final String LOCK_DESCRIPTION = "Lock / Unlock sensitive commands.";
    protected static final String RELOAD_DESCRIPTION = "Reloads DataSync.";
    protected static final String UPLOAD_DESCRIPTION = "Uploads all players on server.";
    protected static final String HELP_DESCRIPTION = "Shows this help page.";
    protected static final String VERSION_DESCRIPTION = "Shows plugin version.";
    protected static final String ROOT_DESCRIPTION
        = String.format("%s root command", DataSyncPluginInfo.name);

    protected static final String RELOAD_USAGE = "[-a|--all|-r|--regex] [plugin]";

    protected static final String HELP_COMMAND = "/datasync help";

    public static String[] SYNC_PATH = new String[]{"sync"};

    private boolean alreadyLoaded;
    protected Map<List<String>, Function<TCommandSource, String>> descriptions;
    protected Map<List<String>, Predicate<TCommandSource>> permissions;
    protected Map<List<String>, Function<TCommandSource, String>> usages;

    @Inject
    protected CommandService<TCommandExecutor, TCommandSource> commandService;

    @Inject
    protected Environment environment;

    protected Registry registry;

    protected CommonSyncCommandNode(Registry registry) {
        this.registry = registry;
        registry.whenLoaded(() -> {
            if (alreadyLoaded) return;
            loadCommands();
            alreadyLoaded = true;
        });
        alreadyLoaded = false;
        descriptions = new HashMap<>();
        permissions = new HashMap<>();
        usages = new HashMap<>();
        descriptions.put(HELP_ALIAS, c -> HELP_DESCRIPTION);
        descriptions.put(VERSION_ALIAS, c -> VERSION_DESCRIPTION);
        usages.put(RELOAD_ALIAS, c -> RELOAD_USAGE);
    }

    protected abstract void loadCommands();

    private static final String ERROR_MESSAGE = "Sync command has not been loaded yet";

    @Override
    public Map<List<String>, Function<TCommandSource, String>> getDescriptions() {
        return Objects.requireNonNull(descriptions, ERROR_MESSAGE);
    }

    @Override
    public Map<List<String>, Predicate<TCommandSource>> getPermissions() {
        return Objects.requireNonNull(permissions, ERROR_MESSAGE);
    }

    @Override
    public Map<List<String>, Function<TCommandSource, String>> getUsages() {
        return Objects.requireNonNull(usages, ERROR_MESSAGE);
    }

    @Override
    public String getName() {
        return DataSyncPluginInfo.id;
    }
}
