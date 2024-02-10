package com.verang.verangutilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ClickableChat implements Listener {
    private static final Pattern URL_PATTERN = Pattern.compile("(https?://[\\w-]+(\\.[\\w-]+)+[/#?]?.*$)", Pattern.CASE_INSENSITIVE);
    private final JavaPlugin plugin;

    public ClickableChat(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        Matcher matcher = URL_PATTERN.matcher(message);

        if (matcher.find()) {
            Player player = event.getPlayer();
            event.setCancelled(true); // Cancel the event to prevent other handlers from processing it further

            String url = matcher.group();
            TextComponent urlComponent = new TextComponent(url);
            urlComponent.setColor(ChatColor.of("#83c9d6"));
            urlComponent.setUnderlined(true);
            urlComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url.startsWith("http") ? url : "http://" + url));

            TextComponent chatMessage = new TextComponent();
            chatMessage.setColor(ChatColor.WHITE); // Reset any previous color formatting
            chatMessage.addExtra(urlComponent);

            if (message.length() > matcher.end()) {
                chatMessage.addExtra(message.substring(matcher.end()));
            }

            // Send formatted message directly to all recipients, bypassing Essentials or other chat plugins
            for (Player recipient : event.getRecipients()) {
                recipient.spigot().sendMessage(chatMessage);
            }
        }
    }
}
