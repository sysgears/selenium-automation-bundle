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
     * @param remotePath path to a file to download
     * @param localPath path where the file will be saved locally
     */
    void downloadFile(String remotePath, String localPath) {
        client.downloadFileById(client.getFileByPath(remotePath).getId(), localPath)
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
}