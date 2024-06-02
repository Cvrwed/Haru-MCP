package cc.unknown.utils.network.waifu;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.minecraft.client.renderer.texture.DynamicTexture;

public class TextureUtil {
    public static DynamicTexture createDynamicTexture(File file) throws IOException {
        BufferedImage image = ImageIO.read(file);
        return new DynamicTexture(image);
    }
}
