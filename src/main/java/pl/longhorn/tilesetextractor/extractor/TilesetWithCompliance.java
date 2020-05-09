package pl.longhorn.tilesetextractor.extractor;

import lombok.Value;
import pl.longhorn.tilesetextractor.comparator.ImageComparisonResult;
import pl.longhorn.tilesetextractor.tileset.Tileset;

@Value
class TilesetWithCompliance {
    private final Tileset tileset;
    private final ImageComparisonResult comparisonResult;

    public int getCompliance() {
        return (int) (((float) comparisonResult.getIdenticalPixelAmount() / (tileset.getNonAlphaPixels() - comparisonResult.getIgnoredPixelAmount())) * 100);
    }
}
