package org.hydev.mcpm.client.export;

/**
 * Results returned from ExportPluginBoundary.
 *
 * @param state The outcome of the export. Must be SUCCESS for other values to be valid.
 */
public record ExportPluginsResult(
        State state
) {
    /**
     * Outcome of the plugin export
     */
    public enum State {
        SUCCESS,
        FAILED_TO_FETCH_PLUGINS
    }
}