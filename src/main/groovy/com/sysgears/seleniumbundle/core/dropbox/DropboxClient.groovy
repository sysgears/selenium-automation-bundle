package com.sysgears.seleniumbundle.core.dropbox

import com.dropbox.core.BadRequestException
import com.dropbox.core.DbxException
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.DeleteErrorException
import com.sysgears.seleniumbundle.core.conf.Config
import com.sysgears.seleniumbundle.core.utils.PathHelper
import groovy.util.logging.Slf4j

/**
 * The Dropbox client. Provides methods to work with the Dropbox API.
 */
@Slf4j
class DropboxClient {

    /**
     * Project properties.
     */
    private final Config conf = Config.instance

    /**
     * Dropbox configuration.
     */
    private final DbxRequestConfig config = new DbxRequestConfig(conf.dropbox.clientIdentifier as String,
            conf.dropbox.userLocale as String)

    /**
     * Dropbox client.
     */
    private final DbxClientV2 client

    /**
     * Creates an instance of a custom DropboxClient.
     */
    DropboxClient() {
        client = new DbxClientV2(config, conf.dropbox.accessToken as String)
    }

    /**
     * Downloads a file from Dropbox.
     *
     * @param dropboxPath path to a file on Dropbox
     * @param downloadPath path to directory where to download the file
     *
     * @throws IOException if any error occurs while downloading the file from Dropbox
     */
    void downloadFile(String dropboxPath, String downloadPath) throws IOException {
        File file = new File(downloadPath)
        file.getParentFile().mkdirs()
        try {
            client.files().download(dropboxPath).download(new FileOutputStream(file))
        } catch (BadRequestException e) {
            log.error("Unable to download the file from $dropboxPath.", e)
            throw new IOException("Unable to download the file from $dropboxPath, invalid request", e)
        } catch (IOException | DbxException e) {
            log.error("Unable to download the file from $dropboxPath.", e)
            throw new IOException("Unable to download the file from $dropboxPath.", e)
        }
    }

    /**
     * Uploads a file to Dropbox.
     *
     * @param dropboxPath path for uploading a file to Dropbox
     * @param uploadPath local path to the file
     *
     * @throws IOException if any error occurs while uploading the file to Dropbox
     */
    void uploadFile(String dropboxPath, String uploadPath) throws IOException {
        dropboxPath = PathHelper.convertToUnixLike(dropboxPath)
        log.info("uploading $dropboxPath")
        try {
            client.files().uploadBuilder("$dropboxPath").uploadAndFinish(new FileInputStream(uploadPath))
        } catch (BadRequestException e) {
            log.error("Unable to upload the file from $uploadPath.", e)
            throw new IOException("Unable to upload the file from $uploadPath, invalid request", e)
        } catch (IOException | DbxException e) {
            log.error("Unable to upload a file from $uploadPath.", e)
            throw new IOException("Unable to upload the file from $uploadPath.", e)
        }
    }

    /**
     * Deletes the file saved in Dropbox.
     *
     * @param dropboxPath path to the file to be deleted
     *
     * @throws IOException if any error occurs while deleting the file on Dropbox
     */
    void deleteFile(String dropboxPath) throws IOException {
        dropboxPath = PathHelper.convertToUnixLike(dropboxPath)
        try {
            client.files().deleteV2("$dropboxPath")
        } catch (DeleteErrorException ignored) {
            // exception is thrown if there is no file of given path
        } catch (BadRequestException e) {
            log.error("Unable to delete the file from $dropboxPath.", e)
            throw new IOException("Unable to delete the file from $dropboxPath, invalid request", e)
        } catch (DbxException e) {
            log.error("Unable to delete a file from $dropboxPath.", e)
            throw new IOException("Unable to delete the file from $dropboxPath.", e)
        }
    }

    /**
     * Returns a list of paths starting from the root.
     *
     * @return list of paths to files on Dropbox
     *
     * @throws IOException if any error occurs while getting the Dropbox paths
     */
    List<String> getDropboxPaths() throws IOException {
        try {
            client.files().listFolderBuilder("").withRecursive(true).start().getEntries().collect {
                it.getPathLower()
            }
        } catch (BadRequestException e) {
            log.error("Unable to get the Dropbox paths.", e)
            throw new IOException("Unable to get the Dropbox paths, check the request settings.", e)
        } catch (DbxException e) {
            log.error("Unable to get the Dropbox paths.", e)
            throw new IOException("Unable to get the Dropbox paths.", e)
        }
    }
}
