package com.superbot.plugin;

import com.superbot.plugin.ai.BotAIManager;
import com.superbot.plugin.commands.SpawnBotCommand;
import com.superbot.plugin.commands.RemoveBotCommand;
import com.superbot.plugin.commands.ReloadCommand;
import com.superbot.plugin.data.BotDataManager;
import com.superbot.plugin.listeners.ChatListener;
import com.superbot.plugin.listeners.GuiListener;
import com.superbot.plugin.listeners.NpcListener;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.plugin.java.JavaPlugin;

public final class SuperBotPlugin extends JavaPlugin {

    private static SuperBotPlugin instance;
    private BotDataManager dataManager;
    private BotAIManager aiManager;

    @Override
    public void onEnable() {
        instance = this;

        // Save default config
        saveDefaultConfig();

        // Check Citizens is present
        if (getServer().getPluginManager().getPlugin("Citizens") == null) {
            getLogger().severe("Citizens plugin not found! SuperBot requires Citizens. Disabling.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Init data manager (SQLite)
        dataManager = new BotDataManager(this);
        dataManager.initialize();

        // Init AI manager
        aiManager = new BotAIManager(this);
        aiManager.start();

        // Register commands
        getCommand("spawnadvancedbot").setExecutor(new SpawnBotCommand(this));
        getCommand("removebot").setExecutor(new RemoveBotCommand(this));
        getCommand("superbotreload").setExecutor(new ReloadCommand(this));

        // Register listeners
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new GuiListener(this), this);
        getServer().getPluginManager().registerEvents(new NpcListener(this), this);

        getLogger().info("SuperBot v" + getDescription().getVersion() + " enabled!");
    }

    @Override
    public void onDisable() {
        if (aiManager != null) aiManager.stop();
        if (dataManager != null) dataManager.shutdown();
        getLogger().info("SuperBot disabled. All bot data saved.");
    }

    public static SuperBotPlugin getInstance() {
        return instance;
    }

    public BotDataManager getDataManager() {
        return dataManager;
    }

    public BotAIManager getAiManager() {
        return aiManager;
    }

    public String msg(String key) {
        String prefix = getConfig().getString("messages.prefix", "&8[&6SuperBot&8] ");
        String value = getConfig().getString("messages." + key, key);
        return colorize(prefix + value);
    }

    public String msg(String key, String... replacements) {
        String m = msg(key);
        for (int i = 0; i < replacements.length - 1; i += 2) {
            m = m.replace(replacements[i], replacements[i + 1]);
        }
        return m;
    }

    public static String colorize(String s) {
        return s.replace("&", "\u00A7");
    }
}
