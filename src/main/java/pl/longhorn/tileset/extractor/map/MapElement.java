package pl.longhorn.tileset.extractor.map;

import lombok.Value;
import pl.longhorn.tileset.extractor.tileset.Tileset;

@Value
public class MapElement {
    private final Tileset tileset;
    private final int matchedPixelsAmount;
}
