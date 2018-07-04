package com.sysgears.seleniumbundle.core.google.drive

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.common.collect.Iterables

/**
 * Provides low-level methods to work with Google Drive.
 */
class DriveClient {

    // TODO Try to set null
    private static final String APPLICATION_NAME = "Test application"

    /**
     * Instance of JsonFactory to be used by Google Drive API.
     */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance()


    private static final String FOLDER_MIME_TYPE = "application/vnd.google-apps.folder"

    /**
     * Instance of HTTP transport to be used by Google Drive API.
     */
    private final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()

    /**
     * Instance of service to be used by Google Drive API.
     */
    Drive service

    /**
     * Path to directory where authorized credentials will be stored.
     */
    private static final String CREDENTIALS_FOLDER = "credentials"

    /**
     * If modifying scopes, delete your previously saved credentials/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE)

    /**
     * Path to file with client secret.
     */
    private static final String CLIENT_SECRET_DIR = "client_secret.json"

    /**
     * Creates an instance of DriveClient.
     */
    DriveClient() {
        this.service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
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
    static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets
        InputStream inputStream = this.getClassLoader().getResourceAsStream(CLIENT_SECRET_DIR)
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(inputStream))

        // Build flow and trigger user authorization request
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(CREDENTIALS_FOLDER)))
                .setAccessType("offline")
                .build()
        new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user")
    }

    /**
     * Get path to file from root folder on Drive.
     *
     * @param fileId id of a file to get path to
     *
     * @return path to file on Drive relatively to Drive root folder
     */
    String getPathFromRootFolderFor(String fileId) {
//        def path = getFileById(fileId).getName()
        def file = getFileById(fileId)
        def path = [file.getName()]
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

        path.reverse().join(java.io.File.separator)
    }

    /**
     * Gets parent for given fileId.
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
    List<File> getFilesInDirectory(String folderId) {
        getFilesByParent(folderId).findResults {
            it.getMimeType() != FOLDER_MIME_TYPE ? it : getFilesInDirectory(it.getId())
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

    File getFolderByParent(String name, String parentId) {
        getFilesByParent(parentId).find {
            log.info(it.toString())
            it.getName() == name && it.getMimeType() == FOLDER_MIME_TYPE
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
                "id, name, mimeType)").execute().getFiles()
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
     * TODO modify the comment
     * Gets the name of the last folder from given path which does not contain file in it.
     *
     * @param path path to extract the last folder name
     *
     * @return folder name of the last folder in the path
     */
    static String getStringValueAfterLastSlash(String path) {
        path.substring(path.lastIndexOf("/")) - "/"
    }
}
