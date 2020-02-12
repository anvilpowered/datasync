/*
 *   DataSync - AnvilPowered
 *   Copyright (C) 2020 Cableguy20
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

package org.anvilpowered.datasync.sponge.command.snapshot;

import com.google.inject.Inject;
import org.anvilpowered.anvil.api.plugin.PluginInfo;
import org.anvilpowered.anvil.api.util.TimeFormatService;
import org.anvilpowered.datasync.api.data.key.DataSyncKeys;
import org.anvilpowered.datasync.api.member.MemberManager;
import org.anvilpowered.datasync.api.model.snapshot.Snapshot;
import org.anvilpowered.datasync.api.serializer.InventorySerializer;
import org.anvilpowered.datasync.api.serializer.SnapshotSerializer;
import org.anvilpowered.datasync.api.snapshot.SnapshotManager;
import org.anvilpowered.datasync.sponge.command.SpongeSyncLockCommand;
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
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;
import java.util.function.Consumer;

public class SpongeSnapshotViewCommand implements CommandExecutor {

    @Inject
    private MemberManager<Text> memberManager;

    @Inject
    private SnapshotManager<Key<?>> snapshotRepository;

    @Inject
    private SnapshotSerializer<User> snapshotSerializer;

    @Inject
    private InventorySerializer<User, Inventory, ItemStackSnapshot> inventorySerializer;

    @Inject
    private TimeFormatService timeFormatService;

    @Inject
    private PluginContainer pluginContainer;

    @Inject
    private PluginInfo<Text> pluginInfo;

    private static final InventoryArchetype inventoryArchetype =
        InventoryArchetype.builder()
            .with(InventoryArchetypes.CHEST).property(new InventoryCapacity(45))
            .build("datasyncinv", "DataSync Inventory");

    @Override
    public CommandResult execute(CommandSource source, CommandContext context) throws CommandException {

        if (source instanceof Player) {

            SpongeSyncLockCommand.assertUnlocked(source);

            if (!snapshotSerializer.isSerializerEnabled("datasync:inventory")) {
                throw new CommandException(Text.of(pluginInfo.getPrefix(), "Inventory serializer must be enabled in config to use this feature"));
            }

            Player player = (Player) source;
            Optional<User> optionalUser = context.getOne(Text.of("user"));
            if (!optionalUser.isPresent()) {
                throw new CommandException(Text.of(pluginInfo.getPrefix(), "User is required"));
            }
            User targetUser = optionalUser.get();

            Consumer<Optional<? extends Snapshot<?>>> afterFound = optionalSnapshot -> {
                if (!optionalSnapshot.isPresent()) {
                    source.sendMessage(Text.of(pluginInfo.getPrefix(), TextColors.RED, "Could not find snapshot for " + targetUser.getName()));
                    return;
                }
                Snapshot<?> snapshot = optionalSnapshot.get();
                source.sendMessage(Text.of(pluginInfo.getPrefix(), TextColors.YELLOW,
                    "Editing snapshot ", TextColors.GOLD,
                    timeFormatService.format(optionalSnapshot.get().getCreatedUtc())
                ));
                final boolean[] closeData = {false};
                final boolean permissionToEdit = player.hasPermission(DataSyncKeys.SNAPSHOT_VIEW_EDIT_PERMISSION.getFallbackValue());
                Inventory inventory = Inventory.builder().of(inventoryArchetype).listener(InteractInventoryEvent.Close.class, e -> {
                    if (closeData[0] || !permissionToEdit) {
                        source.sendMessage(
                            Text.of(
                                pluginInfo.getPrefix(), TextColors.YELLOW,
                                "Closed snapshot ", TextColors.GOLD,
                                timeFormatService.format(optionalSnapshot.get().getCreatedUtc()),
                                TextColors.YELLOW, " without saving"
                            )
                        );
                        return;
                    }
                    // wait until player closes inventory
                    inventorySerializer.serializeInventory(snapshot, e.getTargetInventory());
                    snapshotRepository.getPrimaryComponent().parseAndSetInventory(snapshot.getId(), snapshot.getInventory()).thenAcceptAsync(b -> {
                        if (b) {
                            source.sendMessage(
                                Text.of(
                                    pluginInfo.getPrefix(), TextColors.YELLOW,
                                    "Successfully edited snapshot ", TextColors.GOLD,
                                    timeFormatService.format(optionalSnapshot.get().getCreatedUtc()),
                                    TextColors.YELLOW, " for ", targetUser.getName()
                                )
                            );
                        } else {
                            source.sendMessage(Text.of(pluginInfo.getPrefix(), TextColors.RED, "An error occurred while serializing user ", targetUser.getName()));
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

                }).build(pluginContainer);
                inventorySerializer.deserializeInventory(snapshot, inventory);
                player.openInventory(inventory, Text.of(TextColors.DARK_AQUA,
                    timeFormatService.format(snapshot.getCreatedUtc()).toString()));
            };

            memberManager.getPrimaryComponent().getSnapshotForUser(
                targetUser.getUniqueId(),
                context.<String>getOne(Text.of("snapshot")).orElse(null)
            ).thenAcceptAsync(optionalSnapshot ->
                Task.builder().execute(() ->
                    afterFound.accept(optionalSnapshot)).submit(pluginContainer));

            return CommandResult.success();
        } else {
            throw new CommandException(Text.of(pluginInfo.getPrefix(), "Can only be run as a player"));
        }
    }
}
