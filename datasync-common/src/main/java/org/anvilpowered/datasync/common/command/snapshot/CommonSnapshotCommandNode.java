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

package org.anvilpowered.datasync.common.command.snapshot;

import com.google.inject.Inject;
import org.anvilpowered.anvil.api.command.CommandNode;
import org.anvilpowered.anvil.api.command.CommandService;
import org.anvilpowered.anvil.api.registry.Registry;
import org.anvilpowered.datasync.common.plugin.DataSyncPluginInfo;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class CommonSnapshotCommandNode<TCommandExecutor, TCommandSource>
    implements CommandNode<TCommandSource> {

    protected static final List<String> CREATE_ALIAS = Arrays.asList("create", "c", "upload", "up");
    protected static final List<String> DELETE_ALIAS = Collections.singletonList("delete");
    protected static final List<String> VIEW_ALIAS = Arrays.asList("view", "e", "edit");
    protected static final List<String> INFO_ALIAS = Arrays.asList("info", "i");
    protected static final List<String> LIST_ALIAS = Arrays.asList("list", "l");
    protected static final List<String> RESTORE_ALIAS = Arrays.asList("restore", "r", "download", "down");
    protected static final List<String> HELP_ALIAS = Collections.singletonList("help");
    protected static final List<String> VERSION_ALIAS = Collections.singletonList("version");

    protected static final String CREATE_DESCRIPTION
        = "Creates a manual snapshot for user and uploads it to DB.";
    protected static final String DELETE_DESCRIPTION
        = "Deletes snapshot for user.";
    protected static final String VIEW_DESCRIPTION
        = "Edit/view snapshot for user from DB. If no date is provided, latest snapshot is selected.";
    protected static final String INFO_DESCRIPTION
        = "More info for snapshot for user from DB. If no date is provided, latest snapshot is selected.";
    protected static final String LIST_DESCRIPTION
        = "Lists available snapshots for user.";
    protected static final String RESTORE_DESCRIPTION
        = "Manually restores snapshot from DB. If no date is selected, latest snapshot is restored.";
    protected static final String HELP_DESCRIPTION
        = "Shows this help page.";
    protected static final String VERSION_DESCRIPTION
        = "Shows plugin version.";
    protected static final String ROOT_DESCRIPTION
        = "Snapshot base command.";

    protected static final String CREATE_USAGE = "<user>";
    protected static final String DELETE_USAGE = "<user> <snapshot>";
    protected static final String VIEW_USAGE = "<user> [<snapshot>]";
    protected static final String INFO_USAGE = "<user> [<snapshot>]";
    protected static final String RESTORE_USAGE = "<user> [<snapshot>]";
    protected static final String LIST_USAGE = "<user>";

    protected static final String HELP_COMMAND = "/sync snapshot|snap|s help";

    private boolean alreadyLoaded;
    protected Map<List<String>, Function<TCommandSource, String>> descriptions;
    protected Map<List<String>, Predicate<TCommandSource>> permissions;
    protected Map<List<String>, Function<TCommandSource, String>> usages;

    @Inject
    protected CommandService<TCommandExecutor, TCommandSource> commandService;

    protected Registry registry;

    protected CommonSnapshotCommandNode(Registry registry) {
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
        descriptions.put(CREATE_ALIAS, c -> CREATE_DESCRIPTION);
        descriptions.put(DELETE_ALIAS, c -> DELETE_DESCRIPTION);
        descriptions.put(VIEW_ALIAS, c -> VIEW_DESCRIPTION);
        descriptions.put(INFO_ALIAS, c -> INFO_DESCRIPTION);
        descriptions.put(RESTORE_ALIAS, c -> RESTORE_DESCRIPTION);
        descriptions.put(LIST_ALIAS, c -> LIST_DESCRIPTION);
        descriptions.put(HELP_ALIAS, c -> HELP_DESCRIPTION);
        descriptions.put(VERSION_ALIAS, c -> VERSION_DESCRIPTION);
        usages.put(CREATE_ALIAS, c -> CREATE_USAGE);
        usages.put(DELETE_ALIAS, c -> DELETE_USAGE);
        usages.put(VIEW_ALIAS, c -> VIEW_USAGE);
        usages.put(INFO_ALIAS, c -> INFO_USAGE);
        usages.put(RESTORE_ALIAS, c -> RESTORE_USAGE);
        usages.put(LIST_ALIAS, c -> LIST_USAGE);
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
