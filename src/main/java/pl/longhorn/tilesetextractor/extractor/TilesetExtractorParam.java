package pl.longhorn.tilesetextractor.extractor;

import lombok.Value;
import pl.longhorn.tilesetextractor.tileset.Tilesets;

import java.awt.image.BufferedImage;

@Value
public class TilesetExtractorParam {
    private final Tilesets tilesets;
    private final BufferedImage mapImage;
    private final int minCompliance;
}
