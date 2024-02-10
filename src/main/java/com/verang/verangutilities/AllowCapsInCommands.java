package com.verang.verangutilities;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class AllowCapsInCommands implements Listener {
    private Set<Player> toggleCapsPlayers;
    private VerangUtilities plugin;

    public AllowCapsInCommands(VerangUtilities plugin) {
        this.plugin = plugin;
        this.toggleCapsPlayers = new HashSet<>();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        // Check if the command is /capsoff
        if (message.equalsIgnoreCase("/capsoff")) {
            if (toggleCapsPlayers.contains(player)) {
                toggleCapsPlayers.remove(player);
                plugin.sendMessage(player, "caps-allowed");
            } else {
                toggleCapsPlayers.add(player);
                plugin.sendMessage(player, "caps-disallowed");
            }
            event.setCancelled(true);
        } else if (toggleCapsPlayers.contains(player)) {
            // Process commands normally for players who have not toggled /capsoff
            String[] commandParts = message.split(" ");
            commandParts[0] = commandParts[0].toLowerCase(); // Force the command part to lowercase
            message = String.join(" ", commandParts);
            event.setMessage(message);
        }
    }
}
