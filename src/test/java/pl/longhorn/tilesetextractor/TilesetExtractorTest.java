package pl.longhorn.tilesetextractor;

import lombok.val;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TilesetExtractorTest {

    private final TilesetExtractor tilesetExtractor = new TilesetExtractor();

    @Test
    public void emptyMapShouldReturnProperFile() throws IOException, URISyntaxException {
        val mapFile = ImageHelper.getResourcePath("emptyMap.png");

        val result = tilesetExtractor.run("test-tilesets", "emptyMap.png");

        assertEquals(mapFile.toFile().length(), ImageHelper.save(result).length(), 41);
    }

    @Test
    public void mapWithSingleTypeShouldReturnProperFile() throws IOException, URISyntaxException {
        val mapFile = ImageHelper.getResourcePath("singleElementsMap.png");

        val result = tilesetExtractor.run("test-tilesets", "singleElementsMap.png");

        assertEquals(mapFile.toFile().length(), ImageHelper.save(result).length(), 7_404);
    }

    @Test
    public void mapWithDoubleElementsPerCoordinatesShouldReturnProperFile() throws IOException, URISyntaxException {
        val mapFile = ImageHelper.getResourcePath("doubleMap.png");

        val result = tilesetExtractor.run("test-tilesets", "doubleMap.png");

        assertEquals(mapFile.toFile().length(), ImageHelper.save(result).length(), 7_404);
    }

//    @Test
//    public void mapWithPlantsShouldReturnProperFile() throws IOException, URISyntaxException {
//        val mapFile = ImageHelper.getResourcePath("plants.png");
//
//        val result = tilesetExtractor.run("tilesets-plants", "plants.png");
//
//        assertEquals(mapFile.toFile().length(), ImageHelper.save(result).length(), 7_404);
//    }
}
