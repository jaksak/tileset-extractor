package pl.longhorn.tilesetextractor.map;


import lombok.Value;

import java.util.List;

@Value
public class MapEntry {
    private final int id;
    private final List<MapElement> elements;
}
