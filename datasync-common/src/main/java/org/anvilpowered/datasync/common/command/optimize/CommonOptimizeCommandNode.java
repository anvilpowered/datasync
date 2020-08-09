/*
 *   DataSync - AnvilPowered
 *   Copyright (C) 2020 Cableguy20
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.datasync.common.command.optimize;

import com.google.inject.Inject;
import org.anvilpowered.anvil.api.command.CommandNode;
import org.anvilpowered.anvil.api.command.CommandService;
import org.anvilpowered.anvil.api.registry.Registry;
import org.anvilpowered.datasync.common.command.CommonSyncCommandNode;
import org.anvilpowered.datasync.common.plugin.DataSyncPluginInfo;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class CommonOptimizeCommandNode<TCommandExecutor, TCommandSource>
    implements CommandNode<TCommandSource> {

    protected static final List<String> START_ALIAS = Arrays.asList("start", "s");
    protected static final List<String> INFO_ALIAS = Arrays.asList("info", "i");
    protected static final List<String> STOP_ALIAS = Collections.singletonList("stop");
    protected static final List<String> HELP_ALIAS = Collections.singletonList("help");

    protected static final String START_DESCRIPTION
        = "Starts manual optimization, deletes old snapshots.";
    protected static final String INFO_DESCRIPTION
        = "Gets info on current manual optimization.";
    protected static final String STOP_DESCRIPTION
        = "Stops current manual optimization.";
    protected static final String HELP_DESCRIPTION
        = "Shows this help page.";
    protected static final String ROOT_DESCRIPTION
        = "Optimize base command. (To delete old snapshots)";

    protected static final String START_USAGE = "all|user [<user>]";

    protected static final String HELP_COMMAND = "/sync optimize help";

    private boolean alreadyLoaded;
    protected Map<List<String>, Function<TCommandSource, String>> descriptions;
    protected Map<List<String>, Predicate<TCommandSource>> permissions;
    protected Map<List<String>, Function<TCommandSource, String>> usages;

    @Inject
    protected CommandService<TCommandExecutor, TCommandSource> commandService;

    protected Registry registry;

    protected CommonOptimizeCommandNode(Registry registry) {
        this.registry = registry;
        registry.whenLoaded(() -> {
            if (alreadyLoaded) return;
            loadCommands();
            alreadyLoaded = true;
        }).register();
        alreadyLoaded = false;
        descriptions = new HashMap<>();
        permissions = new HashMap<>();
        usages = new HashMap<>();
        descriptions.put(START_ALIAS, c -> START_DESCRIPTION);
        descriptions.put(INFO_ALIAS, c -> INFO_DESCRIPTION);
        descriptions.put(STOP_ALIAS, c -> STOP_DESCRIPTION);
        descriptions.put(HELP_ALIAS, c -> HELP_DESCRIPTION);
        usages.put(START_ALIAS, c -> START_USAGE);
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

    @Override
    public String[] getPath() {
        return CommonSyncCommandNode.SYNC_PATH;
    }
}
