package pl.longhorn.tileset.extractor;

import lombok.Value;
import lombok.val;
import pl.longhorn.tileset.extractor.comparator.ImageComparisonResult;
import pl.longhorn.tileset.extractor.tileset.Tileset;

@Value
public class TilesetWithCompliance {
    private final Tileset tileset;
    private final ImageComparisonResult comparisonResult;

    public int getCompliance() {
        val toReturn = (int) (((float) comparisonResult.getIdenticalPixelAmount() / (tileset.getNonAlphaPixels() - comparisonResult.getIgnoredPixelAmount())) * 100);
        return toReturn;
    }
}
