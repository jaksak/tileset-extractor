package pl.longhorn.tileset.extractor.extractor;

import lombok.Value;
import pl.longhorn.tileset.extractor.tileset.Tilesets;

import java.awt.image.BufferedImage;

@Value
public class TilesetExtractorParam {
    private final Tilesets tilesets;
    private final BufferedImage mapImage;
    private final int minCompliance;
}
