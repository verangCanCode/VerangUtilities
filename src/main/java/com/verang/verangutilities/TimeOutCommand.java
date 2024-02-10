package com.verang.verangutilities;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TimeOutCommand implements CommandExecutor {
    private final VerangUtilities plugin;

    public TimeOutCommand(VerangUtilities plugin) {
        this.plugin = plugin;
        // Ensure the command "timeout" is defined in your plugin.yml and then register this executor.
        plugin.getCommand("timeout").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("timeout")) return true;

        // Permission check
        if (!sender.hasPermission("verangutilities.timeout")) {
            plugin.sendMessage(sender, "no-permission");
            return true;
        }

        if (args.length < 1) {
            plugin.sendMessage(sender, "timeout-usage");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            plugin.sendMessage(sender, "player-not-online");
            return true;
        }

        String kickMessage = plugin.getConfig().getString("messages.kick-message", "Connection Error: Please try again.");
        target.kickPlayer(kickMessage);
        plugin.sendMessage(sender, "player-kicked", "<player>", target.getName());

        return true;
    }
}
