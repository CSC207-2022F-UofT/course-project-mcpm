package org.hydev.mcpm.client.arguments;

import org.hydev.mcpm.client.DatabaseManager;
import org.hydev.mcpm.client.Downloader;
import org.hydev.mcpm.client.arguments.parsers.*;
import org.hydev.mcpm.client.commands.entries.*;
import org.hydev.mcpm.client.database.ListAllInteractor;
import org.hydev.mcpm.client.database.LocalPluginTracker;
import org.hydev.mcpm.client.database.mirrors.MirrorSelector;
import org.hydev.mcpm.client.database.export.ExportInteractor;
import org.hydev.mcpm.client.database.fetcher.LocalDatabaseFetcher;
import org.hydev.mcpm.client.database.searchusecase.SearchInteractor;
import org.hydev.mcpm.client.injector.PluginLoader;
import org.hydev.mcpm.client.installer.InstallInteractor;
import org.hydev.mcpm.client.installer.SpigotPluginDownloader;
import org.hydev.mcpm.utils.ColorLogger;

import java.net.URI;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Class that produces default implementations of the Controller and ArgsParser classes.
 * These classes are used to handle commands.
 * Checkout Command.java for a process on adding a new command!
 */
public class CommandsFactory {
    // No instantiation.
    private CommandsFactory() { }

    /**
     * Creates a list of general parsers for the ArgsParser class.
     *
     * @return Returns a list of argument parsers that work in any environment (Server & CLI).
     */
    public static List<CommandParser> baseParsers() {
        var mirror = new MirrorSelector();
        var fetcher = new LocalDatabaseFetcher(mirror.selectedMirrorSupplier());
        var tracker = new LocalPluginTracker();
        var searcher = new SearchInteractor(fetcher);
        var exportPluginsController = new ExportPluginsController(new ExportInteractor(tracker));
        var listController = new ListController(new ListAllInteractor(tracker));
        var searchController = new SearchPackagesController(searcher);
        var mirrorController = new MirrorController(mirror);
        var infoController = new InfoController(tracker);
        var installController = new InstallController(new InstallInteractor(
            new SpigotPluginDownloader(new Downloader(), mirror.selectedMirrorSupplier()),
            new DatabaseManager(tracker, searcher)));

        /*
         * Add general parsers to this list!
         * All versions of MCPM will have access to these parsers.
         * If you're not sure if your command is server-only, add it to this list!
         */
        return List.of(
            new ExportPluginsParser(exportPluginsController),
            new ListParser(listController),
            new SearchParser(searchController),
            new MirrorParser(mirrorController),
            new InfoParser(infoController),
            new InstallParser(installController)
        );
    }

    /**
     * Creates a list of server-only parsers for the ArgsParser class.
     *
     * @return Returns a list of argument parsers that require the Server (Minecraft Bukkit Plugin) environment.
     */
    public static List<CommandParser> serverParsers() {
        var loader = new PluginLoader();

        var loadController = new LoadController(loader);
        var reloadController = new ReloadController(loader);
        var unloadController = new UnloadController(loader);

        /*
         * Add server-only parsers to this list!
         * Server only commands will not show up in the MCPM CLI.
         * They'll only be accessible when a user tries to execute a command from the MCPM Server Plugin (in-game).
         */
        var serverOnly = List.of(
            new LoadParser(loadController),
            new ReloadParser(reloadController),
            new UnloadParser(unloadController)
        );

        return Stream.concat(baseParsers().stream(), serverOnly.stream()).toList();
    }

    /**
     * Creates an ArgsParser object that has general parsers (works in any environment).
     *
     * @return An ArgsParser object. Invoke ArgsParser#parse to see more.
     */
    public static ArgsParser baseArgsParser() {
        return new ArgsParser(baseParsers(), ColorLogger.toStdOut());
    }

    /**
     * Creates an ArgsParser object that has all (CLI & Server) parsers.
     *
     * @return An ArgsParser object. Invoke ArgsParser#parse to see more.
     */
    public static ArgsParser serverArgsParser(Consumer<String> log) {
        return new ArgsParser(serverParsers(), log);
    }
}
