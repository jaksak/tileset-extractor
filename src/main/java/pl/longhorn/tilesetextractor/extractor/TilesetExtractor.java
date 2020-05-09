package pl.longhorn.tilesetextractor.extractor;

import lombok.val;
import pl.longhorn.tilesetextractor.ImageHelper;
import pl.longhorn.tilesetextractor.ProjectConfig;
import pl.longhorn.tilesetextractor.comparator.ImageComparator;
import pl.longhorn.tilesetextractor.comparator.ImageComparatorParam;
import pl.longhorn.tilesetextractor.comparator.Pixel;
import pl.longhorn.tilesetextractor.comparator.image.data.CachedImage;
import pl.longhorn.tilesetextractor.comparator.image.data.CachedImageFactory;
import pl.longhorn.tilesetextractor.map.MapElement;
import pl.longhorn.tilesetextractor.map.MapEntry;
import pl.longhorn.tilesetextractor.map.painter.MapPainter;
import pl.longhorn.tilesetextractor.map.painter.MapPainterParam;
import pl.longhorn.tilesetextractor.tileset.Tileset;
import pl.longhorn.tilesetextractor.tileset.Tilesets;

import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class TilesetExtractor {

    public BufferedImage run(Tilesets tilesets, BufferedImage mapImage) {
        AtomicInteger nextMapEntryId = new AtomicInteger();
        List<MapEntry> entries = ImageHelper.split(mapImage).parallel()
                .map(CachedImageFactory::create)
                .map(mapPart -> new EntryExtractorParam(nextMapEntryId.getAndIncrement(), mapPart, tilesets))
                .map(this::countEntry)
                .collect(Collectors.toList());
        val painterParam = new MapPainterParam(mapImage.getWidth(), mapImage.getHeight(), entries);
        return MapPainter.paint(painterParam);
    }

    private MapEntry countEntry(EntryExtractorParam param) {
        List<MapElement> elements = new LinkedList<>();
        List<Pixel> ignoredPixels = new LinkedList<>();
        List<TilesetWithCompliance> matchedElements = getMatchedElements(param, ignoredPixels);
        while (true) {
            Optional<TilesetWithCompliance> bestMatchedElement = getBestMatched(matchedElements);
            if (bestMatchedElement.isPresent()) {
                elements.add(new MapElement(bestMatchedElement.get().getTileset()));
                ignoredPixels.addAll(bestMatchedElement.get().getComparisonResult().getUsedPixels());
            } else {
                break;
            }
            matchedElements = updateMatched(param.getMapPart(), ignoredPixels, matchedElements);
        }
        return new MapEntry(param.getId(), elements);
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

    private List<TilesetWithCompliance> getMatchedElements(EntryExtractorParam param, List<Pixel> ignoredPixels) {
        return param.getTilesets().stream()
                .map(tileset -> getCompliance(tileset, param.getMapPart(), ignoredPixels))
                .filter(tilesetWithCompliance -> tilesetWithCompliance.getCompliance() > ProjectConfig.MIN_COMPLIANCE)
                .collect(Collectors.toList());
    }

    private TilesetWithCompliance getCompliance(Tileset tileset, CachedImage image, List<Pixel> ignoredPixels) {
        ImageComparatorParam param = new ImageComparatorParam(image, tileset, ignoredPixels);
        val comparisonResult = ImageComparator.compare(param);
        return new TilesetWithCompliance(tileset, comparisonResult);
    }
}