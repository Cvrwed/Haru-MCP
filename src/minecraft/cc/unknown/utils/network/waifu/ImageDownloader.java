package cc.unknown.utils.network.waifu;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

public class ImageDownloader {
    public static File downloadImage(String urlString, String fileName) throws IOException {
        URL url = new URL(urlString);
        BufferedImage image = ImageIO.read(url);
        
        String tempDir = System.getProperty("java.io.tmpdir");
        File tempFile = new File(tempDir, fileName);
        
        ImageIO.write(image, "png", tempFile);
        
        return tempFile;
    }
}
