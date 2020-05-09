package pl.longhorn.tilesetextractor.map.painter;

import lombok.val;
import pl.longhorn.tilesetextractor.ProjectConfig;
import pl.longhorn.tilesetextractor.map.MapElement;
import pl.longhorn.tilesetextractor.map.MapEntry;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class MapPainter {

    public BufferedImage paint(MapPainterParam param) {
        BufferedImage result = new BufferedImage(param.getWidth(), param.getHeight(), BufferedImage.TYPE_INT_ARGB);
        val graphic = result.createGraphics();
        int entriesInRow = param.getWidth() / ProjectConfig.TILESET_WIDTH;
        Iterator<MapEntry> elementIterator = param.getEntries().iterator();
        for (int i = 0; elementIterator.hasNext(); i++) {
            int width = CoordinatesFinder.getWidth(i, entriesInRow);
            int height = CoordinatesFinder.getHeight(i, entriesInRow);
            val elements = elementIterator.next().getElements();
            elements.sort(Collections.reverseOrder(Comparator.comparingInt(element -> element.getTileset().getGroundProbability())));
            elements.forEach(element -> draw(element, width, height, graphic));
        }
        return result;
    }

    private void draw(MapElement element, int width, int height, Graphics result) {
        result.drawImage(element.getTileset().getImage(), width, height, null);
    }
}
