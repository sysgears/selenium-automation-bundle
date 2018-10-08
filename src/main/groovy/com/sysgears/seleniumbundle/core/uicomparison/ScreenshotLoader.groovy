package com.sysgears.seleniumbundle.core.uicomparison

import groovy.util.logging.Slf4j

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

/**
 * Provides methods for saving and retrieving screenshots.
 */
@Slf4j
class ScreenshotLoader {

    /**
     * Saves buffered image to a file in PNG.
     *
     * @param bufferedImage image to be saved
     * @param path path to save the image
     *
     * @throws IOException if writing to file failed due to I/O error
     */
    void save(BufferedImage bufferedImage, String path) throws IOException {
        File newImage = new File(path)
        newImage.getParentFile().mkdirs()
        try {
            ImageIO.write(bufferedImage, "png", newImage)
        } catch (IOException e) {
            log.error("I/O error while saving the file", e)
            throw new IOException("I/O error while saving the file", e)
        }
    }

    /**
     * Retrieves a buffered image from the file located by a given path.
     *
     * @param path path to the file
     *
     * @return buffered image
     *
     * @throws IOException if retrieving a file has failed due to I/O error
     */
    BufferedImage retrieve(String path) throws IOException {
        try {
            ImageIO.read(new File(path))
        } catch (IOException e) {
            log.error("File is missing.", e)
            throw new IOException("File is missing.", e)
        }
    }
}
