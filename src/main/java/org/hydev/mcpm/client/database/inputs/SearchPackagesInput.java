package org.hydev.mcpm.client.database.inputs;

/**
 * Input for the SearchPackagesBoundary.
 *
 * @param type An int that specifies the type of search.
 *             1 if the search is by name, 2 if the search is by command. Else the search is by keyword.
 * @param searchStr String containing the name, keyword, or command to search by.
 * @param noCache If true, the ListPackagesBoundary will re-download the database before performing the request.
 *
 * @author Jerry Zhu (<a href="https://github.com/jerryzhu509">...</a>)
 */
public record SearchPackagesInput(Type type, String searchStr, boolean noCache) {
    /**
     * The possible types of searching.
     */
    public enum Type {
        BY_NAME,
        BY_COMMAND,
        BY_KEYWORD
    }
}