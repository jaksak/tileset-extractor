package pl.longhorn.tilesetextractor.comparator;

import lombok.Value;
import pl.longhorn.tilesetextractor.comparator.image.data.CachedImage;

import java.util.List;

@Value
public class ImageComparatorParam {
    private final CachedImage baseImage;
    private final CachedImage comparedImage;
    private final List<Pixel> ignoredPixels;
}
