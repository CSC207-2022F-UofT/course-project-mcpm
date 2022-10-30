package org.hydev.mcpm.utils;

import com.google.common.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Utilities for JVM reflections
 *
 * @author Azalea (https://github.com/hykilpikonna)
 * @since 2022-10-29
 */
public class ReflectionUtils
{
    /**
     * Read the value of a private instance variable
     *
     * @param obj Object to read from
     * @param fieldName Name of the variable
     * @param type Type of the result
     * @return Value of the private instance variable, or null if it cannot be read
     */
    @SuppressWarnings("unchecked")
    public static <T> Optional<T> getPrivateField(@NotNull Object obj, @NotNull String fieldName,
                                                  @NotNull TypeToken<T> type)
    {
        try
        {
            var field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return Optional.ofNullable((T) type.getRawType().cast(field.get(obj)));
        }
        catch (NoSuchFieldException | IllegalAccessException e)
        {
            return Optional.empty();
        }
    }
}
