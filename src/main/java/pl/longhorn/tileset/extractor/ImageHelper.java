package pl.longhorn.tileset.extractor;

import lombok.val;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class ImageHelper {

    public static boolean pixelIsAlpha(int pixelRGB) {
        int alphaColor = (pixelRGB >> 24) & 0xFF;
        return alphaColor == 0;
    }

    public static BufferedImage getImage(String fileName) throws URISyntaxException, IOException {
        val file = getResourcePath(fileName).toFile();
        return ImageIO.read(file);
    }

    public static Stream<BufferedImage> split(BufferedImage image) {
        Stream.Builder<BufferedImage> stream = Stream.builder();
        int height = image.getHeight();
        int width = image.getWidth();
        for (int y = 0; y < height; y += ProjectConfig.TILESET_HEIGHT) {
            for (int x = 0; x < width; x += ProjectConfig.TILESET_WIDTH) {
                stream.add(image.getSubimage(x, y, ProjectConfig.TILESET_WIDTH, ProjectConfig.TILESET_HEIGHT));
            }
        }
        return stream.build();
    }

    public static Path getResourcePath(String directory) throws URISyntaxException {
        return Paths.get(ClassLoader.getSystemResource(directory).toURI());
    }

    public static int[][] getRgb(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] rgbs = new int[width][height];
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                rgbs[x][y] = image.getRGB(x, y);
            }
        }
        return rgbs;
    }
}
