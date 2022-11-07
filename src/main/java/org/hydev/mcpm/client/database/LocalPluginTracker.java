package org.hydev.mcpm.client.database;

import org.hydev.mcpm.client.models.PluginVersion;
import org.hydev.mcpm.client.models.PluginYml;
import org.hydev.mcpm.utils.PluginJarFile;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.io.*;  
import java.util.Scanner;

import javax.naming.NameNotFoundException;  

/**
 * This class keeps track of locally installed packages
 *
 * @author Azalea (https://github.com/hykilpikonna)
 * @author Kevin (https://github.com/kchprog)
 * @since 2022-09-27
 */
public class LocalPluginTracker {
    private String MainLockFile = "TODO: Get this path"; // CSV file storing the list of manually installed plugins
    private String PluginDirectory = "TODO: Get this path"; // Directory storing the plugins

    public LocalPluginTracker(String mainLockFile, String pluginDirectory) {
        this.MainLockFile = mainLockFile;
        this.PluginDirectory = pluginDirectory;
    }
        
    /**
     * Read metadata from a plugin's jar
     *
     * @param jar Local plugin jar path
     * @return Metadata
     */
    public PluginYml readMeta(File jar)
    {
        try (PluginJarFile InstancePluginJarFile = new PluginJarFile(jar)) {
            return(InstancePluginJarFile.readPluginYaml());
        } catch (Exception e) {
            System.out.printf("Error reading plugin.yml from " + jar);
            return null;
        }
    }

    /**
     * List all currently installed plugins in an ArrayList
     * 
     * 
     * 
     */
    public List<PluginYml> listInstalled()
    {   
        List<PluginYml>InstalledPlugins = new ArrayList<PluginYml>();
        // Go into the plugin directory and read the metadata of all the plugins
        File dir = new File(PluginDirectory);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                try {
                    InstalledPlugins.add(readMeta(child));
                } catch (Exception e) {
                    System.out.printf("Error reading plugin.yml from " + child);
                }
            }
        } else {
            System.out.printf("Error reading directory");
        }

        return InstalledPlugins;
    }

    /**
     * Mark a plugin as manually installed (as opposed to a dependency)
     *
     * @param name Plugin name
     * Precondition: MainLockFile is a sorted .csv file with the following format:
     * 1st column: Plugin name
     * 2nd column: Manual Dependency (true/false)
     */
    public void addManuallyInstalled(String name)
    {
        // Locate the name in the list of installed plugins and add it to the list of manually installed plugins.
        // TODO: Implement this
        Scanner sc = new Scanner(MainLockFile);
        sc.useDelimiter(",");

        boolean found = false;
        while (sc.hasNextLine() && !found) {
            String[] line = sc.nextLine().split(",");
            if (line[0].equals(name)) {
                line[1] = "true";
                found = true;
                sc.close();
            } 
        }

        if (!found) {
            // Throw an error if the plugin is not installed
            throw new IllegalArgumentException("Plugin not installed");
        }


    }

    /**
     * Remove a plugin from the manually installed plugin list
     *
     * @param name Plugin name
     */
    public void removeManuallyInstalled(String name)
    {
        // TODO: Implement this
        throw new UnsupportedOperationException("TODO");
    }

    /**
     * Get a list of manually installed plugins
     *
     * @return List of plugin names
     */
    public List<String> listManuallyInstalled()
    {
        // TODO: Implement this
        throw new UnsupportedOperationException("TODO");
    }

    /**
     * Get a list of automatically installed plugin dependencies that are no longer required
     *
     * @return List of plugin names
     */
    public List<String> listOrphans()
    {
        // TODO: Implement this
        throw new UnsupportedOperationException("TODO");
    }
}
