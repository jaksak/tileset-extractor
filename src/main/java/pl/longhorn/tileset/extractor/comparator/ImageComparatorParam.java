package pl.longhorn.tileset.extractor.comparator;

import lombok.Value;

import java.awt.image.BufferedImage;
import java.util.List;

@Value
public class ImageComparatorParam {
    private final BufferedImage baseImage;
    private final BufferedImage comparedImage;
    private final List<Pixel> ignoredPixels;
}
