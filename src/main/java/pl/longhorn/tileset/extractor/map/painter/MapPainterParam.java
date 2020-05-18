package pl.longhorn.tileset.extractor.map.painter;

import lombok.Value;
import pl.longhorn.tileset.extractor.map.MapEntry;

import java.util.List;

@Value
public class MapPainterParam {
    private final int width;
    private final int height;
    private final List<MapEntry> entries;
}
