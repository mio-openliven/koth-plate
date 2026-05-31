package me.openliven.kothplate.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SettingsParserTest {
    private final SettingsParser parser = new SettingsParser();

    @Test
    void parsesValidSettings() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("settings.language", "en");
        config.set("settings.capture-time", 45);
        config.set("settings.reward-amount", 75.5D);
        config.set("settings.respect-cancelled-physical-events", true);
        config.set("settings.schedule.enabled", true);
        config.set("settings.schedule.timezone", "UTC");
        config.set("settings.schedule.windows", List.of(Map.of("start", "18:00", "end", "23:00")));
        config.set("zone.world", "world");
        config.set("zone.x", 10);
        config.set("zone.y", 65);
        config.set("zone.z", -4);

        List<String> warnings = new ArrayList<>();
        PluginSettings settings = parser.parse(config, warnings::add);

        assertEquals("en", settings.language());
        assertEquals(45, settings.captureSeconds());
        assertEquals(75.5D, settings.rewardAmount());
        assertTrue(settings.respectCancelledPhysicalEvents());
        assertTrue(settings.schedule().enabled());
        assertEquals(ZoneId.of("UTC"), settings.schedule().zone());
        assertEquals(LocalTime.parse("18:00"), settings.schedule().windows().get(0).start());
        assertEquals("world", settings.platePosition().worldName());
        assertEquals(10, settings.platePosition().x());
        assertEquals(65, settings.platePosition().y());
        assertEquals(-4, settings.platePosition().z());
        assertTrue(warnings.isEmpty());
    }

    @Test
    void fallsBackFromInvalidValues() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("settings.language", "de");
        config.set("settings.capture-time", 0);
        config.set("settings.reward-amount", -10.0D);
        config.set("settings.schedule.enabled", true);
        config.set("settings.schedule.timezone", "bad/timezone");
        config.set("settings.schedule.windows", List.of(Map.of("start", "bad", "end", "23:00")));

        List<String> warnings = new ArrayList<>();
        PluginSettings settings = parser.parse(config, warnings::add);

        assertEquals("ru", settings.language());
        assertEquals(20, settings.captureSeconds());
        assertEquals(25.0D, settings.rewardAmount());
        assertTrue(settings.schedule().enabled());
        assertTrue(settings.schedule().windows().isEmpty());
        assertNull(settings.platePosition());
        assertEquals(6, warnings.size());
    }

    @Test
    void blankZoneMeansPointIsNotConfigured() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("zone.world", "");
        config.set("zone.x", 1);
        config.set("zone.y", 2);
        config.set("zone.z", 3);

        PluginSettings settings = parser.parse(config, ignored -> {
        });

        assertNull(settings.platePosition());
        assertEquals("ru", settings.language());
        assertEquals(20, settings.captureSeconds());
        assertEquals(25.0D, settings.rewardAmount());
        assertFalse(settings.schedule().enabled());
    }
}
