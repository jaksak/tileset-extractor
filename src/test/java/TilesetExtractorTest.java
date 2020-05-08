import lombok.val;
import org.junit.jupiter.api.Test;
import pl.longhorn.tileset.extractor.ImageHelper;
import pl.longhorn.tileset.extractor.TilesetExtractor;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TilesetExtractorTest {

    private final TilesetExtractor tilesetExtractor = new TilesetExtractor();

    @Test
    public void emptyMapShouldReturnProperFile() throws IOException, URISyntaxException {
        val mapFile = ImageHelper.getResourcePath("emptyMap.png");

        val resultFile = tilesetExtractor.run("test-tilesets", "emptyMap.png");

        assertEquals(mapFile.toFile().length(), resultFile.length(), 41);
    }

    @Test
    public void mapWithSingleTypeShouldReturnProperFile() throws IOException, URISyntaxException {
        val mapFile = ImageHelper.getResourcePath("singleElementsMap.png");

        val resultFile = tilesetExtractor.run("test-tilesets", "singleElementsMap.png");

        assertEquals(mapFile.toFile().length(), resultFile.length(), 7_404);
    }

    @Test
    public void mapWithDoubleElementsPerCoordinatesShouldReturnProperFile() throws IOException, URISyntaxException {
        val mapFile = ImageHelper.getResourcePath("doubleMap.png");

        val resultFile = tilesetExtractor.run("test-tilesets", "doubleMap.png");

        assertEquals(mapFile.toFile().length(), resultFile.length(), 7_404);
    }

//    @Test
//    public void mapWithPlantsShouldReturnProperFile() throws IOException, URISyntaxException {
//        val mapFile = ImageHelper.getResourcePath("plants.png");
//
//        val resultFile = tilesetExtractor.run("tilesets-plants", "plants.png");
//
//        assertEquals(mapFile.toFile().length(), resultFile.length(), 7_404);
//    }
}
