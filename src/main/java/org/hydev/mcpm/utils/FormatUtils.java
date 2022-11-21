package org.hydev.mcpm.utils;

import com.fasterxml.jackson.databind.util.ArrayIterator;
import com.google.common.collect.Streams;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.IntStream;

import static org.apache.commons.lang3.StringUtils.*;
import static org.hydev.mcpm.utils.ColorLogger.lengthNoColor;

/**
 * Utility functions for formatting the CLI
 *
 * @author Azalea (https://github.com/hykilpikonna)
 * @since 2022-11-20
 */
public class FormatUtils
{
    /**
     * String justification type
     */
    private enum Justify
    {
        LEFT, RIGHT, CENTER
    }

    /**
     * Justify a string
     *
     * @param in Input string
     * @param method Justification Method
     * @param len Justification length
     * @return Justified string
     */
    private static String justify(String in, Justify method, int len)
    {
        // Adjust for color
        len += in.length() - lengthNoColor(in);

        // Justify
        return switch (method)
        {
            case LEFT -> rightPad(in, len);
            case RIGHT -> leftPad(in, len);
            case CENTER -> center(in, len);
        };
    }

    /**
     * Tabulate a table, with justify and adjusted for colors. This function processes justification automatically. If
     * a header begins with :, then it is justified to the left. If a header ends with :, then it is justified to the
     * right.
     *
     * @param rows1 Rows of objects (should have length R, with each row having length C)
     * @param headers Headers (should have length C)
     * @return Formatted string
     */
    public static String tabulate(List<List<String>> rows1, List<String> headers)
    {
        return tabulate(rows1, headers, "&r | ");
    }

    /**
     * Tabulate a table, with justify and adjusted for colors. This function processes justification automatically. If
     * a header begins with :, then it is justified to the left. If a header ends with :, then it is justified to the
     * right.
     *
     * @param rows1 Rows of objects (should have length R, with each row having length C)
     * @param headers Headers (should have length C)
     * @param sep Separator
     * @return Formatted string
     */
    public static String tabulate(List<List<String>> rows1, List<String> headers, String sep)
    {
        // Make rows mutable
        var rows = new ArrayList<>(rows1);

        // Find out justify method for each column
        var justify = headers.stream().map(h -> h.endsWith(":") ? Justify.RIGHT
            : h.startsWith(":") ? Justify.LEFT : Justify.CENTER).toList();

        // Add headers row as a row, bold headers
        rows.add(0, headers.stream().map(h -> "&f&n" + StringUtils.strip(h, ":") + "&r").toList());

        // Find max lengths for each column
        var lens = IntStream.range(0, headers.size()).map(col -> rows.stream()
            .mapToInt(r -> lengthNoColor(r.get(col))).max().orElse(0)).toArray();

        // Format string
        var lines = rows.stream().map(row -> String.join(sep, Streams.mapWithIndex(row.stream(),
            (v, col) -> justify(v, justify.get((int) col), lens[(int) col])).toList())).toList();

        // Join
        return String.join("&r\n", lines);
    }
}

