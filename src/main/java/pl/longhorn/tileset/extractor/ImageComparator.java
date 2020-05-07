package pl.longhorn.tileset.extractor;

import lombok.val;

import java.awt.image.BufferedImage;

public class ImageComparator {

    public ImageComparisonResult compare(BufferedImage baseImage, BufferedImage comparedImage) {
        checkDimension(baseImage, comparedImage);
        int height = baseImage.getHeight();
        int width = baseImage.getWidth();
        int identicalPixels = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                val baseImagePixel = baseImage.getRGB(x, y);
                val comparedImagePixel = comparedImage.getRGB(x, y);
                if (pixelIsAlpha(baseImagePixel) || pixelIsAlpha(comparedImagePixel)) {
                    continue;
                }

                if (baseImagePixel == comparedImagePixel) {
                    identicalPixels++;
                }
            }
        }
        return new ImageComparisonResult(identicalPixels);
    }

    private boolean pixelIsAlpha(int pixelRGB) {
        int alphaColor = (pixelRGB >> 24) & 0xFF;
        return alphaColor == 0;
    }

    private void checkDimension(BufferedImage baseImage, BufferedImage comparedImage) {
        if (baseImage.getWidth() != comparedImage.getWidth() || baseImage.getHeight() != comparedImage.getHeight()) {
            throw new InvalidDimensionException();
        }
    }
}
