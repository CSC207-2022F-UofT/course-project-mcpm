package org.hydev.mcpm.client.arguments;

import net.sourceforge.argparse4j.inf.ArgumentParserException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the CommandFactory
 *
 * @author Azalea (https://github.com/hykilpikonna)
 * @since 2022-11-21
 */
class CommandsFactoryTest
{
    @Test
    void test() throws ArgumentParserException
    {
        var ba = CommandsFactory.baseArgsParser();

        assertTrue(ba.getRawSubparsers().stream().anyMatch(it -> it.name().equals("list")));
        assertTrue(ba.getRawSubparsers().stream().noneMatch(it -> it.name().equals("load")));
    }
}
