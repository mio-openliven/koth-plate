package me.openliven.kothplate.packaging;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ResourcePresenceTest {
    @Test
    void pluginResourcesArePackaged() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        assertNotNull(loader.getResource("plugin.yml"));
        assertNotNull(loader.getResource("config.yml"));
        assertNotNull(loader.getResource("ru.yml"));
        assertNotNull(loader.getResource("en.yml"));
    }
}
