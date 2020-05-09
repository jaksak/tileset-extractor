package pl.longhorn.tilesetextractor.map.painter;

import pl.longhorn.tilesetextractor.ProjectConfig;

public class CoordinatesFinder {

    public static int getWidth(int entryIndex, int entriesInRowAmount) {
        return (entryIndex % entriesInRowAmount) * ProjectConfig.TILESET_WIDTH;
    }

    public static int getHeight(int entryIndex, int entriesInRowAmount) {
        return entryIndex / entriesInRowAmount * ProjectConfig.TILESET_HEIGHT;
    }
}
