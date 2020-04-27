package org.anvilpowered.datasync.sponge.command;

import com.google.inject.Inject;
import org.anvilpowered.anvil.api.plugin.PluginInfo;
import org.anvilpowered.datasync.api.serializer.user.UserSerializerManager;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class SyncTestCommand implements CommandExecutor {

    @Inject
    private UserSerializerManager<User, Text> userSerializerManager;

    @Inject
    private PluginInfo<Text> pluginInfo;

    @Override
    public CommandResult execute(CommandSource source, CommandContext context) {
        if (!(source instanceof Player)) {
            source.sendMessage(Text.of(pluginInfo.getPrefix(), TextColors.RED, "Run as player"));
            return CommandResult.empty();
        }
        Player player = (Player) source;
        userSerializerManager.serialize(player)
            .exceptionally(e -> {
                source.sendMessage(Text.of(e.getMessage()));
                return null;
            })
            .thenAcceptAsync(text -> {
                if (text == null) {
                    return;
                }
                source.sendMessage(text);
                source.sendMessage(Text.of(pluginInfo.getPrefix(), TextColors.GREEN, "Deserializing in 5 seconds"));
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                userSerializerManager.restore(player.getUniqueId(), Optional.empty())
                    .thenAcceptAsync(source::sendMessage);
            });
        return CommandResult.success();
    }
}
