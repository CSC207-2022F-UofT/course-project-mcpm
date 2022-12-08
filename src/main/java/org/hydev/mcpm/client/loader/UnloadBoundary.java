package org.hydev.mcpm.client.loader;

import java.io.File;

/**
 * Interface for unloading a locally installed plugin.
 */
public interface UnloadBoundary
{
    /**
     * Dynamically unload a local plugin through JVM reflections and classloader hacks
     *
     * @param name Loaded plugin name
     * @throws PluginNotFoundException If a loaded plugin of the name isn't found
     */
    File unloadPlugin(String name) throws PluginNotFoundException;
}
