package com.sysgears.seleniumbundle.core.google.drive

import com.sysgears.seleniumbundle.core.utils.FileHelper
import org.apache.commons.io.FilenameUtils

/**
 * Provides methods to interact with files on Google Drive.
 */
class GoogleDriveCloudService {

    /**
     * Instance of GoogleDriveCloudClient.
     */
    GoogleDriveCloudClient client = new GoogleDriveCloudClient()

    /**
     * Downloads a file from Google Drive.
     *
     * @param remotePath path to a file to download
     * @param localPath path where the file will be saved locally
     *
     * @throws IOException  I/O exception
     */
    void downloadFile(String remotePath, String localPath) throws IOException {
        client.downloadFileById(client.getFileByPath(remotePath).getId(), FilenameUtils.separatorsToSystem(localPath))
    }

    /**
     * Downloads all files stored in remote path on Google Drive.
     *
     * @param remoteFolderPath path to a folder on Google Drive
     * @param localPath local path to download the files to
     *
     * @throws IOException  I/O exception
     */
    void downloadFiles(String remoteFolderPath, String localPath) throws IOException {
        def folderId = client.getFolder(remoteFolderPath).getId()

        client.getAllFilesInFolder(folderId).each {
            def remoteFilePath = client.getPathByFileId(it.getId())

            downloadFile(remoteFilePath,
                    FilenameUtils.separatorsToSystem(localPath) + (remoteFilePath - remoteFolderPath))
        }
    }

    /**
     * Uploads a file to Google Drive.
     *
     * @param localPath path to a local file to be uploaded
     * @param remotePath path to the file on Google Drive
     *
     * @throws IOException  I/O exception
     */
    void uploadFile(String localPath, String remotePath) throws IOException {
        def fileOnDrive = client.getFileByPath(remotePath)
        def parentId

        if (fileOnDrive && !fileOnDrive.getTrashed()) {
            parentId = fileOnDrive.getParents().first()
            client.delete(fileOnDrive.getId())
        } else {
            parentId = client.createFolders(FilenameUtils.getPath(remotePath))
        }

        client.uploadFileToParentFolder(FilenameUtils.separatorsToSystem(localPath), parentId)
    }

    /**
     * Uploads all files stored in local path to Google Drive.
     *
     * @param localPath path to a local folder to be uploaded
     * @param remotePath path to the remote folder on Google Drive
     *
     * @throws IOException  I/O exception
     */
    void uploadFiles(String localPath, String remotePath) throws IOException {

        FileHelper.getFiles(FilenameUtils.separatorsToSystem(localPath)).each {
            uploadFile(it.path, remotePath + (it.path - localPath))
        }
    }
}