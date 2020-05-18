package pl.longhorn.tileset.extractor.tileset;

import lombok.Value;
import pl.longhorn.tileset.extractor.comparator.image.data.CachedImage;

import java.awt.image.BufferedImage;

@Value
public class Tileset implements CachedImage {
    private int id;
    private BufferedImage image;
    private int nonAlphaPixels;
    private int groundProbability;
    private int[][] rgbCache;

    @Override
    public int getWidth() {
        return image.getWidth();
    }

    @Override
    public int getHeight() {
        return image.getHeight();
    }

    @Override
    public int getRGB(int x, int y) {
        return rgbCache[x][y];
    }
}
