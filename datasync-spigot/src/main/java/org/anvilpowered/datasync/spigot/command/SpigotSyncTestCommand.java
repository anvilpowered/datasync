package org.anvilpowered.datasync.spigot.command;

import com.google.inject.Inject;
import net.md_5.bungee.api.chat.TextComponent;
import org.anvilpowered.anvil.api.util.TextService;
import org.anvilpowered.datasync.api.serializer.user.UserSerializerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpigotSyncTestCommand implements CommandExecutor {

    @Inject
    private UserSerializerManager<Player, TextComponent> userSerializerManager;

    @Inject
    private TextService<TextComponent, CommandSender> textService;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;
        userSerializerManager.serialize(player)
            .exceptionally(e -> {
                e.printStackTrace();
                sender.sendMessage("An error occurred!");
                return null;
            })
            .thenAcceptAsync(text -> {
                if (text == null) {
                    return;
                }
                sender.sendMessage(text.toPlainText());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                userSerializerManager.restore(player.getUniqueId(), null)
                    .thenAcceptAsync(msg -> textService.send(msg, player));
            });
        return true;
    }
}
