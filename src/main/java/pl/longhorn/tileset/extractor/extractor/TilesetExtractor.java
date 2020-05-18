package pl.longhorn.tileset.extractor.extractor;

import lombok.val;
import pl.longhorn.tileset.extractor.ImageHelper;
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

import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class TilesetExtractor {

    public BufferedImage run(TilesetExtractorParam param) {
        AtomicInteger nextMapEntryId = new AtomicInteger();
        List<MapEntry> entries = ImageHelper.split(param.getMapImage()).parallel()
                .map(CachedImageFactory::create)
                .map(mapPart -> new EntryExtractorParam(nextMapEntryId.getAndIncrement(), mapPart, param.getTilesets(), param.getMinCompliance()))
                .map(this::countEntry)
                .collect(Collectors.toList());
        val painterParam = new MapPainterParam(param.getMapImage().getWidth(), param.getMapImage().getHeight(), entries);
        return MapPainter.paint(painterParam);
    }

    private MapEntry countEntry(EntryExtractorParam param) {
        List<MapElement> elements = new LinkedList<>();
        List<Pixel> ignoredPixels = new LinkedList<>();
        List<TilesetWithCompliance> matchedElements = getMatchedElements(param, ignoredPixels);
        while (true) {
            Optional<TilesetWithCompliance> bestMatchedElement = getBestMatched(matchedElements);
            if (bestMatchedElement.isPresent()) {
                TilesetWithCompliance tilesetsToAdd = bestMatchedElement.get();
                val mapElement = new MapElement(tilesetsToAdd.getTileset(), tilesetsToAdd.getComparisonResult().getIdenticalPixelAmount());
                elements.add(mapElement);
                matchedElements.remove(tilesetsToAdd);
                ignoredPixels.addAll(tilesetsToAdd.getComparisonResult().getUsedPixels());
            } else {
                break;
            }
            matchedElements = updateMatched(param, ignoredPixels, matchedElements);
        }
        return new MapEntry(param.getId(), elements);
    }

    private Optional<TilesetWithCompliance> getBestMatched(List<TilesetWithCompliance> matchedElements) {
        return matchedElements.stream().max(Comparator.comparingInt(TilesetWithCompliance::getCompliance));
    }

    private List<TilesetWithCompliance> updateMatched(EntryExtractorParam param, List<Pixel> ignoredPixels, List<TilesetWithCompliance> matchedElements) {
        return matchedElements.stream()
                .map(tilesetWithCompliance -> getCompliance(tilesetWithCompliance.getTileset(), param.getMapPart(), ignoredPixels))
                .filter(tilesetWithCompliance -> tilesetWithCompliance.getCompliance() >= param.getMinCompliance())
                .collect(Collectors.toList());
    }

    private List<TilesetWithCompliance> getMatchedElements(EntryExtractorParam param, List<Pixel> ignoredPixels) {
        return param.getTilesets().stream()
                .map(tileset -> getCompliance(tileset, param.getMapPart(), ignoredPixels))
                .filter(tilesetWithCompliance -> tilesetWithCompliance.getCompliance() >= param.getMinCompliance())
                .collect(Collectors.toList());
    }

    private TilesetWithCompliance getCompliance(Tileset tileset, CachedImage image, List<Pixel> ignoredPixels) {
        ImageComparatorParam param = new ImageComparatorParam(image, tileset, ignoredPixels);
        val comparisonResult = ImageComparator.compare(param);
        return new TilesetWithCompliance(tileset, comparisonResult);
    }
}