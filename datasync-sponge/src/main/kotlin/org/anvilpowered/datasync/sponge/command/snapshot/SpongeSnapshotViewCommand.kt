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
package org.anvilpowered.datasync.sponge.command.snapshot

import com.google.inject.Inject
import org.anvilpowered.anvil.api.plugin.PluginInfo
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.anvil.api.splitContext
import org.anvilpowered.anvil.api.util.TextService
import org.anvilpowered.anvil.api.util.TimeFormatService
import org.anvilpowered.datasync.api.member.MemberManager
import org.anvilpowered.datasync.api.misc.LockService
import org.anvilpowered.datasync.api.model.snapshot.Snapshot
import org.anvilpowered.datasync.api.registry.DataSyncKeys
import org.anvilpowered.datasync.api.serializer.InventorySerializer
import org.anvilpowered.datasync.api.serializer.SnapshotSerializer
import org.anvilpowered.datasync.api.snapshot.SnapshotManager
import org.anvilpowered.datasync.common.command.snapshot.CommonSnapshotCommandNode
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandCallable
import org.spongepowered.api.command.CommandException
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.data.key.Key
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.living.player.User
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.InventoryArchetype
import org.spongepowered.api.item.inventory.InventoryArchetypes
import org.spongepowered.api.item.inventory.ItemStackSnapshot
import org.spongepowered.api.item.inventory.property.InventoryCapacity
import org.spongepowered.api.item.inventory.transaction.SlotTransaction
import org.spongepowered.api.plugin.PluginContainer
import org.spongepowered.api.scheduler.Task
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World
import java.util.Optional
import java.util.function.Consumer

class SpongeSnapshotViewCommand : CommandCallable {

    @Inject
    private lateinit var lockService: LockService

    @Inject
    private lateinit var memberManager: MemberManager<Text>

    @Inject
    private lateinit var registry: Registry

    @Inject
    private lateinit var snapshotRepository: SnapshotManager<Key<*>>

    @Inject
    private lateinit var snapshotSerializer: SnapshotSerializer<User>

    @Inject
    private lateinit var inventorySerializer: InventorySerializer<User, Inventory, ItemStackSnapshot>

    @Inject
    private lateinit var timeFormatService: TimeFormatService

    @Inject
    private lateinit var textService: TextService<Text, CommandSource>

    @Inject
    private lateinit var pluginContainer: PluginContainer

    @Inject
    private lateinit var pluginInfo: PluginInfo<Text>

    companion object {
        private val inventoryArchetype = InventoryArchetype.builder()
            .with(InventoryArchetypes.CHEST).property(InventoryCapacity(45))
            .build("datasyncinv", "DataSync Inventory")
        val DESCRIPTION: Optional<Text> = Optional.of(Text.of(CommonSnapshotCommandNode.VIEW_DESCRIPTION))
        val USAGE: Text = Text.of(CommonSnapshotCommandNode.VIEW_USAGE)
    }

    private val serializerDisabled: Text by lazy {
        textService.builder()
            .appendPrefix()
            .yellow().append("Inventory serializer must be enabled in the config to use this feature")
            .build()
    }

    private val lockedError: Text by lazy {
        textService.builder()
            .appendPrefix()
            .yellow().append("You must first unlock this command with /sync lock off")
            .build()
    }

