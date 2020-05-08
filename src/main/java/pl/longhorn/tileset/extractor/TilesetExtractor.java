package pl.longhorn.tileset.extractor;

import lombok.val;
import pl.longhorn.tileset.extractor.comparator.ImageComparator;
import pl.longhorn.tileset.extractor.comparator.ImageComparatorParam;
import pl.longhorn.tileset.extractor.comparator.Pixel;
import pl.longhorn.tileset.extractor.map.MapElement;
import pl.longhorn.tileset.extractor.map.MapEntry;
import pl.longhorn.tileset.extractor.map.painter.MapPainter;
import pl.longhorn.tileset.extractor.map.painter.MapPainterParam;
import pl.longhorn.tileset.extractor.tileset.Tileset;
import pl.longhorn.tileset.extractor.tileset.Tilesets;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class TilesetExtractor {

    private static Tilesets tilesets;
    private static ImageComparator imageComparator = new ImageComparator();
    private static MapPainter mapPainter = new MapPainter();

    public File run(String tilesetDirectory, String mapFilename) throws IOException, URISyntaxException {
        tilesets = new Tilesets(tilesetDirectory);
        val mapImage = ImageHelper.getImage(mapFilename);
        AtomicInteger nextMapEntryId = new AtomicInteger();
        List<MapEntry> entries = ImageHelper.split(mapImage).parallel()
                .map(image -> countEntry(image, nextMapEntryId.getAndIncrement()))
                .collect(Collectors.toList());
        val painterParam = new MapPainterParam(mapImage.getWidth(), mapImage.getHeight(), entries);
        val result = mapPainter.paint(painterParam);
        val fileResult = Paths.get("build", UUID.randomUUID().toString() + ".png").toFile();
        ImageIO.write(result, "PNG", fileResult);
        return fileResult;
    }

    private MapEntry countEntry(BufferedImage image, int id) {
        List<MapElement> elements = new LinkedList<>();
        List<Pixel> ignoredPixels = new LinkedList<>();
        while (true) {
            val bestMatchedElement = getBestMatched(image, ignoredPixels);
            if (bestMatchedElement.isPresent() && bestMatchedElement.get().getCompliance() > ProjectConfig.MIN_COMPLIANCE) {
                elements.add(new MapElement(bestMatchedElement.get().getTileset()));
                ignoredPixels.addAll(bestMatchedElement.get().getComparisonResult().getUsedPixels());
            } else {
                break;
            }
        }
        return new MapEntry(id, elements);
    }

    private Optional<TilesetWithCompliance> getBestMatched(BufferedImage image, List<Pixel> ignoredPixels) {
        return tilesets.stream()
                .map(tileset -> getCompliance(tileset, image, ignoredPixels))
                .max(Comparator.comparingInt(TilesetWithCompliance::getCompliance));
    }

    private TilesetWithCompliance getCompliance(Tileset tileset, BufferedImage image, List<Pixel> ignoredPixels) {
        ImageComparatorParam param = new ImageComparatorParam(image, tileset.getImage(), ignoredPixels);
        val comparisonResult = imageComparator.compare(param);
        return new TilesetWithCompliance(tileset, comparisonResult);
    }
}