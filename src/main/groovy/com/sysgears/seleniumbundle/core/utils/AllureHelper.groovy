package com.sysgears.seleniumbundle.core.utils

import groovy.util.logging.Slf4j
import io.qameta.allure.Allure

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

@Slf4j
/**
 * Provides methods to attach additional data to Allure reports.
 */
class AllureHelper {

    /**
     * Attaches a given image with a name to an Allure report.
     *
     * @param name name of the image to be shown in the report
     * @param image image to be attached
     */
    void attach(String name, BufferedImage image) {
        Allure.addAttachment(name, imageToInputStream(image))
    }

    /**
     * Converts BufferedImage into the input stream.
     *
     * @param bufferedImage image to be converted
     *
     * @return input stream
     *
     * @throws IOException if writing to a file failed due to I/O error
     */
    private InputStream imageToInputStream(BufferedImage bufferedImage) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        try {
            ImageIO.write(bufferedImage, "jpg", baos)
            new ByteArrayInputStream(baos.toByteArray())
        } catch (IOException e) {
            log.error("I/O error while saving the file", e)
            throw new IOException("I/O error while saving the file", e)
        }
    }
}
