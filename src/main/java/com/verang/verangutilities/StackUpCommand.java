package com.verang.verangutilities;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class StackUpCommand implements CommandExecutor, Listener {
    private JavaPlugin plugin;
    private Map<Player, Boolean> isEnabledForPlayer = new HashMap<>();
    private Map<Player, Integer> stackHeightForPlayer = new HashMap<>();
    private boolean stopAtCeiling = false;

    public StackUpCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfig().getString("messages.command-player-only"));
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("verangutilities.stackup")) {
            player.sendMessage(plugin.getConfig().getString("messages.no-permission"));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(plugin.getConfig().getString("messages.usage"));
            return true;
        } else if (args[0].equalsIgnoreCase("off")) {
            isEnabledForPlayer.put(player, false);
            player.sendMessage(plugin.getConfig().getString("messages.stackup-disabled"));
            return true;
        }

        try {
            int stackHeight = Integer.parseInt(args[0]);
            isEnabledForPlayer.put(player, true);
            stackHeightForPlayer.put(player, stackHeight);
            stopAtCeiling = args.length > 1 && args[1].equalsIgnoreCase("-a");
            player.sendMessage(plugin.getConfig().getString("messages.stackup-enabled").replace("<amount>", String.valueOf(stackHeight)));
        } catch (NumberFormatException e) {
            player.sendMessage(plugin.getConfig().getString("messages.invalid-number"));
        }

        return true;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Boolean isEnabled = isEnabledForPlayer.getOrDefault(player, false);
        if (!isEnabled) {
            return;
        }

        Integer stackHeight = stackHeightForPlayer.getOrDefault(player, 0);
        Block block = event.getBlockPlaced();
        Material blockType = block.getType();
        for (int i = 1; i < stackHeight; i++) {
            Block above = block.getRelative(0, i, 0);
            if (stopAtCeiling && above.getType() != Material.AIR) {
                break;
            }
            above.setType(blockType);
        }
    }
}
