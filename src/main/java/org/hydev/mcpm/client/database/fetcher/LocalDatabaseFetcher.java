package org.hydev.mcpm.client.database.fetcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.hydev.mcpm.client.models.Database;
import org.hydev.mcpm.utils.HashUtils;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

public class LocalDatabaseFetcher implements DatabaseFetcher {
    private final URI host;
    private final Path cacheDirectory;

    private Database localDatabase;

    public static final String HASH_FILE_NAME = "db.hash";
    public static final String DATABASE_FILE_NAME = "db";

    public static final String USER_AGENT = "MCPM Client";

    public LocalDatabaseFetcher(URI host) {
        this(host, Path.of(".mcpm/"));
    }

    public LocalDatabaseFetcher(URI host, Path cacheDirectory) {
        this.host = host;
        this.cacheDirectory = cacheDirectory;
    }

    private Request requestTo(String path) {
        return Request
            .get(URI.create(host.toString() + "/" + path))
            .addHeader("Host", host.getHost())
            .addHeader("User-Agent", "MCPM Client")
            .addHeader("Accepts", "application/json");
    }

    @Nullable
    private String fetchHostHash() {
        try {
            var response = requestTo(HASH_FILE_NAME).execute();

            return response.returnContent().asString().trim();
        } catch (IOException e) {
            return null;
        }
    }

    @Nullable
    private String fetchLocalHash() {
        try {
            return Files.readString(cacheDirectory.resolve(DATABASE_FILE_NAME));
        } catch (IOException e) {
            return null;
        }
    }

    private boolean localDatabaseIsUpToDate() {
        var hostHash = fetchHostHash();
        var localHash = fetchLocalHash();

        // We can't reach the server right now, don't update.
        if (hostHash == null)
            return true;

        // Missing hash, we need to update.
        if (localHash == null)
            return false;

        return hostHash.equals(localHash);
    }

    @Nullable
    private Database fetchLocalDatabase() {
        try {
            if (localDatabase != null) {
                return localDatabase;
            }

            var file = cacheDirectory.resolve(DATABASE_FILE_NAME).toFile();

            var database = new ObjectMapper().readValue(file, Database.class);

            if (database != null) {
                localDatabase = database;
            }

            return database;
        } catch (IOException e) {
            return null;
        }
    }

    private String readDatabaseFromContent(HttpEntity entity, DatabaseFetcherListener listener) throws IOException {
        long total = entity.getContentLength();

        var builder = new ByteArrayOutputStream();

        try (var stream = entity.getContent()) {
            var buffer = new byte[8096];
            long completed = 0;

            var count = stream.read(buffer);

            while (count > 0) {
                builder.write(buffer, 0, count);

                completed += count;
                listener.download(completed, total);

                count = stream.read(buffer);
            }

            listener.download(total, total);
        }

        listener.finish();

        return builder.toString(StandardCharsets.UTF_8);
    }

    @Nullable
    private Database fetchHostDatabase(DatabaseFetcherListener listener) {
        try {
            var response = requestTo(DATABASE_FILE_NAME).execute();
            var httpResponse = (ClassicHttpResponse) response.returnResponse();

            var body = readDatabaseFromContent(httpResponse.getEntity(), listener);

            var database = new ObjectMapper().readValue(body, Database.class);

            if (database != null) {
                localDatabase = database;

                try {
                    var hash = new HashUtils().hash(body);

                    Files.writeString(Paths.get(cacheDirectory.toString(), DATABASE_FILE_NAME), body);
                    Files.writeString(Paths.get(cacheDirectory.toString(), HASH_FILE_NAME), hash);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }

            return database;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    @Nullable
    public Database fetchDatabase(boolean cache, DatabaseFetcherListener listener) {
        var databaseHasAlreadyBeenChecked = localDatabase != null;

        if (cache && (databaseHasAlreadyBeenChecked || localDatabaseIsUpToDate())) {
            var database = fetchLocalDatabase();

            if (database != null)
                return database;
        }

        return fetchHostDatabase(listener);
    }
}