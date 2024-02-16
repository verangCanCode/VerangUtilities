package com.verang.verangutilities;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MuteChatCommand implements CommandExecutor {
    private VerangUtilities plugin;

    public MuteChatCommand(VerangUtilities plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        if (plugin.toggleChatMute(player)) {
            plugin.sendMessage(player, "chat-muted");
        } else {
            plugin.sendMessage(player, "chat-unmuted");
        }

        return true;
    }
}
