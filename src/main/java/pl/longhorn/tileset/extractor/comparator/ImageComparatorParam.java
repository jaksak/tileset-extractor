package pl.longhorn.tileset.extractor.comparator;

import lombok.Value;
import pl.longhorn.tileset.extractor.comparator.image.data.CachedImage;

import java.util.List;

@Value
public class ImageComparatorParam {
    private final CachedImage baseImage;
    private final CachedImage comparedImage;
    private final List<Pixel> ignoredPixels;
}
