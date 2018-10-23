package com.sysgears.seleniumbundle.core.uicomparison.commands

import com.sysgears.seleniumbundle.core.command.AbstractCommand
import com.sysgears.seleniumbundle.core.conf.Config
import com.sysgears.seleniumbundle.core.data.cloud.ICloudService
import com.sysgears.seleniumbundle.core.implicitinit.annotations.ImplicitInit
import com.sysgears.seleniumbundle.core.utils.ClassFinder
import org.apache.commons.io.FilenameUtils

/**
 * Class which provides the method to download screenshots from Dropbox.
 */
class DownloadFromCommand extends AbstractCommand {

    /**
     * Name of cloud service to be used.
     */
    @ImplicitInit(pattern = "googledrive|dropbox", isRequired = true)
    private String service

    /**
     * Category of the downloaded screenshots.
     */
    @ImplicitInit(pattern = "baseline|difference|actual", isRequired = true)
    private List<String> categories

    /**
     * Instance of Cloud Client to be used by the command.
     */
    private ICloudService serviceInstance

    /**
     * Creates an instance of DownloadFromCommand.
     *
     * @param arguments map that contains command arguments
     * @param conf project properties
     *
     * @throws IllegalArgumentException is thrown in case a value is missing for a mandatory parameter or
     * the value doesn't match the validation pattern
     */
    DownloadFromCommand(Map<String, List<String>> arguments, Config conf) throws IllegalArgumentException {
        super(arguments, conf)

        serviceInstance = ClassFinder.findCloudService(service, conf)
    }

    /**
     * Executes the command.
     */
    @Override
    void execute() {
        categories.each { category ->
            serviceInstance.downloadFiles(category, FilenameUtils.separatorsToSystem(conf.ui.path."$category"))
        }
    }
}