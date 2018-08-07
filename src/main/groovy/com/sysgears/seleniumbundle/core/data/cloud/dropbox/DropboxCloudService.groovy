package com.sysgears.seleniumbundle.core.data.cloud.dropbox

import com.dropbox.core.BadRequestException
import com.dropbox.core.DbxException
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.DeleteErrorException
import com.sysgears.seleniumbundle.core.conf.Config
import com.sysgears.seleniumbundle.core.data.cloud.AbstractCloudService
import com.sysgears.seleniumbundle.core.utils.FileHelper
import com.sysgears.seleniumbundle.core.utils.PathHelper
import groovy.util.logging.Slf4j

/**
 * Client for Dropbox. Provides methods to work with Dropbox API.
 */
@Slf4j
class DropboxCloudService extends AbstractCloudService {

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
     * Creates an instance of a custom DropboxCloudService.
     */
    DropboxCloudService() {
        client = new DbxClientV2(config, conf.dropbox.accessToken as String)
    }

    /**
     * Downloads a file from Dropbox.
     *
     * @param remotePath path to a file on Dropbox
     * @param localPath path to download the file to
     *
     * @throws IOException if any error occurs while downloading file from Dropbox
     */
    @Override
    void downloadFile(String remotePath, String localPath) throws IOException {
        File file = new File(localPath)

        file.getParentFile().mkdirs()
        try {
            client.files().download(remotePath).download(new FileOutputStream(file))
        } catch (BadRequestException e) {
            log.error("Unable to download a file from $remotePath.", e)
            throw new IOException("Unable to download a file from $remotePath, invalid request", e)
        } catch (IOException | DbxException e) {
            log.error("Unable to download a file from $remotePath.", e)
            throw new IOException("Unable to download a file from $remotePath.", e)
        }
    }

    /**
     * Downloads all files stored in remote path on Dropbox.
     *
     * @param remotePath path to files on Dropbox
     * @param localPath local path to download the files to
     */
    @Override
    void downloadFiles(String remotePath, String localPath) {

        getDropboxPaths().each {
            downloadFile(it, localPath + it - remotePath)
        }
    }

    /**
     * Uploads a file to Dropbox.
     *
     * @param remotePath path for saving a file on Dropbox
     * @param localPath local path to a file
     *
     * @throws IOException if any error occurs while uploading file to Dropbox
     */
    @Override
    void uploadFile(String localPath, String remotePath) throws IOException {
        remotePath = PathHelper.convertToUnixLike(remotePath)

        try {
            client.files().uploadBuilder("$remotePath").uploadAndFinish(new FileInputStream(localPath))
        } catch (BadRequestException e) {
            log.error("Unable to upload a file from $localPath.", e)
            throw new IOException("Unable to upload a file from $localPath, invalid request", e)
        } catch (IOException | DbxException e) {
            log.error("Unable to upload a file from $localPath.", e)
            throw new IOException("Unable to upload a file from $localPath.", e)
        }
    }

    /**
     * Uploads all files stored in local path to Dropbox.
     *
     * @param localPath path to local directory
     * @param remotePath path for saving files on Dropbox
     */
    @Override
    void uploadFiles(String localPath, String remotePath) {

        FileHelper.getFiles(localPath).each {
            uploadFile(it.path, remotePath + it.path - localPath)
        }
    }

    /**
     * Deletes file saved in Dropbox path.
     *
     * @param dropboxPath path to the file to be deleted
     *
     * @throws IOException if any error occurs while deleting file on Dropbox
     */
    void deleteFile(String dropboxPath) throws IOException {
        dropboxPath = PathHelper.convertToUnixLike(dropboxPath)
        try {
            client.files().deleteV2("$dropboxPath")
        } catch (DeleteErrorException ignored) {
            // exception is thrown if there is no file of given path
        } catch (BadRequestException e) {
            log.error("Unable to delete a file from $dropboxPath.", e)
            throw new IOException("Unable to delete a file from $dropboxPath, invalid request", e)
        } catch (DbxException e) {
            log.error("Unable to delete a file from $dropboxPath.", e)
            throw new IOException("Unable to delete a file from $dropboxPath.", e)
        }
    }

    /**
     * Returns a list of paths starting from root.
     *
     * @return list of paths of files from Dropbox
     *
     * @throws IOException if any error occurs while getting Dropbox paths
     */
    List<String> getDropboxPaths() throws IOException {
        try {
            client.files().listFolderBuilder("").withRecursive(true).start().getEntries().collect {
                it.getPathLower()
            }
        } catch (BadRequestException e) {
            log.error("Unable to get dropbox paths.", e)
            throw new IOException("Unable to get dropbox paths, check request settings.", e)
        } catch (DbxException e) {
            log.error("Unable to get dropbox paths.", e)
            throw new IOException("Unable to get dropbox paths.", e)
        }
    }
}
