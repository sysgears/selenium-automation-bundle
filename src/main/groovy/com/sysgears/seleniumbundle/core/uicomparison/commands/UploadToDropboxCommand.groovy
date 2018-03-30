package com.sysgears.seleniumbundle.core.uicomparison.commands

import com.sysgears.seleniumbundle.core.command.AbstractCommand
import com.sysgears.seleniumbundle.core.conf.Config
import com.sysgears.seleniumbundle.core.dropbox.DropboxClient
import com.sysgears.seleniumbundle.core.implicitinit.annotations.ImplicitInit
import com.sysgears.seleniumbundle.core.utils.FileHelper
import groovy.util.logging.Slf4j
import org.apache.commons.io.FilenameUtils

/**
 * Class which provides the method to upload screenshots to Dropbox.
 */
@Slf4j
class UploadToDropboxCommand extends AbstractCommand {

    /**
     * Category of the uploaded screenshots.
     */
    @ImplicitInit(pattern = "baseline|difference|actual", isRequired = true)
    private List<String> categories

    /**
     * Instance of Dropbox Client to be used by the command.
     */
    private DropboxClient client = new DropboxClient()

    /**
     * Creates an instance of UploadToDropboxCommand.
     *
     * @param @param arguments the map with arguments of the command
     * @param conf project properties
     *
     * @throws IllegalArgumentException is thrown in case a value is missing for a mandatory parameter or
     * the value doesn't match the validation pattern
     */
    UploadToDropboxCommand(Map<String, List<String>> arguments, Config conf) throws IllegalArgumentException {
        super(arguments, conf)
    }

    /**
     * Executes the command.
     *
     * @throws IOException in case Dropbox client operations produce an error.
     */
    @Override
    void execute() throws IOException {
        categories.each { category ->
            def categoryPath = FilenameUtils.separatorsToSystem(conf.ui.path."$category")
            def localPaths = FileHelper.getFiles(categoryPath).collect { it.path }

            if (!localPaths) {
                log.error("No $category files found.")
                throw new IOException("No $category files found.")
            }

            localPaths.each { localPath ->
                def remotePath = localPath - categoryPath.substring(0, categoryPath.lastIndexOf(File.separator))

                // delete is a workaround due to issues with "withMode(WriteMode.OVERWRITE)"
                client.deleteFile(remotePath)
                client.uploadFile(remotePath, localPath)
            }
        }
    }
}
