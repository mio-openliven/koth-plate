package me.openliven.kothplate.service;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Locale;

public final class MessageService {
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();

    private final JavaPlugin plugin;
    private YamlConfiguration activeMessages;
    private YamlConfiguration fallbackMessages;

    public MessageService(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void load(String language) {
        saveLanguageFile("ru.yml");
        saveLanguageFile("en.yml");

        fallbackMessages = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "ru.yml"));

        String normalizedLanguage = language == null ? "ru" : language.toLowerCase(Locale.ROOT);
        if (!normalizedLanguage.equals("ru") && !normalizedLanguage.equals("en")) {
            plugin.getLogger().warning("Unsupported language '" + language + "'. Falling back to ru.");
            normalizedLanguage = "ru";
        }

        activeMessages = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), normalizedLanguage + ".yml"));
    }

    private void saveLanguageFile(String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            plugin.saveResource(fileName, false);
        }
    }

    public void send(CommandSender sender, String key, String... replacements) {
        sender.sendMessage(messageWithPrefix(key, replacements));
    }

    public void action(Player player, String key, String... replacements) {
        player.sendActionBar(message(key, replacements));
    }

    private Component messageWithPrefix(String key, String... replacements) {
        return message("prefix").append(message(key, replacements));
    }

    private Component message(String key, String... replacements) {
        return LEGACY.deserialize(rawMessage(key, replacements));
    }

    private String rawMessage(String key, String... replacements) {
        String value = activeMessages == null ? null : activeMessages.getString(key);
        if (value == null && fallbackMessages != null) {
            value = fallbackMessages.getString(key);
        }
        if (value == null) {
            value = "";
        }
        for (int index = 0; index + 1 < replacements.length; index += 2) {
            value = value.replace(replacements[index], replacements[index + 1]);
        }
        return value;
    }
}
