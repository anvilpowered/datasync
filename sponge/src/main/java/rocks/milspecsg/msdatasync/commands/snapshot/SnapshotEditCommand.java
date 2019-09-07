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
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.property.InventoryCapacity;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import rocks.milspecsg.msdatasync.MSDataSync;
import rocks.milspecsg.msdatasync.MSDataSyncPluginInfo;
import rocks.milspecsg.msdatasync.api.data.InventorySerializer;
import rocks.milspecsg.msdatasync.api.data.SnapshotSerializer;
import rocks.milspecsg.msdatasync.api.snapshot.SnapshotRepository;
import rocks.milspecsg.msdatasync.commands.SyncLockCommand;
import rocks.milspecsg.msdatasync.misc.CommandUtils;
import rocks.milspecsg.msdatasync.misc.DateFormatService;
import rocks.milspecsg.msdatasync.model.core.Snapshot;

import java.util.Optional;
import java.util.function.Consumer;

public class SnapshotEditCommand implements CommandExecutor {

    @Inject
    SnapshotRepository<Snapshot, Key> snapshotRepository;

    @Inject
    SnapshotSerializer<Snapshot, User> snapshotSerializer;

    @Inject
    InventorySerializer<Snapshot, User, Inventory> inventorySerializer;

    @Inject
    DateFormatService dateFormatService;

    @Inject
    CommandUtils commandUtils;

    private static InventoryArchetype inventoryArchetype =
        InventoryArchetype.builder()
            .with(InventoryArchetypes.CHEST).property(new InventoryCapacity(40))
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

            Consumer<Optional<Snapshot>> afterFound = optionalSnapshot -> {
                if (!optionalSnapshot.isPresent()) {
                    source.sendMessage(Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.RED, "Could not find snapshot for user " + targetUser.getName()));
                    return;
                }
                Snapshot snapshot = optionalSnapshot.get();
                source.sendMessage(Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.YELLOW, "Editing snapshot " + dateFormatService.format(optionalSnapshot.get().getId().getDate())));
                Inventory inventory = Inventory.builder().of(inventoryArchetype).listener(InteractInventoryEvent.Close.class, e -> {
                    // wait until player closes inventory
                    inventorySerializer.serializeInventory(snapshot, e.getTargetInventory());
                    snapshotRepository.insertOne(snapshot).thenAcceptAsync(optionalS -> {
                        if (optionalS.isPresent()) {
                            source.sendMessage(
                                Text.of(
                                    MSDataSyncPluginInfo.pluginPrefix, TextColors.YELLOW,
                                    "Successfully edited snapshot ", TextColors.GOLD,
                                    dateFormatService.format(optionalSnapshot.get().getId().getDate()),
                                    TextColors.YELLOW, " for user ", optionalUser.get().getName()
                                )
                            );
                        } else {
                            source.sendMessage(Text.of(MSDataSyncPluginInfo.pluginPrefix, TextColors.RED, "An error occurred while serializing user ", optionalUser.get().getName()));
                        }
                    });
                }).build(MSDataSync.plugin);
                inventorySerializer.deserializeInventory(snapshot, inventory);
                player.openInventory(inventory, Text.of(targetUser.getName(), " ", dateFormatService.format(snapshot.getId().getDate())));
            };

            commandUtils.parseDateOrGetLatest(source, context, targetUser, afterFound);

            return CommandResult.success();
        } else {
            throw new CommandException(Text.of(MSDataSyncPluginInfo.pluginPrefix, "Can only be run as a player"));
        }
    }
}
