package org.hydev.mcpm.client.database.export;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Fixed local file string storage. This stores the string to a specific file.
 *
 * @param path Path of the file
 * @author Peter (https://github.com/MstrPikachu)
 * @author Azalea (https://github.com/hykilpikonna)
 * @since 2022-11-23
 */
public record FixedFileStorage(Path path) implements StringStorage
{
    @Override
    public String store(String content) throws IOException
    {
        // Write file
        Files.writeString(path, content, StandardCharsets.UTF_8);
        return path.toAbsolutePath().toString();
    }

    @Override
    public String load(String token) throws IOException
    {
        // Read file
        return Files.readString(path, StandardCharsets.UTF_8);
    }

    @Override
    public String instruction()
    {
        return String.format("Please save your content to %s.", path.toAbsolutePath());
    }
}