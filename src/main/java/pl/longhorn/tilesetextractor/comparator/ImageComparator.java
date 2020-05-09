package pl.longhorn.tilesetextractor.comparator;

import lombok.val;
import pl.longhorn.tilesetextractor.ImageHelper;
import pl.longhorn.tilesetextractor.comparator.image.data.CachedImage;

import java.util.LinkedList;
import java.util.List;

public class ImageComparator {

    public static ImageComparisonResult compare(ImageComparatorParam param) {
        val baseImage = param.getBaseImage();
        val comparedImage = param.getComparedImage();
        checkDimension(baseImage, comparedImage);
        int height = baseImage.getHeight();
        int width = baseImage.getWidth();
        int identicalPixelAmount = 0;
        int ignoredPixelAmount = 0;
        List<Pixel> usedPixels = new LinkedList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                val baseImagePixel = baseImage.getRGB(x, y);
                val comparedImagePixel = comparedImage.getRGB(x, y);
                if (ImageHelper.pixelIsAlpha(baseImagePixel) || ImageHelper.pixelIsAlpha(comparedImagePixel)) {
                    continue;
                }

                Pixel currentPixel = new Pixel(x, y);
                if (baseImagePixel == comparedImagePixel) {
                    if (param.getIgnoredPixels().contains(currentPixel)) {
                        ignoredPixelAmount++;
                    } else {
                        identicalPixelAmount++;
                        usedPixels.add(currentPixel);
                    }
                }
            }
        }
        return new ImageComparisonResult(identicalPixelAmount, ignoredPixelAmount, usedPixels);
    }

    private static void checkDimension(CachedImage baseImage, CachedImage comparedImage) {
        if (baseImage.getWidth() != comparedImage.getWidth() || baseImage.getHeight() != comparedImage.getHeight()) {
            throw new InvalidDimensionException();
        }
    }
}
