package com.verang.verangutilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ClickableChat implements Listener {
    private static final Pattern URL_PATTERN = Pattern.compile("(https?://[\\w-]+(\\.[\\w-]+)+[/#?]?.*?)(\\s|$)", Pattern.CASE_INSENSITIVE);
    private final JavaPlugin plugin;

    public ClickableChat(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        Matcher matcher = URL_PATTERN.matcher(message);

        if (matcher.find()) {
            event.setCancelled(true); // Cancel the event to prevent other handlers from processing it further

            // Create a new TextComponent for the whole message
            TextComponent wholeMessage = new TextComponent("");

            int lastMatchEnd = 0;
            matcher.reset(); // Reset the matcher to use it in the loop

            // Loop through all matches
            while (matcher.find()) {
                // Text before URL
                String beforeUrl = message.substring(lastMatchEnd, matcher.start());
                if (!beforeUrl.isEmpty()) {
                    wholeMessage.addExtra(new TextComponent(beforeUrl));
                }

                // URL TextComponent
                String url = matcher.group(1);
                TextComponent urlComponent = new TextComponent(url);
                urlComponent.setColor(ChatColor.BLUE);
                urlComponent.setUnderlined(true);
                urlComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));

                wholeMessage.addExtra(urlComponent);

                lastMatchEnd = matcher.end();
            }

            // Add the rest of the message if there's any left after the last URL
            if (lastMatchEnd < message.length()) {
                wholeMessage.addExtra(new TextComponent(message.substring(lastMatchEnd)));
            }

            // Prepend player's display name and group (if applicable)
            String displayName = player.getDisplayName(); // Or use Essentials's method to get the formatted name
            TextComponent playerNameComponent = new TextComponent(displayName + ": ");
            playerNameComponent.setColor(ChatColor.GRAY); // Or any other color

            TextComponent finalComponent = new TextComponent("");
            finalComponent.addExtra(playerNameComponent);
            finalComponent.addExtra(wholeMessage);

            // Send the composed message to all recipients
            for (Player recipient : event.getRecipients()) {
                recipient.spigot().sendMessage(finalComponent);
            }
        }
    }
}
