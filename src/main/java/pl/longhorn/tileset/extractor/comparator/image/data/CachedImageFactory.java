package pl.longhorn.tileset.extractor.comparator.image.data;

import pl.longhorn.tileset.extractor.ImageHelper;

import java.awt.image.BufferedImage;

public class CachedImageFactory {
    public static CachedImage create(BufferedImage image) {
        int[][] rgbCache = ImageHelper.getRgb(image);
        int height = image.getHeight();
        int width = image.getWidth();
        return new CachedImage() {
            @Override
            public int getWidth() {
                return width;
            }

            @Override
            public int getHeight() {
                return height;
            }

            @Override
            public BufferedImage getImage() {
                return image;
            }

            @Override
            public int getRGB(int x, int y) {
                return rgbCache[x][y];
            }
        };
    }
}
