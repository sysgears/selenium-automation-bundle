package com.sysgears.seleniumbundle.core.uicomparison.commands

import com.sysgears.seleniumbundle.core.command.AbstractCommand
import com.sysgears.seleniumbundle.core.conf.Config
import com.sysgears.seleniumbundle.core.data.cloud.ICloudService
import com.sysgears.seleniumbundle.core.implicitinit.annotations.ImplicitInit
import com.sysgears.seleniumbundle.core.utils.ClassFinder
import org.apache.commons.io.FilenameUtils

/**
 * Class which provides the method to upload screenshots to Dropbox.
 */
class UploadToCommand extends AbstractCommand {

    /**
     * Name of cloud service to be used.
     */
    @ImplicitInit(pattern = "googledrive|dropbox", isRequired = true)
    String service

    /**
     * Category of the uploaded screenshots.
     */
    @ImplicitInit(pattern = "baseline|difference|actual", isRequired = true)
    private List<String> categories

    /**
     * Instance of Cloud Client to be used by the command.
     */
    private ICloudService serviceInstance

    /**
     * Creates an instance of UploadToCommand.
     *
     * @param @param arguments the map with arguments of the command
     * @param conf project properties
     *
     * @throws IllegalArgumentException is thrown in case a value is missing for a mandatory parameter or
     * the value doesn't match the validation pattern
     */
    UploadToCommand(Map<String, List<String>> arguments, Config conf) throws IllegalArgumentException {
        super(arguments, conf)

        serviceInstance = ClassFinder.findCloudService(service, conf)
    }

    /**
     * Executes the command.
     */
    @Override
    void execute() throws IOException {
        categories.each { category ->

            serviceInstance.uploadFiles(category, FilenameUtils.separatorsToSystem(conf.ui.path."$category"))
        }
    }
}
