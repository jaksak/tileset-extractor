package pl.longhorn.tilesetextractor.map.painter;

import java.awt.image.BufferedImage;

public class DiffPainter {

    public static BufferedImage paint(BufferedImage base, BufferedImage toCompare) {
        int width = base.getWidth();
        int height = base.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int baseRgb = base.getRGB(x, y);
                int toCompareRgb = toCompare.getRGB(x, y);
                if (baseRgb != toCompareRgb) {
                    result.setRGB(x, y, baseRgb);
                }
            }
        }
        return result;
    }
}
