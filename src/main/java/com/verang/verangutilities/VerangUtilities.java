package com.verang.verangutilities;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class VerangUtilities extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("VerangUtilities is starting up!");
        saveDefaultConfig();

        // Register command executors
        getCommand("timeout").setExecutor(new TimeOutCommand(this));
        getCommand("findhome").setExecutor(new HomeFinder(this));
        getCommand("stackup").setExecutor(new StackUpCommand(this));

        // Register event listeners
        getServer().getPluginManager().registerEvents(new ClickableChat(this), this);
        getServer().getPluginManager().registerEvents(new AllowCapsInCommands(this), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("VerangUtilities is shutting down!");
    }

    /**
     * Sends a message to a command sender (player or console) with the plugin's prefix.
     *
     * @param sender     The CommandSender to whom the message will be sent.
     * @param messageKey The key for the message in the plugin's config.yml.
     * @param replacements Varargs parameters for placeholder replacements, in key-value pairs.
     */
    public void sendMessage(CommandSender sender, String messageKey, String... replacements) {
        String message = getConfig().getString("messages." + messageKey, "Message not found: " + messageKey);
        String prefix = getConfig().getString("prefix", "");

        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) { // Ensure there's a pair
                message = message.replace(replacements[i], replacements[i + 1]);
            }
        }

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));
    }
}
