package org.hydev.mcpm.client.installer.input;

import org.hydev.mcpm.client.database.LocalPluginTracker;
import org.hydev.mcpm.client.database.PluginTracker;
import org.hydev.mcpm.client.database.boundary.SearchPackagesBoundary;
import org.hydev.mcpm.client.database.inputs.SearchPackagesType;
import org.hydev.mcpm.client.installer.PluginDownloader;

/**
 * Install Plugin Input
 *
 * @param name     Plugin name from repository
 * @param filePath Downloaded local file (exclusive with name)
 * @param load     Whether to load after installing
 */
public record InstallInput(
    String name,
    SearchPackagesType type,
    String filePath,
    boolean load,
    SearchPackagesBoundary searchPackage,
    PluginDownloader pluginDownloader,
    PluginTracker pluginTracker
) {

}
