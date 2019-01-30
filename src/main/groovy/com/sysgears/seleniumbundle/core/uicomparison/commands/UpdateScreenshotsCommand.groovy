package com.sysgears.seleniumbundle.core.uicomparison.commands

import com.sysgears.seleniumbundle.core.command.AbstractCommand
import com.sysgears.seleniumbundle.core.conf.Config
import com.sysgears.seleniumbundle.core.utils.FileHelper
import com.sysgears.seleniumbundle.core.utils.PathHelper
import groovy.util.logging.Slf4j

/**
 * The class that provides the method to update the baseline screenshots.
 */
@Slf4j
class UpdateScreenshotsCommand extends AbstractCommand {

    /**
     * Path to the root folder with the baseline screenshots.
     */
    private String baselinePath

    /**
     * Path to the root folder of the resulting screenshots.
     */
    private String actualPath

    /**
     * Creates an instance of UpdateScreenshotsCommand.
     *
     * @param arguments map with arguments of the command
     * @param conf project properties
     *
     * @throws IllegalArgumentException if a value is missing for a mandatory parameter or
     * the value does not match the validation pattern
     */
    UpdateScreenshotsCommand(Map<String, List<String>> arguments, Config conf) throws IllegalArgumentException {
        super(arguments, conf)
        baselinePath = conf.ui.path.baseline
        actualPath = conf.ui.path.actual
    }

    /**
     * Executes the command. Moves screenshots from the "actual/" to the "baseline/" directory.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    void execute() throws IOException {

        def baselineScreenshots = FileHelper.getFiles(baselinePath)
        def actualScreenshots = FileHelper.getFiles(actualPath)

        if (!actualScreenshots) {
            log.error("No actual screenshots found.")
            throw new IOException("No actual screenshots found.")
        }

        def baselinePaths = baselineScreenshots.collect { PathHelper.getRelativePath(it.path, baselinePath) }
        def actualPaths = actualScreenshots.collect { PathHelper.getRelativePath(it.path, actualPath) }

        baselinePaths.findAll {
            actualPaths.contains(it)
        }.each {
            FileHelper.deleteFile("$baselinePath${it}")
        }

        log.info ("Moving ${actualPaths.size()} screenshots...")

        actualPaths.each {
            FileHelper.moveFile("$actualPath${it}", "$baselinePath${it}")
        }

        log.info ("Done.")

        new File(actualPath).deleteDir()
    }
}
