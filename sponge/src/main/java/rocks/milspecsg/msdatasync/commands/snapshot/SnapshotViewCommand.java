/*
 *     MSDataSync - MilSpecSG
 *     Copyright (C) 2019 Cableguy20
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package rocks.milspecsg.msdatasync.commands.snapshot;

import com.google.inject.Inject;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.property.InventoryCapacity;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import rocks.milspecsg.msdatasync.MSDataSync;
import rocks.milspecsg.msdatasync.MSDataSyncPluginInfo;
import rocks.milspecsg.msdatasync.PluginPermissions;
import rocks.milspecsg.msdatasync.api.serializer.InventorySerializer;
import rocks.milspecsg.msdatasync.api.serializer.SnapshotSerializer;
import rocks.milspecsg.msdatasync.api.misc.DateFormatService;
import rocks.milspecsg.msdatasync.api.snapshot.SnapshotManager;
import rocks.milspecsg.msdatasync.commands.SyncLockCommand;
import rocks.milspecsg.msdatasync.misc.CommandUtils;
import rocks.milspecsg.msdatasync.model.core.snapshot.Snapshot;

import java.util.Optional;
import java.util.function.Consumer;

public class SnapshotViewCommand implements CommandExecutor {

    @Inject
    private SnapshotManager<Snapshot<?>, Key<?>> snapshotRepository;

    @Inject
    private SnapshotSerializer<Snapshot<?>, User> snapshotSerializer;

    @Inject
    private InventorySerializer<Snapshot<?>, User, Inventory, ItemStackSnapshot> inventorySerializer;

    @Inject
    private DateFormatService dateFormatService;

    @Inject
    private CommandUtils commandUtils;

    private static InventoryArchetype inventoryArchetype =
        InventoryArchetype.builder()
            .with(InventoryArchetypes.CHEST).property(new InventoryCapacity(45))
            .build("msdatasyncinv", "MSDataSync Inventory");

    @Override
    public CommandResult execute(CommandSource source, CommandContext context) throws CommandException {

        if (source instanceof Player) {

            SyncLockCommand.assertUnlocked(source);

            if (!snapshotSerializer.isSerializerEnabled("msdatasync:inventory")) {
                throw new CommandException(Text.of(MSDataSyncPluginInfo.pluginPrefix, "Inventory serializer must be enabled in config to use this feature"));
            }

            Player player = (Player) source;
            Optional<User> optionalUser = context.getOne(Text.of("user"));
            if (!optionalUser.isPresent()) {
                throw new CommandException(Text.of(MSDataSyncPluginInfo.pluginPrefix, "User is required"));
            }
            User targetUser = optionalUser.get();

            Consumer<Optional<Snapshot<?>>> afterFound = optionalSnapshot -> {
                if (!optionalSnapshot.isPresent()) {
                    source.sendMessage(Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.RED, "Could not find snapshot for user " + targetUser.getName()));
                    return;
                }
                Snapshot<?> snapshot = optionalSnapshot.get();
                source.sendMessage(Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.YELLOW, "Editing snapshot ", TextColors.GOLD, dateFormatService.format(optionalSnapshot.get().getCreatedUtcDate())));
                final boolean[] closeData = {false};
                final boolean permissionToEdit = player.hasPermission(PluginPermissions.SNAPSHOT_VIEW_EDIT);
                Inventory inventory = Inventory.builder().of(inventoryArchetype).listener(InteractInventoryEvent.Close.class, e -> {
                    if (closeData[0] || !permissionToEdit) {
                        source.sendMessage(
                            Text.of(
                                MSDataSyncPluginInfo.pluginPrefix, TextColors.YELLOW,
                                "Closed snapshot ", TextColors.GOLD,
                                dateFormatService.format(optionalSnapshot.get().getCreatedUtcDate()),
                                TextColors.YELLOW, " without saving"
                            )
                        );
                        return;
                    }
                    // wait until player closes inventory
                    inventorySerializer.serializeInventory(snapshot, e.getTargetInventory());
                    snapshotRepository.getPrimaryComponent().insertOne(snapshot).thenAcceptAsync(optionalS -> {
                        if (optionalS.isPresent()) {
                            source.sendMessage(
                                Text.of(
                                    MSDataSyncPluginInfo.pluginPrefix, TextColors.YELLOW,
                                    "Successfully edited snapshot ", TextColors.GOLD,
                                    dateFormatService.format(optionalSnapshot.get().getCreatedUtcDate()),
                                    TextColors.YELLOW, " for user ", optionalUser.get().getName()
                                )
                            );
                        } else {
                            source.sendMessage(Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.RED, "An error occurred while serializing user ", optionalUser.get().getName()));
                        }
                    });
                }).listener(ClickInventoryEvent.class, e -> {
                    if (!permissionToEdit) {
                        e.setCancelled(true);
                    }

                    // prevent touching the barriers at the end
                    if (e.getTransactions().stream().anyMatch(slotTransaction -> slotTransaction.getOriginal().createStack().equalTo(inventorySerializer.getDefaultFallbackItemStackSnapshot().createStack()))) {
                        e.setCancelled(true);
                    }

                    if (e.getTransactions().stream().anyMatch(slotTransaction -> slotTransaction.getOriginal().createStack().equalTo(inventorySerializer.getExitWithoutSavingItemStackSnapshot().createStack()))) {
                        e.setCancelled(true);
                        closeData[0] = true;
                        player.closeInventory();
                    }

                }).build(MSDataSync.plugin);
                inventorySerializer.deserializeInventory(snapshot, inventory);
                player.openInventory(inventory, Text.of(TextColors.DARK_AQUA, dateFormatService.format(snapshot.getCreatedUtcDate())));
            };

            commandUtils.parseDateOrGetLatest(source, context, targetUser, afterFound);

            return CommandResult.success();
        } else {
            throw new CommandException(Text.of(MSDataSyncPluginInfo.pluginPrefix, "Can only be run as a player"));
        }
    }
}
