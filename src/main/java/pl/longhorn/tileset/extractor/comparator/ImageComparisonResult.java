package pl.longhorn.tileset.extractor.comparator;

import lombok.Value;

import java.util.List;

@Value
public class ImageComparisonResult {
    private final int identicalPixels;
    private final List<Pixel> usedPixels;
}
