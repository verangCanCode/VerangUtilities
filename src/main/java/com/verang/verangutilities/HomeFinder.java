package com.verang.verangutilities;

import net.ess3.api.IEssentials;
import net.ess3.api.IUser;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

public class HomeFinder implements CommandExecutor {
    private VerangUtilities plugin;

    public HomeFinder(VerangUtilities plugin) {
        this.plugin = plugin;
        plugin.getCommand("findhome").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.sendMessage(sender, "command-player-only");
            return true;
        }
        if (args.length != 2) {
            plugin.sendMessage(sender, "findhome-usage"); // Ensure this message is defined in your config.yml
            return true;
        }

        Player player = (Player) sender;
        String targetPlayerName = args[0];
        String homeName = args[1];

        // Try to get the UUID of the player whose home we're looking for
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(targetPlayerName);
        UUID targetUUID = targetPlayer.getUniqueId();

        if (!targetPlayer.hasPlayedBefore()) {
            plugin.sendMessage(player, "player-not-found");
            return true;
        }

        // Assuming EssentialsX is used and userdata is stored in UUID.yml files
        IEssentials essentials = (IEssentials) Bukkit.getPluginManager().getPlugin("Essentials");
        IUser user = essentials.getUser(targetUUID);

        if (user == null) {
            plugin.sendMessage(player, "user-data-not-found");
            return true;
        }

        try {
            File userdataFile = new File(essentials.getDataFolder() + File.separator + "userdata" + File.separator + targetUUID + ".yml");
            if (!userdataFile.exists()) {
                plugin.sendMessage(player, "user-data-not-found");
                return true;
            }

            FileConfiguration userdataConfig = YamlConfiguration.loadConfiguration(userdataFile);
            if (!userdataConfig.isSet("homes." + homeName)) {
                plugin.sendMessage(player, "home-not-found");
                return true;
            }

            String worldName = userdataConfig.getString("homes." + homeName + ".world");
            if (worldName == null) {
                plugin.sendMessage(player, "world-not-found");
                return true;
            }

            plugin.sendMessage(player, "home-location", "<player>", targetPlayerName, "<home>", homeName, "<world>", worldName);
        } catch (Exception e) {
            e.printStackTrace();
            plugin.sendMessage(player, "error-reading-home"); // Make sure to define this message in config.yml
        }

        return true;
    }
}
