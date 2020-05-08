package pl.longhorn.tileset.extractor.comparator;

import lombok.val;
import pl.longhorn.tileset.extractor.comparator.image.data.CachedImage;

import java.util.LinkedList;
import java.util.List;

import static pl.longhorn.tileset.extractor.ImageHelper.pixelIsAlpha;

public class ImageComparator {

    public ImageComparisonResult compare(ImageComparatorParam param) {
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
                if (pixelIsAlpha(baseImagePixel) || pixelIsAlpha(comparedImagePixel)) {
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

    private void checkDimension(CachedImage baseImage, CachedImage comparedImage) {
        if (baseImage.getWidth() != comparedImage.getWidth() || baseImage.getHeight() != comparedImage.getHeight()) {
            throw new InvalidDimensionException();
        }
    }
}