    override fun process(source: CommandSource, arguments: String): CommandResult {
        val context: Array<String> = arguments.splitContext()
        return if (source is Player) {
            if (!snapshotSerializer.isSerializerEnabled("datasync:inventory")) {
                throw CommandException(serializerDisabled)
            }
            if (!lockService.assertUnlocked(source)) {
                throw CommandException(lockedError)
            }
            val target: String
            val snapshot: String?
            when {
                context.isEmpty() -> {
                    source.sendMessage(USAGE)
                    return CommandResult.success()
                }
                context.size == 1 -> {
                    target = context[0]
                    snapshot = null
                }
                else -> {
                    target = context[0]
                    snapshot = context[1]
                }
            }
            val optionalUser: Optional<Player> = Sponge.getServer().getPlayer(target)
            if (!optionalUser.isPresent) {
                throw CommandException(Text.of(pluginInfo.prefix, "User is required"))
            }
            val targetUser = optionalUser.get()
            val afterFound = Consumer { optionalSnapshot: Optional<Snapshot<*>> ->
                if (!optionalSnapshot.isPresent) {
                    source.sendMessage(Text.of(pluginInfo.prefix, TextColors.RED, "Could not find snapshot for " + targetUser.name))
                    return@Consumer
                }
                val snapshot = optionalSnapshot.get()
                source.sendMessage(Text.of(pluginInfo.prefix, TextColors.YELLOW,
                    "Editing snapshot ", TextColors.GOLD,
                    timeFormatService.format(optionalSnapshot.get().createdUtc)
                ))
                val closeData = booleanArrayOf(false)
                val permissionToEdit = source.hasPermission(DataSyncKeys.SNAPSHOT_VIEW_EDIT_PERMISSION.fallbackValue)
                val inventory = Inventory.builder().of(inventoryArchetype).listener(InteractInventoryEvent.Close::class.java) { e: InteractInventoryEvent.Close ->
                    if (closeData[0] || !permissionToEdit) {
                        source.sendMessage(
                            Text.of(
                                pluginInfo.prefix, TextColors.YELLOW,
                                "Closed snapshot ", TextColors.GOLD,
                                timeFormatService.format(snapshot.createdUtc),
                                TextColors.YELLOW, " without saving"
                            )
                        )
                        return@listener
                    }
                    // wait until player closes inventory
                    inventorySerializer.serializeInventory(snapshot, e.targetInventory)
                    snapshotRepository.primaryComponent.parseAndSetInventory(snapshot.id, snapshot.inventory).thenAcceptAsync { b: Boolean ->
                        if (b) {
                            source.sendMessage(
                                Text.of(
                                    pluginInfo.prefix, TextColors.YELLOW,
                                    "Successfully edited snapshot ", TextColors.GOLD,
                                    timeFormatService.format(snapshot.createdUtc),
                                    TextColors.YELLOW, " for ", targetUser.name
                                )
                            )
                        } else {
                            source.sendMessage(Text.of(pluginInfo.prefix, TextColors.RED, "An error occurred while serializing user ", targetUser.name))
                        }
                    }
                }.listener(ClickInventoryEvent::class.java) { e: ClickInventoryEvent ->
                    if (!permissionToEdit) {
                        e.isCancelled = true
                    }

                    // prevent touching the barriers at the end
                    if (e.transactions.stream().anyMatch { slotTransaction: SlotTransaction ->
                            slotTransaction.original.createStack().equalTo(
                                inventorySerializer.defaultFallbackItemStackSnapshot.createStack())
                        }
                    ) {
                        e.isCancelled = true
                    }
                    if (e.transactions.stream().anyMatch { slotTransaction: SlotTransaction ->
                            slotTransaction.original.createStack().equalTo(
                                inventorySerializer.exitWithoutSavingItemStackSnapshot.createStack())
                        }
                    ) {
                        e.isCancelled = true
                        closeData[0] = true
                        source.closeInventory()
                    }
                }.build(pluginContainer)
                inventorySerializer.deserializeInventory(snapshot, inventory)
                source.openInventory(inventory, Text.of(TextColors.DARK_AQUA,
                    timeFormatService.format(snapshot.createdUtc).toString()))
            }
            memberManager.primaryComponent.getSnapshotForUser(
                targetUser.uniqueId,
                snapshot
            ).thenApplyAsync {
                Task.builder()
                    .execute(Runnable { afterFound.accept(it as Optional<Snapshot<*>>) })
                    .submit(pluginContainer)
            }
            CommandResult.success()
        } else {
            throw CommandException(Text.of(pluginInfo.prefix, "Can only be run as a player"))
        }
    }

    override fun getSuggestions(source: CommandSource, arguments: String, targetPosition: Location<World>?): MutableList<String> {
        TODO("Not yet implemented")
    }

    override fun testPermission(source: CommandSource): Boolean {
        return source.hasPermission(registry.getOrDefault(DataSyncKeys.SNAPSHOT_VIEW_BASE_PERMISSION))
    }

    override fun getShortDescription(source: CommandSource): Optional<Text> = DESCRIPTION
    override fun getHelp(source: CommandSource): Optional<Text> = DESCRIPTION
    override fun getUsage(source: CommandSource): Text = USAGE
}
