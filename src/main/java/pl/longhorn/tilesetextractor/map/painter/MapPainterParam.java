package pl.longhorn.tilesetextractor.map.painter;

import lombok.Value;
import pl.longhorn.tilesetextractor.map.MapEntry;

import java.util.List;

@Value
public class MapPainterParam {
    private final int width;
    private final int height;
    private final List<MapEntry> entries;
}
