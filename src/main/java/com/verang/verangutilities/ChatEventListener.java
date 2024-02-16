package com.verang.verangutilities;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatEventListener implements Listener {
    private VerangUtilities plugin;

    public ChatEventListener(VerangUtilities plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player sender = event.getPlayer();
        // Loop through all recipients and remove those who have muted chat
        event.getRecipients().removeIf(recipient -> plugin.hasChatMuted(recipient));
    }
}
