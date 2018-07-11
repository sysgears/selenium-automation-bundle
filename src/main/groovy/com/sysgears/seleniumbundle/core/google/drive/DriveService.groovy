package com.sysgears.seleniumbundle.core.google.drive

/**
 * Provides methods to interact with files on Google Drive.
 */
class DriveService {

    /**
     * Instance of DriveClient.
     */
    private DriveClient client

    /**
     * Creates an instance of DriveService.
     *
     * @param client Google Drive Client to be used by the service
     */
    DriveService(DriveClient client) {
        this.client = client
    }

    /**
     * Downloads a file from Google Drive.
     *
     * @param path path to a file to download
     * @param pathToSave path where the file will be saved locally
     */
    void downloadFile(String path, String pathToSave) {

        //get id of a file to download
        def fileId = client.getFileByPath(path).getId()

        client.downloadFileById(fileId, pathToSave)
    }

    /**
     * Uploads given file to given folder on Google Drive.
     *
     * @param localPath path to a local file to be uploaded
     * @param remotePath path to the file on Google Drive
     */
    void uploadFile(String localPath, String remotePath) {
        def pathToRemoteDirectory = remotePath.substring(0, remotePath.lastIndexOf("/") + 1)

        if (!client.createFolders(pathToRemoteDirectory)) {
            def fileOnDrive = client.getFileByPath(remotePath)

            if (fileOnDrive && !fileOnDrive.getTrashed()) {
                client.delete(fileOnDrive.getId())
            }
        }

        client.uploadFileToParentFolder(localPath, client.getFolder(pathToRemoteDirectory).getId())
    }
}