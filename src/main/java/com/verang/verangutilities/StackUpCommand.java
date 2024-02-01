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

public class StackUpCommand implements CommandExecutor, Listener {
    private JavaPlugin plugin;
    private boolean isEnabled = false;
    private int stackHeight = 0;
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
        if (args.length > 0 && args[0].equalsIgnoreCase("off")) {
            isEnabled = false;
            player.sendMessage(plugin.getConfig().getString("messages.stackup-disabled"));
            return true;
        }

        try {
            stackHeight = Integer.parseInt(args[0]);
            isEnabled = true;
            stopAtCeiling = args.length > 1 && args[1].equalsIgnoreCase("-a");
            player.sendMessage(plugin.getConfig().getString("messages.stackup-enabled").replace("<amount>", String.valueOf(stackHeight)));
        } catch (NumberFormatException e) {
            player.sendMessage(plugin.getConfig().getString("messages.invalid-number"));
        }

        return true;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!isEnabled || !event.getPlayer().equals(event.getPlayer())) {
            return;
        }

        Block block = event.getBlockPlaced();
        Material blockType = block.getType();
        for (int i = 1; i < stackHeight; i++) {
            Block above = block.getRelative(0, i, 0);
            if (stopAtCeiling && !above.getType().equals(Material.AIR)) {
                break;
            }
            above.setType(blockType);
        }
    }
}