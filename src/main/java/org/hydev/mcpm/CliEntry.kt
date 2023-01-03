package org.hydev.mcpm

import net.sourceforge.argparse4j.inf.ArgumentParserException
import org.hydev.mcpm.client.arguments.ArgsParserFactory
import org.hydev.mcpm.utils.ColorLogger

/**
 * CLI Entry point
 *
 * @author Azalea (https://github.com/hykilpikonna)
 * @since 2023-01-02
 */
fun main(args: Array<String>)
{
    val parser = ArgsParserFactory.baseArgsParser()

    try
    {
        parser.parse(args, ColorLogger.toStdOut())
    }
    catch (e: ArgumentParserException)
    {
        parser.fail(e, ColorLogger.toStdOut())
    }
}