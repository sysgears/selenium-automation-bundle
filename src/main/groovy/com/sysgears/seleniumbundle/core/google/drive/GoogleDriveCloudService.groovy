package com.sysgears.seleniumbundle.core.google.drive

import com.sysgears.seleniumbundle.core.utils.FileHelper

/**
 * Provides methods to interact with files on Google Drive.
 */
class GoogleDriveCloudService {

    /**
     * Instance of GoogleDriveCloudClient.
     */
    private GoogleDriveCloudClient client = new GoogleDriveCloudClient()

    /**
     * Downloads a file from Google Drive.
     *
     * @param remotePath path to a file to download
     * @param localPath path where the file will be saved locally
     */
    void downloadFile(String remotePath, String localPath) {
        client.downloadFileById(client.getFileByPath(remotePath).getId(), localPath)
    }

    /**
     * Downloads all files stored in remote path on Google Drive.
     *
     * @param remotePath path to files on Google Drive
     * @param localPath local path to download the files to
     */
    void downloadFiles(String remotePath, String localPath) {
        def folderId = client.getFolder(remotePath).getId()

        client.getFilesInFolder(folderId).each {
            def pathOnRemote = client.getPathFromRootFolderFor(it.getId())

            client.downloadFileById(it.getId(), localPath + (pathOnRemote - remotePath))
        }
    }

    /**
     * Uploads a file to Google Drive.
     *
     * @param localPath path to a local file to be uploaded
     * @param remotePath path to the file on Google Drive
     */
    void uploadFile(String localPath, String remotePath) {
        def pathToFileDirectory = new File(remotePath).getParentFile().getPath()

        if (!client.createFolders(pathToFileDirectory)) {
            def fileOnDrive = client.getFileByPath(remotePath)

            if (fileOnDrive && !fileOnDrive.getTrashed()) {
                client.delete(fileOnDrive.getId())
            }
        }

        client.uploadFileToParentFolder(localPath, client.getFolder(pathToFileDirectory).getId())
    }

    /**
     * Uploads all files stored in local path to Google Drive.
     *
     * @param localPath
     * @param remotePath
     */
    void uploadFiles(String localPath, String remotePath) {

        FileHelper.getFiles(localPath).each {
            uploadFiles(it.path, remotePath + (it.path - localPath))
        }
    }
}