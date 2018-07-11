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
import groovy.util.logging.Slf4j

import javax.activation.MimetypesFileTypeMap

/**
 * Provides low-level methods to work with Google Drive.
 */
@Slf4j
class DriveClient {

    /**
     * Instance of JsonFactory to be used by Google Drive API.
     */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance()

    /**
     * If modifying scopes, delete your previously saved credentials.
     */
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE)

    /**
     * Mime type of a folder.
     */
    private static final String FOLDER_MIME_TYPE = "application/vnd.google-apps.folder"

    /**
     * Instance of service to be used by Google Drive API.
     */
    private Drive service

    /**
     * Instance of HTTP transport to be used by Google Drive API.
     */
    private final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()

    /**
     * Project properties.
     */
    private final Config conf = Config.instance

    /**
     * Creates an instance of DriveClient.
     */
    DriveClient() {
        this.service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(conf.google.drive.applicationName)
                .build()
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
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(conf.google.drive.credentials as String)))
                .setAccessType("offline")
                .build()
        new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user")
    }

    /**
     * Gets id of root folder of the Drive.
     *
     * @return id of the root folder
     */
    String getRootFolderId() {
        service.files().get("root").execute().getId()
    }

    /**
     * Gets File object for given fileId.
     *
     * @param fileId id of a file to get
     *
     * @return File object
     */
    File getFileById(String fileId) {
        service.files().get(fileId).set("fields", "parents, id, name, kind, mimeType, trashed").execute()
    }

    /**
     * Gets File object for a file which is stored by a given path.
     *
     * @param path path to a file on Google Drive
     *
     * @return File object
     */
    File getFileByPath(String path) {
        def folderId = getFolder(path.substring(0, path.lastIndexOf("/"))).getId()

        getFilesInFolder(folderId).find {
            it.getName() == new java.io.File(path).getName()
        }
    }

    /**
     * Gets list of files which have the given parentId.
     *
     * @param parentId id of a parent file
     *
     * @return List of Files
     */
    List<File> getFilesByParent(String parentId) {
        service.files().list().setQ("'" + parentId + "' in parents").set("fields", "files(kind, trashed, parents, " +
                "id, name, mimeType)").execute().getFiles().findAll {
            !it.getTrashed()
        }
    }

    /**
     * Gets parent of a file with given fileId.
     *
     * @param fileId id of a file to find a parent for
     *
     * @return File object which represents parent for given file
     */
    File getParentFor(String fileId) {
        def parents = getFileById(fileId).getParents()
        def parentId = Iterables.getOnlyElement(parents)

        getFileById(parentId)
    }

    /**
     * Gets files in a folder by the given folderId.
     *
     * @param folderId id of a folder to search for files in
     *
     * @return List of File object
     */
    List<File> getFilesInFolder(String folderId) {
        getFilesByParent(folderId).findResults {
            it.getMimeType() != FOLDER_MIME_TYPE ? it : getFilesInFolder(it.getId())
        }?.flatten() as List<File>
    }

    /**
     * Gets File object of the last folder in the path.
     *
     * @param path path to get the last folder from
     *
     * @return File object of the last folder in the given path
     */
    File getFolder(String path) {
        def paths = path.split("/").toList().reverse()
        def parentId = getRootFolderId()

        while (paths) {
            def currentFolderName = paths.pop()

            parentId = getFilesByParent(parentId).find {
                it.getName() == currentFolderName
            }.getId()
        }

        getFileById(parentId)
    }

    /**
     * Gets File instance of a folder by folder name and its parent id.
     *
     * @param name name of a folder
     * @param parentId id of a parent file
     *
     * @return File object of the folder
     */
    File getFolderByParent(String name, String parentId) {
        getFilesByParent(parentId).find {
            it.getName() == name && it.getMimeType() == FOLDER_MIME_TYPE && !it.getTrashed()
        }
    }

    /**
     * Get path to a file from root folder on Drive.
     *
     * @param fileId id of a file to get path to
     *
     * @return path to file on Drive relatively to Drive root folder
     */
    String getPathFromRootFolderFor(String fileId) {
        def path = [getFileById(fileId).getName()]
        def parentId = fileId
        def rootFolderId = getRootFolderId()

        while (parentId != rootFolderId) {
            def parentFile = getParentFor(parentId)

            if (parentFile.getId() == rootFolderId) {
                break
            }

            parentId = parentFile.getId()
            path << parentFile.getName()
        }

        path.reverse().join("/")
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
        service.files().get(fileId).executeMediaAndDownloadTo(outputStream)
        outputStream.writeTo(fos)
    }

    /**
     * Main method to upload a file. If parentId is not provided, the file will be saved to root folder.
     *
     * @param pathToFile path to local file to be uploaded
     * @param parentId id of the parent folder to upload the file to
     */
    void uploadFileToParentFolder(String pathToFile, String parentId = null) {
        parentId = parentId ? parentId : getRootFolderId()
        def fileName = new java.io.File(pathToFile).getName()

        // create File object
        File fileMetadata = new File()
        fileMetadata.setName(fileName)
        fileMetadata.setParents(Collections.singletonList(parentId))

        // read local file
        FileContent mediaContent = new FileContent(new MimetypesFileTypeMap().getContentType(fileName),
                new java.io.File(pathToFile))

        // uploading process
        service.files().create(fileMetadata, mediaContent)
                .setFields("id, parents")
                .execute()
    }

    /**
     * Deletes files by a given fileId.
     *
     * @param fileId id of a file to delete
     */
    void delete(String fileId) {
        service.files().delete(fileId).execute()
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
                .setParents([parentId ? parentId : getRootFolderId()])

        // creation process
        service.files().create(fileMetadata)
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
    boolean createFolders(String path, String parentId = null) {
        def folderNames = path.split("/").toList().reverse()
        parentId = parentId ? parentId : getRootFolderId()
        def created = false

        while (folderNames) {
            def currentFolderId = getFolderByParent(folderNames.last(), parentId)?.getId()

            if (!currentFolderId) {
                parentId = createFolder(folderNames.pop(), parentId).getId()
                created = true
            } else {
                parentId = currentFolderId
                folderNames.pop()
            }
        }

        created
    }
}