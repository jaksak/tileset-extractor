package pl.longhorn.tilesetextractor;

import lombok.val;
import org.junit.jupiter.api.Test;
import pl.longhorn.tilesetextractor.extractor.TilesetExtractor;
import pl.longhorn.tilesetextractor.tileset.Tilesets;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TilesetExtractorTest {

    private final TilesetExtractor tilesetExtractor = new TilesetExtractor();
    private final Tilesets tilesets = new Tilesets("test-tilesets");

    public TilesetExtractorTest() throws IOException, URISyntaxException {
    }

    @Test
    public void emptyMapShouldReturnProperFile() throws IOException, URISyntaxException {
        val mapFile = ImageHelper.getResourcePath("emptyMap.png");

        val result = tilesetExtractor.run(tilesets, ImageHelper.getImage("emptyMap.png"));

        assertEquals(mapFile.toFile().length(), ImageHelper.save(result).length(), 41);
    }

    @Test
    public void mapWithSingleTypeShouldReturnProperFile() throws IOException, URISyntaxException {
        val mapFile = ImageHelper.getResourcePath("singleElementsMap.png");

        val result = tilesetExtractor.run(tilesets, ImageHelper.getImage("singleElementsMap.png"));

        assertEquals(mapFile.toFile().length(), ImageHelper.save(result).length(), 7_404);
    }


    //    @Test
//    public void mapWithPlantsShouldReturnProperFile() throws IOException, URISyntaxException {
//        val mapFile = ImageHelper.getResourcePath("plants.png");
//
//        val result = tilesetExtractor.run(tilesets, ImageHelper.getImage("plants.png"));
//
//        assertEquals(mapFile.toFile().length(), ImageHelper.save(result).length(), 7_404);
//    }

    @Test
    public void mapWithDoubleElementsPerCoordinatesShouldReturnProperFile() throws IOException, URISyntaxException {
        val mapFile = ImageHelper.getResourcePath("doubleMap.png");

        val result = tilesetExtractor.run(tilesets, ImageHelper.getImage("doubleMap.png"));

        assertEquals(mapFile.toFile().length(), ImageHelper.save(result).length(), 7_404);
    }
}
