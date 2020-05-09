package pl.longhorn.tilesetextractor.comparator;

import lombok.Value;

import java.util.List;

@Value
public class ImageComparisonResult {
    private final int identicalPixelAmount;
    private final int ignoredPixelAmount;
    private final List<Pixel> usedPixels;
}
