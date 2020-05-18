package pl.longhorn.tileset.extractor.extractor;

import lombok.Value;
import pl.longhorn.tileset.extractor.comparator.image.data.CachedImage;
import pl.longhorn.tileset.extractor.tileset.Tilesets;

@Value
public class EntryExtractorParam {
    private final int id;
    private final CachedImage mapPart;
    private final Tilesets tilesets;
    private final int minCompliance;
}
