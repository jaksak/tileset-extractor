package pl.longhorn.tileset.extractor.comparator;

import lombok.Value;

import java.util.List;

@Value
public class ImageComparisonResult {
    private final int identicalPixelAmount;
    private final int ignoredPixelAmount;
    private final List<Pixel> usedPixels;
}
