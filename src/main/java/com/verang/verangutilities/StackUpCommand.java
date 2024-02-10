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
    private Map<Player, Boolean> stopAtCeilingForPlayer = new HashMap<>(); // Added

    public StackUpCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            // Assuming VerangUtilities has a method like this
            ((VerangUtilities) plugin).sendMessage(sender, "command-player-only");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("verangutilities.stackup")) {
            ((VerangUtilities) plugin).sendMessage(player, "no-permission");
            return true;
        }

        if (args.length == 0) {
            ((VerangUtilities) plugin).sendMessage(player, "usage");
            return true;
        } else if (args[0].equalsIgnoreCase("off")) {
            isEnabledForPlayer.put(player, false);
            ((VerangUtilities) plugin).sendMessage(player, "stackup-disabled");
            return true;
        }

        try {
            int stackHeight = Integer.parseInt(args[0]);
            isEnabledForPlayer.put(player, true);
            stackHeightForPlayer.put(player, stackHeight);
            boolean stopAtCeiling = args.length > 1 && args[1].equalsIgnoreCase("-a");
            stopAtCeilingForPlayer.put(player, stopAtCeiling); // Update per-player flag
            ((VerangUtilities) plugin).sendMessage(player, "stackup-enabled", "<amount>", String.valueOf(stackHeight));
        } catch (NumberFormatException e) {
            ((VerangUtilities) plugin).sendMessage(player, "invalid-number");
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
        Boolean stopAtCeiling = stopAtCeilingForPlayer.getOrDefault(player, false); // Use per-player flag
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
