package pl.longhorn.tilesetextractor.map.painter;

import lombok.val;
import pl.longhorn.tilesetextractor.ProjectConfig;
import pl.longhorn.tilesetextractor.map.MapElement;
import pl.longhorn.tilesetextractor.map.MapEntry;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Iterator;

public class MapPainter {

    public static BufferedImage paint(MapPainterParam param) {
        BufferedImage result = new BufferedImage(param.getWidth(), param.getHeight(), BufferedImage.TYPE_INT_ARGB);
        val graphic = result.createGraphics();
        int entriesInRow = param.getWidth() / ProjectConfig.TILESET_WIDTH;
        Iterator<MapEntry> elementIterator = param.getEntries().iterator();
        for (int i = 0; elementIterator.hasNext(); i++) {
            int width = CoordinatesFinder.getWidth(i, entriesInRow);
            int height = CoordinatesFinder.getHeight(i, entriesInRow);
            val elements = elementIterator.next().getElements();
            //elements.sort(Collections.reverseOrder(Comparator.comparingInt(element -> element.getTileset().getGroundProbability())));
            elements.sort(MapPainter::compareGroundAndAmount);
            elements.forEach(element -> draw(element, width, height, graphic));
        }
        return result;
    }

    private static int compareGroundAndAmount(MapElement el1, MapElement el2) {
        if (el1.getTileset().getNonAlphaPixels() == 32 * 32) {
            return -1;
        } else if (el2.getTileset().getNonAlphaPixels() == 32 * 32) {
            return 1;
        }
        return el1.getMatchedPixelsAmount() - el2.getMatchedPixelsAmount();
    }

    private static void draw(MapElement element, int width, int height, Graphics result) {
        result.drawImage(element.getTileset().getImage(), width, height, null);
    }
}
