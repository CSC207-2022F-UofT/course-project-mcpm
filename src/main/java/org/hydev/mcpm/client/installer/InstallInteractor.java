package org.hydev.mcpm.client.installer;

import org.hydev.mcpm.client.DatabaseManager;
import org.hydev.mcpm.client.Downloader;
import org.hydev.mcpm.client.database.LocalPluginTracker;
import org.hydev.mcpm.client.database.PluginTracker;
import org.hydev.mcpm.client.database.fetcher.LocalDatabaseFetcher;
import org.hydev.mcpm.client.database.inputs.SearchPackagesType;
import org.hydev.mcpm.client.database.results.SearchPackagesResult;
import org.hydev.mcpm.client.database.searchusecase.SearchInteractor;
import org.hydev.mcpm.client.installer.InstallException.Type;
import org.hydev.mcpm.client.installer.input.InstallInput;
import org.hydev.mcpm.client.models.PluginModel;

import java.io.File;
import java.net.URI;

/**
 * Implementation to the InstallBoundary, handles installation of plugins
 * @author Azalea (https://github.com/hykilpikonna)
 * @author Rena (https://github.com/thudoan1706)
 * @author Taylor (https://github.com/1whatleytay)
 * @since 2022-11-20
 * */

public class InstallInteractor implements InstallBoundary {
    private static final String FILEPATH = "plugins";

    private final DatabaseManager databaseManager;
    private final PluginDownloader spigotPluginDownloader;

    public InstallInteractor(PluginDownloader spigotPluginDownloader, DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        this.spigotPluginDownloader = spigotPluginDownloader;
    }

    /*
     * Install the plugin
     * @param installInput: the plugin submitted by the user for installing
     * */
    @Override
    public void installPlugin(InstallInput installInput) throws InstallException
    {
        // 1. Search the name and get a list of plugins
        SearchPackagesResult searchResult = databaseManager.getSearchResult(installInput);


        if (searchResult.plugins().isEmpty()) {
            throw new InstallException(Type.NOT_FOUND);
        }

        // 2. Pick the plugin id
        // TODO: Let the user pick a plugin ID (Future)
        PluginModel latestPluginModel = searchResult.plugins().get(0);
        long id = latestPluginModel.id();
        for (var plugin : searchResult.plugins()) {
            if (plugin.id() > id) {
                id = plugin.id();
                latestPluginModel = plugin;
            }
        }

        var pluginVersion = latestPluginModel.getLatestPluginVersion()
                .orElseThrow(() -> new InstallException(Type.NO_VERSION_AVAILABLE));
        databaseManager.checkPluginInstalled(pluginVersion);

        // 3. Download it
        spigotPluginDownloader.download(id, pluginVersion.id(), "plugins/" + pluginVersion.meta().name() + ".jar");

        // TODO: Add manual install

        // 4. Installing the dependency of that plugin
        if (pluginVersion.meta().depend()!= null) {
            for (String dependency:pluginVersion.meta().depend()) {
                InstallInput dependencyInput = new InstallInput(dependency,
                        SearchPackagesType.BY_NAME,
                        installInput.load());
                installPlugin(dependencyInput);
//                input.pluginTracker().removeManuallyInstalled(name);
            }
        }
    }




    public static void main(String[] args) throws InstallException {
        new File(FILEPATH).mkdirs();
        Downloader downloader = new Downloader();
        SpigotPluginDownloader spigotPluginDownloader = new SpigotPluginDownloader(downloader);
        LocalPluginTracker pluginTracker = new LocalPluginTracker();
        LocalDatabaseFetcher localDatabaseFetcher = new LocalDatabaseFetcher(URI.create("http://mcpm.hydev.org"));
        SearchInteractor searchInteractor = new SearchInteractor(localDatabaseFetcher);
        DatabaseManager databaseManager = new DatabaseManager(pluginTracker, searchInteractor);
        InstallInteractor installInteractor = new InstallInteractor(spigotPluginDownloader, databaseManager);
        InstallInput installInput = new InstallInput("JedCore", SearchPackagesType.BY_NAME, true);
        installInteractor.installPlugin(installInput);
    }
}