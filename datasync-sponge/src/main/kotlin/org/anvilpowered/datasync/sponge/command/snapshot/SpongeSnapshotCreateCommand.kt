package org.anvilpowered.datasync.sponge.command.snapshot

import org.anvilpowered.anvil.api.splitContext
import org.anvilpowered.datasync.api.registry.DataSyncKeys
import org.anvilpowered.datasync.common.command.snapshot.CommonSnapshotCommandNode
import org.anvilpowered.datasync.common.command.snapshot.CommonSnapshotCreateCommand
import org.spongepowered.api.command.CommandCallable
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.living.player.User
import org.spongepowered.api.text.Text
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World
import java.util.Optional

class SpongeSnapshotCreateCommand : CommonSnapshotCreateCommand<Text, User, Player, CommandSource>(), CommandCallable {

    companion object {
        val DESCRIPTION: Optional<Text> = Optional.of(Text.of(CommonSnapshotCommandNode.CREATE_DESCRIPTION))
        val USAGE: Text = Text.of(CommonSnapshotCommandNode.CREATE_USAGE)
    }

    override fun process(source: CommandSource, context: String): CommandResult {
        execute(source, context.splitContext())
        return CommandResult.success()
    }

    override fun getSuggestions(source: CommandSource, arguments: String, targetPosition: Location<World>?): MutableList<String> {
        TODO("Not yet implemented")
    }

    override fun testPermission(source: CommandSource): Boolean {
        return source.hasPermission(registry.getOrDefault(DataSyncKeys.SNAPSHOT_CREATE_PERMISSION))
    }

    override fun getShortDescription(source: CommandSource): Optional<Text> = DESCRIPTION
    override fun getHelp(source: CommandSource): Optional<Text> = DESCRIPTION
    override fun getUsage(source: CommandSource): Text = USAGE
}
