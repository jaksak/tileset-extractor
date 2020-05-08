package pl.longhorn.tileset.extractor;

import lombok.Value;
import pl.longhorn.tileset.extractor.comparator.ImageComparisonResult;
import pl.longhorn.tileset.extractor.tileset.Tileset;

@Value
public class TilesetWithCompliance {
    private final Tileset tileset;
    private final ImageComparisonResult comparisonResult;

    public int getCompliance() {
        return (int) (((float) comparisonResult.getIdenticalPixelsAmount() / tileset.getNonAlphaPixels()) * 100);
    }
}
