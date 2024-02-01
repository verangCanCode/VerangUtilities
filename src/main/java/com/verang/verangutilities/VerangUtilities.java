package com.verang.verangutilities;

import org.bukkit.plugin.java.JavaPlugin;

public class VerangUtilities extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("VerangUtilities is starting up!");

        // Load config.yml (or create it if it doesn't exist)
        saveDefaultConfig();

        // Registering the stackup command
        this.getCommand("stackup").setExecutor(new StackUpCommand(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("VerangUtilities is shutting down!");
    }
}