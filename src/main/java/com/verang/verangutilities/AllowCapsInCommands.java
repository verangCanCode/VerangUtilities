package com.verang.verangutilities;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class AllowCapsInCommands implements Listener {
    private final Set<Player> playersWithCapsOff;
    private final VerangUtilities plugin;

    public AllowCapsInCommands(VerangUtilities plugin) {
        this.plugin = plugin;
        this.playersWithCapsOff = new HashSet<>();
        // Ensure this class is only registered as an event listener once, preferably in the main class.
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage();
        Player player = event.getPlayer();

        if ("/capsoff".equalsIgnoreCase(message)) {
            handleCapsOffCommand(player);
            event.setCancelled(true); // Prevent further processing of the command
        } else if (playersWithCapsOff.contains(player)) {
            // Process other commands with caps off if applicable
            convertCommandToLowerCase(event);
        }
    }

    private void handleCapsOffCommand(Player player) {
        if (playersWithCapsOff.contains(player)) {
            playersWithCapsOff.remove(player);
            plugin.sendMessage(player, "caps-allowed");
        } else {
            playersWithCapsOff.add(player);
            plugin.sendMessage(player, "caps-disallowed");
        }
    }

    private void convertCommandToLowerCase(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage();
        String[] parts = message.split("\\s+", 2);
        String command = parts[0].toLowerCase();
        String args = parts.length > 1 ? " " + parts[1] : "";
        event.setMessage(command + args);
    }
}
