package com.sysgears.seleniumbundle

import com.sysgears.seleniumbundle.core.command.CommandArgs
import com.sysgears.seleniumbundle.core.command.CommandFinder
import com.sysgears.seleniumbundle.core.conf.Config
import groovy.util.logging.Slf4j

/**
 * Main class of the application.
 * <p>
 * This class is responsible for parsing command line options,
 * as well as executing parsed command with its arguments.
 */
@Slf4j
class Main {

    /**
     * Executes Bundle application.
     *
     * @param args command line arguments
     */
    static void main(String[] args) {

        log.info("Starting application...")

        new CommandFinder(Config.instance).find(new CommandArgs(args)).execute()

        log.info("Terminating application...")
    }
}
