package pl.longhorn.tilesetextractor.extractor;

import lombok.Value;
import pl.longhorn.tilesetextractor.comparator.image.data.CachedImage;
import pl.longhorn.tilesetextractor.tileset.Tilesets;

@Value
public class EntryExtractorParam {
    private final int id;
    private final CachedImage mapPart;
    private final Tilesets tilesets;
}
