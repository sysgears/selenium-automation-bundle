package com.sysgears.seleniumbundle.core.google.drive

import com.google.api.client.http.FileContent
import com.sysgears.seleniumbundle.core.utils.FileHelper
import com.sysgears.seleniumbundle.core.utils.PathHelper

import javax.activation.MimetypesFileTypeMap

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
     * Main method to download a file.
     *
     * @param fileId id of a file to be downloaded
     * @param pathToSave path where a file has to be saved locally
     */
    void downloadFileById(String fileId, String pathToSave) {
        // create folders for new file
        def localFile = new java.io.File(pathToSave)
        localFile.getParentFile().mkdirs()

        // open streams
        FileOutputStream fos = new FileOutputStream(localFile)
        OutputStream outputStream = new ByteArrayOutputStream()

        // downloading process
        client.service.files().get(fileId).executeMediaAndDownloadTo(outputStream)
        outputStream.writeTo(fos)
    }

    /**
     * Downloads a file from Google Drive.
     *
     * @param path path to a file to download
     * @param pathToSave path where the file will be saved locally
     */
    void downloadFile(String path, String pathToSave) {

        //get id of a file to download
        def fileId = client.getFilesInDirectory(
                client.getFolder(path.substring(0, path.lastIndexOf("/"))).getId()).find {
            it.getName() == client.getStringValueAfterLastSlash(path)
        }.getId()

        downloadFileById(fileId, pathToSave)
    }

    /**
     * Downloads all files from given folder on Google Drive. Copies the exact hierarchy of folders and files.
     *
     * @param directoryPath path to folder from which the files will be downloaded from
     * @param localPath path where the files will be saved locally
     */
    void downloadFiles(String directoryPath, String localPath) {
        client.getFilesInDirectory(client.getFolder(directoryPath).getId()).each {
            def remotePath = client.getPathFromRootFolderFor(it.getId()) - directoryPath

            downloadFileById(it.getId(), "$localPath/$remotePath")
        }
    }

    /**
     * Downloads all files from Google Drive. Copies the exact hierarchy of folders and files.
     *
     * @param localPath path where the files will be saved locally
     */
    void downloadAllFiles(String localPath) {
        client.getFilesInDirectory(client.getRootFolderId()).each {
            def remotePath = client.getPathFromRootFolderFor(it.getId())

            downloadFileById(it.getId(), "$localPath${java.io.File.separator}$remotePath")
        }
    }

    /**
     * Main method to create a folder.
     *
     * @param name name of a folder
     * @param parentId name of a parent folder, if parent id is not specified, the folder will be created relatively
     * to root folder
     *
     * @return created File object
     */
    File createFolder(String name, String parentId = null) {

        // create File object
        File fileMetadata = new File()
                .setName(name)
                .setMimeType("application/vnd.google-apps.folder")
                .setParents([parentId ? parentId : client.getRootFolderId()])

        // create process
        client.service.files().create(fileMetadata)
                .setFields("id")
                .execute()
    }

    /**
     * Creates folders if they are not created yet.
     *
     * @param path hierarchy of folders which has to be created
     * @param parentId id of a parent folder, if parent id is not specified, the hierarchy will be created relatively
     * to root folder
     */
    void createFolders(String path, String parentId = null) {
        def folderNames = path.split("/").toList().reverse()
        parentId = parentId ? parentId : client.getRootFolderId()

        while (folderNames) {
            def currentFolderId = client.getFolderByParent(folderNames.last(), parentId)?.getId()

            if (!currentFolderId) {
                parentId = createFolder(folderNames.pop(), parentId).getId()
            } else {
                parentId = currentFolderId
                folderNames.pop()
            }
        }
    }

    /**
     * Main method to upload a file. If parentId is not provided, the file will be saved to root folder.
     *
     * @param pathToFile path to local file to be uploaded
     * @param parentId id of the parent folder to upload the file to
     */
    void uploadFileToParentFolder(String pathToFile, String parentId = null) {
        parentId = parentId ? parentId : client.getRootFolderId()
        def fileName = client.getStringValueAfterLastSlash(pathToFile)

        // create File object
        File fileMetadata = new File()
        fileMetadata.setName(fileName)
        fileMetadata.setParents(Collections.singletonList(parentId))

        // read local file
        FileContent mediaContent = new FileContent(new MimetypesFileTypeMap().getContentType(fileName),
                new java.io.File(pathToFile))

        // uploading process
        client.service.files().create(fileMetadata, mediaContent)
                .setFields("id, parents")
                .execute()
    }

    /**
     * Uploads given file to given folder on Google Drive.
     *
     * @param localPath path to a local file to be uploaded
     * @param remotePath path to the file on Google Drive
     */
    void uploadFile(String localPath, String remotePath) {
        def pathToRemoteDirectory = remotePath.substring(0, remotePath.lastIndexOf("/") + 1)

        createFolders(pathToRemoteDirectory)

        uploadFileToParentFolder(localPath, client.getFolder(pathToRemoteDirectory).getId())
    }

    /**
     * Uploads all files in given repository.
     *
     * @param localPath path to a local directory from which to upload files
     * @param remotePath path on Google Drive to save files to
     */
    void uploadFiles(String localPath, String remotePath) {
        def pathsToFiles = FileHelper.getFiles(localPath).collect {
            it.path
        }

        pathsToFiles.each {
            uploadFile(it, "$remotePath/${PathHelper.getRelativePath(it, localPath)}")
        }
    }
}
