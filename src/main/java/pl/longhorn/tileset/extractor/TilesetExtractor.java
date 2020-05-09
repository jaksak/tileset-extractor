package pl.longhorn.tileset.extractor;

import lombok.val;
import pl.longhorn.tileset.extractor.comparator.ImageComparator;
import pl.longhorn.tileset.extractor.comparator.ImageComparatorParam;
import pl.longhorn.tileset.extractor.comparator.Pixel;
import pl.longhorn.tileset.extractor.comparator.image.data.CachedImage;
import pl.longhorn.tileset.extractor.comparator.image.data.CachedImageFactory;
import pl.longhorn.tileset.extractor.map.MapElement;
import pl.longhorn.tileset.extractor.map.MapEntry;
import pl.longhorn.tileset.extractor.map.painter.MapPainter;
import pl.longhorn.tileset.extractor.map.painter.MapPainterParam;
import pl.longhorn.tileset.extractor.tileset.Tileset;
import pl.longhorn.tileset.extractor.tileset.Tilesets;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class TilesetExtractor {

    private static Tilesets tilesets;
    private static ImageComparator imageComparator = new ImageComparator();
    private static MapPainter mapPainter = new MapPainter();

    public BufferedImage run(String tilesetDirectory, String mapFilename) throws IOException, URISyntaxException {
        tilesets = new Tilesets(tilesetDirectory);
        val mapImage = ImageHelper.getImage(mapFilename);
        AtomicInteger nextMapEntryId = new AtomicInteger();
        List<MapEntry> entries = ImageHelper.split(mapImage).parallel()
                .map(CachedImageFactory::create)
                .map(image -> countEntry(image, nextMapEntryId.getAndIncrement()))
                .collect(Collectors.toList());
        val painterParam = new MapPainterParam(mapImage.getWidth(), mapImage.getHeight(), entries);
        val result = mapPainter.paint(painterParam);
        return result;
    }

    private MapEntry countEntry(CachedImage image, int id) {
        List<MapElement> elements = new LinkedList<>();
        List<Pixel> ignoredPixels = new LinkedList<>();
        List<TilesetWithCompliance> matchedElements = getMatchedElements(image, ignoredPixels);
        while (true) {
            Optional<TilesetWithCompliance> bestMatchedElement = getBestMatched(matchedElements);
            if (bestMatchedElement.isPresent()) {
                elements.add(new MapElement(bestMatchedElement.get().getTileset()));
                ignoredPixels.addAll(bestMatchedElement.get().getComparisonResult().getUsedPixels());
            } else {
                break;
            }
            matchedElements = updateMatched(image, ignoredPixels, matchedElements);
        }
        return new MapEntry(id, elements);
    }

    private Optional<TilesetWithCompliance> getBestMatched(List<TilesetWithCompliance> matchedElements) {
        return matchedElements.stream().max(Comparator.comparingInt(TilesetWithCompliance::getCompliance));
    }

    private List<TilesetWithCompliance> updateMatched(CachedImage image, List<Pixel> ignoredPixels, List<TilesetWithCompliance> matchedElements) {
        return matchedElements.stream()
                .map(tilesetWithCompliance -> getCompliance(tilesetWithCompliance.getTileset(), image, ignoredPixels))
                .filter(tilesetWithCompliance -> tilesetWithCompliance.getCompliance() > ProjectConfig.MIN_COMPLIANCE)
                .collect(Collectors.toList());
    }

    private List<TilesetWithCompliance> getMatchedElements(CachedImage image, List<Pixel> ignoredPixels) {
        return tilesets.stream()
                .map(tileset -> getCompliance(tileset, image, ignoredPixels))
                .filter(tilesetWithCompliance -> tilesetWithCompliance.getCompliance() > ProjectConfig.MIN_COMPLIANCE)
                .collect(Collectors.toList());
    }

    private TilesetWithCompliance getCompliance(Tileset tileset, CachedImage image, List<Pixel> ignoredPixels) {
        ImageComparatorParam param = new ImageComparatorParam(image, tileset, ignoredPixels);
        val comparisonResult = imageComparator.compare(param);
        return new TilesetWithCompliance(tileset, comparisonResult);
    }
}