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
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TilesetExtractor {

    private static Tilesets tilesets;
    private static ImageComparator imageComparator = new ImageComparator();
    private static MapPainter mapPainter = new MapPainter();

    public static void main(String[] args) throws IOException, URISyntaxException {
        tilesets = new Tilesets("tileset");
        val mapImage = ImageHelper.getImage("map.png");
        List<MapEntry> entries = ImageHelper.split(mapImage).parallel()
                .map(TilesetExtractor::countEntry)
                .collect(Collectors.toList());
        val painterParam = new MapPainterParam(mapImage.getWidth(), mapImage.getHeight(), entries);
        val result = mapPainter.paint(painterParam);
        ImageIO.write(result, "PNG", Paths.get("build", "result.png").toFile());
    }

    private static MapEntry countEntry(BufferedImage image) {
        List<MapElement> elements = new LinkedList<>();
        List<Pixel> ignoredPixels = new LinkedList<>();
        while (true) {
            val bestMatchedElement = getBestMatched(image, ignoredPixels);
            if (bestMatchedElement.isPresent() && bestMatchedElement.get().getCompliance() > 5) {
                elements.add(new MapElement(bestMatchedElement.get().getTileset().getImage()));
                ignoredPixels.addAll(bestMatchedElement.get().getComparisonResult().getUsedPixels());
            } else {
                break;
            }
        }
        return new MapEntry(elements);
    }

    private static Optional<TilesetWithCompliance> getBestMatched(BufferedImage image, List<Pixel> ignoredPixels) {
        return tilesets.stream()
                .map(tileset -> getCompliance(tileset, image, ignoredPixels))
                .max(Comparator.comparingInt(TilesetWithCompliance::getCompliance));
    }

    private static TilesetWithCompliance getCompliance(Tileset tileset, BufferedImage image, List<Pixel> ignoredPixels) {
        ImageComparatorParam param = new ImageComparatorParam(image, tileset.getImage(), ignoredPixels);
        val comparisonResult = imageComparator.compare(param);
        return new TilesetWithCompliance(tileset, comparisonResult);
    }
}