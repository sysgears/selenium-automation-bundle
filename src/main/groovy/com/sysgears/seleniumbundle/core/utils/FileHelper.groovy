package com.sysgears.seleniumbundle.core.utils

import groovy.util.logging.Slf4j

import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

/**
 * Utility class that provides methods to work with the file system.
 */
@Slf4j
class FileHelper {

    /**
     * Deletes a file by a given path.
     *
     * @param path to a file
     *
     * @throws IOException if an I/O error occurs
     */
    static void deleteFile(String path) throws IOException {
        try {
            Files.delete(Paths.get(path))
        } catch (IOException e) {
            log.error("File was not deleted by the path $path", e)
            throw new IOException("File was not deleted by the path $path", e)
        }
    }

    /**
     * Moves the file, to the new given path.
     *
     * @param from current file location
     * @param to where to move a file
     * @param copyOption option to be used, default is REPLACE_EXISTING
     *
     * @throws IOException if an I/O error occurs
     */
    static void moveFile(String from, String to, StandardCopyOption copyOption = StandardCopyOption.REPLACE_EXISTING)
            throws IOException {
        try {
            new File(to).getParentFile().mkdirs()
            Files.move(Paths.get(from), Paths.get(to), copyOption)
        } catch (IOException e) {
            log.error("Unable to move a file from $from to $to", e)
            throw new IOException("Unable to move a file from $from to $to", e)
        }
    }

    /**
     * Returns a list of files in a given directory recursively.
     *
     * @param directory path to directory from where all the files will be taken
     *
     * @return list of files
     *
     * @throws IOException if an I/O error occurs
     */
    static List<File> getFiles(String directory) throws IOException {
        try {
            new File(directory)?.listFiles()?.toList()?.findResults {
                it.isFile() ? it : getFiles(it.path)
            }?.flatten() as List<File>
        } catch (SecurityException e) {
            log.error("Unable to get access to ${directory.path}")
            throw new IOException("Unable to get access to ${directory.path}", e)
        }
    }

    /**
     * Finds all files with a given file extension in a given folder.
     *
     * @param rootDirectory path to root directory to start the search from
     * @param extension filename extension
     *
     * @return list of files
     *
     * @throws IOException if an I/O error occurs
     */
    static List<File> getFiles(String rootDirectory, String extension) throws IOException {
        getFiles(rootDirectory).findAll {
            it.name.endsWith(".$extension")
        } as List<File>
    }

    /**
     * Returns a list of subdirectories in a given directory recursively.
     *
     * @param directory path to directory from where subdirectories will be taken
     *
     * @return list of files
     *
     * @throws IOException if an I/O error occurs
     */
    static List<File> getSubDirs(String directory) throws IOException {
        try {
            new File(directory)?.listFiles()?.toList()?.findResults {
                (it.isDirectory()) ? [it, getSubDirs(it.path)] : null
            }?.flatten() as List<File>
        } catch (SecurityException e) {
            log.error("Unable to get access to ${directory.path}")
            throw new IOException("Unable to get access to ${directory.path}", e)
        }
    }
}
