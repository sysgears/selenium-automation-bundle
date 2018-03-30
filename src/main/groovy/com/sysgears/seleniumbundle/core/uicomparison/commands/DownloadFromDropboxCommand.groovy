package com.sysgears.seleniumbundle.core.uicomparison.commands

import com.sysgears.seleniumbundle.core.command.AbstractCommand
import com.sysgears.seleniumbundle.core.conf.Config
import com.sysgears.seleniumbundle.core.dropbox.DropboxClient
import com.sysgears.seleniumbundle.core.implicitinit.annotations.ImplicitInit
import groovy.util.logging.Slf4j
import org.apache.commons.io.FilenameUtils

/**
 * Class which provides the method to download screenshots from Dropbox.
 */
@Slf4j
class DownloadFromDropboxCommand extends AbstractCommand {

    /**
     * Category of the downloaded screenshots.
     */
    @ImplicitInit(pattern = "baseline|difference|actual", isRequired = true)
    private List<String> categories

    /**
     * Instance of Dropbox Client to be used by the command.
     */
    private DropboxClient client = new DropboxClient()

    /**
     * Creates an instance of DownloadFromDropboxCommand.
     *
     * @param arguments map that contains command arguments
     * @param conf project properties
     *
     * @throws IllegalArgumentException is thrown in case a value is missing for a mandatory parameter or
     * the value doesn't match the validation pattern
     */
    DownloadFromDropboxCommand(Map<String, List<String>> arguments, Config conf) throws IllegalArgumentException {
        super(arguments, conf)
    }

    /**
     * Executes the command.
     *
     * @throws IOException in case Dropbox client operations produced an error
     */
    @Override
    void execute() throws IOException {
        categories.each { category ->
            def categoryPath = FilenameUtils.separatorsToSystem(conf.ui.path."$category")
            def remotePaths = client.dropboxPaths.findAll {
                it.matches(/^\/$category\/(.*).png\$/)
            }

            if (!remotePaths) {
                log.error("No $category files found on Dropbox.")
                throw new IOException("No $category files found on Dropbox.")
            }

            remotePaths.each { remotePath ->
                def localPath = "${categoryPath.substring(0, categoryPath.lastIndexOf('/'))}${remotePath}"

                client.downloadFile(remotePath, localPath)
            }
        }
    }
}