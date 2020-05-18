package pl.longhorn.tileset.extractor.extractor;

import lombok.Value;
import pl.longhorn.tileset.extractor.comparator.ImageComparisonResult;
import pl.longhorn.tileset.extractor.tileset.Tileset;

@Value
class TilesetWithCompliance {
    private final Tileset tileset;
    private final ImageComparisonResult comparisonResult;

    public int getCompliance() {
        return (int) (((float) comparisonResult.getIdenticalPixelAmount() / (tileset.getNonAlphaPixels() - comparisonResult.getIgnoredPixelAmount())) * 100);
    }
}
