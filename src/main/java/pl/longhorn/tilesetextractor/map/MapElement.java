package pl.longhorn.tilesetextractor.map;

import lombok.Value;
import pl.longhorn.tilesetextractor.tileset.Tileset;

@Value
public class MapElement {
    private final Tileset tileset;
}
