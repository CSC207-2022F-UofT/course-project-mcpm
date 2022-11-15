package org.hydev.mcpm.client.database.searchusecase;

import org.hydev.mcpm.client.database.inputs.SearchPackagesInput;
import org.hydev.mcpm.client.database.results.SearchPackagesResult;
import org.hydev.mcpm.client.models.PluginModel;

import java.util.List;
import java.util.Map;

/**
 * Interface for searching plugins.
 *
 * @author Jerry Zhu (https://github.com/jerryzhu509)
 */
public interface Searcher {

    /**
     * Searches for plugins based on the provided user input.
     *
     * @param plugins A list of all plugins in the database.
     * @return A dictionary associating a string feature of the plugins to the matching plugins.
     */
    Map<String, List<PluginModel>> constructSearchMaps(List<PluginModel> plugins);
}
