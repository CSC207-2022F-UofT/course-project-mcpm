package org.hydev.mcpm.client.arguments;

import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;

/**
 * Temporary exception thrown to store a help message
 *
 * @author Azalea (https://github.com/hykilpikonna)
 * @since 2022-11-20
 */
public class HelpException extends ArgumentParserException
{
    private final String help;

    public HelpException(ArgumentParser parser, String help)
    {
        super(parser);
        this.help = help;
    }

    public String help()
    {
        return help;
    }
}
