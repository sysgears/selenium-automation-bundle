package com.sysgears.seleniumbundle.core.command

import com.sysgears.seleniumbundle.core.conf.Config
import com.sysgears.seleniumbundle.core.implicitinit.ParameterMapper
import com.sysgears.seleniumbundle.core.implicitinit.annotations.ImplicitInit
import groovy.util.logging.Slf4j

/**
 * Abstract class to be extended by the classes that provide implementations for the bundle commands.
 */
@Slf4j
abstract class AbstractCommand implements ICommand {

    /**
     * Project properties.
     */
    protected final Config conf

    /**
     * Creates a new Command instance by validating and implicitly mapping given arguments to the command
     * object fields annotated with {@link ImplicitInit}.
     *
     * @param arguments map that contains command arguments
     * @param conf project properties
     *
     * @throws IllegalArgumentException if a value is missing for a mandatory argument or the value doesn't
     * match the validation pattern
     */
    AbstractCommand(Map<String, ?> arguments, Config conf) throws IllegalArgumentException {
        this.conf = conf

        new ParameterMapper().initParameters(this, arguments)
    }

    /**
     * Abstract method to be implemented by subclasses for executing the command.
     */
    abstract void execute()
}