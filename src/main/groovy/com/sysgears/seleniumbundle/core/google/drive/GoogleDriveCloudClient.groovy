package com.sysgears.seleniumbundle.core.google.drive

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.FileContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import com.google.common.collect.Iterables
import com.sysgears.seleniumbundle.core.conf.Config
import org.apache.commons.io.FilenameUtils

import javax.activation.MimetypesFileTypeMap

/**
 * Provides low-level methods to work with Google Drive.
 */
class GoogleDriveCloudClient {

    /**
     * Mime type of a folder.
     */
    private static final String FOLDER_MIME_TYPE = "application/vnd.google-apps.folder"

    /**
     * Instance of JsonFactory to be used by Google Drive API.
     */
    private final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance()

    /**
     * If modifying scopes, delete your previously saved credentials.
     */
    private final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE)

    /**
     * Instance of HTTP transport to be used by Google Drive API.
     */
    private final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()

    /**
     * Project properties.
     */
    private final Config conf = Config.instance

    /**
     * Instance of service to be used by Google Drive API.
     */
    final Drive service

    /**
     * Creates an instance of GoogleDriveCloudClient.
     */
    GoogleDriveCloudClient() {
        service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(conf.google.drive.applicationName)
                .build()
    }

    /**
     * Main method to download a file.
     *
     * @param fileId id of a file to be downloaded
     * @param pathToSave path where the file has to be saved locally
     *
     * @throws IOException I/O exception
     */
    void downloadFileById(String fileId, String pathToSave) throws IOException {

        def localFile = new java.io.File(pathToSave)
        localFile.getParentFile().mkdirs()

        FileOutputStream fos = new FileOutputStream(localFile)
        OutputStream outputStream = new ByteArrayOutputStream()

        service.files().get(fileId).executeMediaAndDownloadTo(outputStream)
        outputStream.writeTo(fos)
    }

    /**
     * Main method to upload a file. If parentId is not provided, the file will be saved to root folder.
     *
     * @param pathToFile path to a local file to be uploaded
     * @param parentId id of the parent folder to upload the file to
     *
     * @throws IOException I/O exception
     */
    void uploadFileToParentFolder(String pathToFile, String parentId = null) throws IOException {
        parentId = parentId ? parentId : getRootFolderId()
        def fileName = new java.io.File(pathToFile).getName()

        File fileMetadata = new File()
        fileMetadata.setName(fileName)
        fileMetadata.setParents(Collections.singletonList(parentId))

        FileContent mediaContent = new FileContent(new MimetypesFileTypeMap().getContentType(fileName),
                new java.io.File(pathToFile))

        service.files().create(fileMetadata, mediaContent)
                .setFields("id, parents")
                .execute()
    }

    /**
     * Deletes a file with given fileId.
     *
     * @param fileId id of a file to delete
     *
     * @throws IOException I/O exception
     */
    void delete(String fileId) throws IOException {
        service.files().delete(fileId).execute()
    }

    /**
     * Gets File object for a file which is stored by a given path.
     *
     * @param path path to a file on Google Drive
     *
     * @return File object
     *
     * @throws IOException I/O exception
     */
    File getFileByPath(String path) throws IOException {
        getAllFilesInFolder(getFolder(FilenameUtils.getPath(path)).getId()).find {
            it.getName() == FilenameUtils.getName(path)
        }
    }

    /**
     * Get path to a file from the root folder on Drive.
     *
     * @param fileId id of a file to get path to
     *
     * @return path to file on Drive relatively to Drive root folder
     *
     * @throws IOException I/O exception
     */
    String getPathByFileId(String fileId) throws IOException {
        def file = getFileById(fileId)
        def parent = getParentFor(fileId)
        def closingElement = file.getMimeType() == FOLDER_MIME_TYPE ? java.io.File.separator : ""

        parent && fileId != getRootFolderId() ?
                "${getPathByFileId(parent.getId())}${file.getName()}$closingElement" :
                ""
    }

    /**
     * Gets all (including nested) files in a folder recursively by the given folderId.
     *
     * @param folderId id of a folder to search for files in
     *
     * @return List of File object
     *
     * @throws IOException I/O exception
     */
    List<File> getAllFilesInFolder(String folderId) throws IOException {
        getFilesByParent(folderId).findResults {
            it.getMimeType() != FOLDER_MIME_TYPE ? it : getAllFilesInFolder(it.getId())
        }?.flatten() as List<File>
    }

    /**
     * Gets File object of the last folder in the path.
     *
     * @param path path to get the last folder from
     *
     * @return File object of the last folder in the given path
     *
     * @throws IOException I/O exception
     */
    File getFolder(String path) throws IOException {
        def folderNames = path.split(java.io.File.separator)
        def currentFolderId = getRootFolderId()

        folderNames.each { currentFolderName ->
            currentFolderId = getFilesByParent(currentFolderId).find {
                it.getName() == currentFolderName
            }.getId()
        }

        getFileById(currentFolderId)
    }

    /**
     * Creates folders if they are not created yet.
     *
     * @param path hierarchy of folders which has to be created
     * @param parentId id of a parent folder, if parent id is not specified, the hierarchy will be created relatively
     * to root folder
     *
     * @throws IOException I/O exception
     */
    String createFolders(String path) throws IOException {
        def folderNames = path.split(java.io.File.separator)
        def currentFolderId = getRootFolderId()

        folderNames.each { currentFolderName ->
            currentFolderId = getFolderByParent(currentFolderName, currentFolderId)?.getId() ?:
                    createFolder(currentFolderName, currentFolderId).getId()
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
     *
     * @throws IOException I/O exception
     */
    private File createFolder(String name, String parentId = null) throws IOException {
        File fileMetadata = new File()
                .setName(name)
                .setMimeType(FOLDER_MIME_TYPE)
                .setParents([parentId ? parentId : getRootFolderId()])

        service.files().create(fileMetadata)
                .setFields("id")
                .execute()
    }

    /**
     * Gets id of root folder of the Drive.
     *
     * @return id of the root folder
     *
     * @throws IOException I/O exception
     */
    private String getRootFolderId() throws IOException {
        service.files().get("root").execute().getId()
    }

    /**
     * Gets File object for given fileId.
     *
     * @param fileId id of a file to get
     *
     * @return File object
     *
     * @throws IOException I/O exception
     */
    private File getFileById(String fileId) throws IOException {
        service.files().get(fileId)
                .setFields("parents, id, name, kind, mimeType, trashed")
                .execute()
    }

    /**
     * Gets list of files which have the given parentId.
     *
     * @param parentId id of a parent file
     *
     * @return List of Files
     *
     * @throws IOException I/O exception
     */
    private List<File> getFilesByParent(String parentId) throws IOException {
        service.files().list()
                .setQ("'$parentId' in parents")
                .setFields("files(kind, trashed, parents, id, name, mimeType)")
                .execute()
                .getFiles()
                .findAll { !it.getTrashed() }
    }

    /**
     * Gets parent of a file with given fileId.
     *
     * @param fileId id of a file to find a parent for
     *
     * @return File object which represents parent for given file
     */
    private File getParentFor(String fileId) {
        def parents = getFileById(fileId).getParents()
        def parentId = Iterables.getOnlyElement(parents)

        parentId ? getFileById(parentId) : null
    }

    /**
     * Gets File instance of a folder by folder name and its parent id.
     *
     * @param name name of a folder
     * @param parentId id of a parent file
     *
     * @return File object of the folder
     */
    private File getFolderByParent(String name, String parentId) {
        getFilesByParent(parentId).find {
            it.getName() == name && it.getMimeType() == FOLDER_MIME_TYPE && !it.getTrashed()
        }
    }

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport
     *
     * @return An authorized Credential object
     *
     * @throws IOException If there is no client_secret
     */
    private Credential getCredentials(NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets
        InputStream inputStream = this.class.getClassLoader().getResourceAsStream(conf.google.drive.clientSecret)
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(inputStream))

        // Build flow and trigger user authorization request
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
                clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(conf.google.drive.credentials as String)))
                .setAccessType("offline")
                .build()
        new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user")
    }
}