package pl.longhorn.tileset.extractor.comparator.image.data;

import java.awt.image.BufferedImage;

public interface CachedImage {
    int getWidth();

    int getHeight();

    BufferedImage getImage();

    int getRGB(int x, int y);
}
