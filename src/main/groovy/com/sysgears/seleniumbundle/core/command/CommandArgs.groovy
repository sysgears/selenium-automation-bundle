package com.sysgears.seleniumbundle.core.command

import groovy.util.logging.Slf4j

/**
 * Holds command-line arguments.
 */
@Slf4j
class CommandArgs {

    /**
     * Command name.
     */
    final String name

    /**
     * Command arguments map.
     */
    final Map<String, ?> arguments

    /**
     * Constructs a new instance of CommandArgs object.
     *
     * @param args array that contains command arguments provided for bundle execution
     *
     * @throws IllegalArgumentException if a command name or command arguments are invalid
     */
    CommandArgs(String[] args) throws IllegalArgumentException {

        // get a list of all command line arguments including command name
        def cliArguments = (args && args.first()) ? args.first() : {
            log.error("No arguments were provided for the [run] command")
            throw new IllegalArgumentException("Please, provide the options to run the application.")
        }()

        name = (cliArguments =~ /(?<![-=:,])\b(\w+)\b/).with { matcher ->
            matcher.find() ? matcher[0][1] : ""
        }

        arguments = (cliArguments =~ /-([^=]+)=(\S+)/).with { matcher ->
            matcher.find() ? matcher.collect { List<String> it ->
                [(it[1]): it[2].split(",").toList()]
            }.collectEntries() : [:]
        }
    }
}
