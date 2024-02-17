package com.verang.verangutilities;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class VerangUtilities extends JavaPlugin {
    private Set<UUID> mutedChats = new HashSet<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("VerangUtilities is starting up!");
        getLogger().info("Registering event listeners...");
        saveDefaultConfig();

        // Register command executors
        getCommand("timeout").setExecutor(new TimeOutCommand(this));
        getCommand("findhome").setExecutor(new HomeFinder(this));
        getCommand("stackup").setExecutor(new StackUpCommand(this));
        getCommand("mutechat").setExecutor(new MuteChatCommand(this)); // Registering the mutechat command

        // Register event listeners
        getServer().getPluginManager().registerEvents(new ClickableChat(this), this);
        getServer().getPluginManager().registerEvents(new AllowCapsInCommands(this), this);
        getServer().getPluginManager().registerEvents(new ChatEventListener(this), this); // Registering the chat event listener

        // Schedule autosave task
        scheduleAutosave();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("VerangUtilities is shutting down!");
    }

    public void sendMessage(CommandSender sender, String messageKey, String... replacements) {
        String message = getConfig().getString("messages." + messageKey, "Message not found: " + messageKey);
        String prefix = getConfig().getString("prefix", "");

        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                message = message.replace(replacements[i], replacements[i + 1]);
            }
        }

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));
    }

    public boolean toggleChatMute(Player player) {
        UUID playerId = player.getUniqueId();
        if (mutedChats.contains(playerId)) {
            mutedChats.remove(playerId);
            return false; // Chat is now unmuted for the player
        } else {
            mutedChats.add(playerId);
            return true; // Chat is now muted for the player
        }
    }

    public boolean hasChatMuted(Player player) {
        return mutedChats.contains(player.getUniqueId());
    }

    private void scheduleAutosave() {
        // Schedule the autosave task to run every 15 minutes (18000 ticks)
        this.getServer().getScheduler().runTaskTimer(this, () -> {
            this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "save-all");
            String autosaveMessage = getConfig().getString("messages.autosave-complete", "Worlds have been autosaved.");
            getLogger().info(autosaveMessage);
        }, 0L, 18000L); // 20 ticks = 1 second, so 18000 ticks = 15 minutes
    }
}
