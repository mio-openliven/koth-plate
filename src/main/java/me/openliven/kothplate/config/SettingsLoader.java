package me.openliven.kothplate.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class SettingsLoader {
    private final JavaPlugin plugin;
    private final SettingsParser parser = new SettingsParser();

    public SettingsLoader(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public PluginSettings load() {
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();
        return parser.parse(config, message -> plugin.getLogger().warning(message));
    }
}
