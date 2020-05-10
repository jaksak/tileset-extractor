package pl.longhorn.tilesetextractor;

import lombok.val;
import org.junit.jupiter.api.Test;
import pl.longhorn.tilesetextractor.extractor.TilesetExtractor;
import pl.longhorn.tilesetextractor.extractor.TilesetExtractorParam;
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
        val extractorParam = new TilesetExtractorParam(tilesets, ImageHelper.getImage("emptyMap.png"), 15);

        val result = tilesetExtractor.run(extractorParam);

        assertEquals(mapFile.toFile().length(), ImageHelper.save(result).length(), 41);
    }

    @Test
    public void mapWithSingleTypeShouldReturnProperFile() throws IOException, URISyntaxException {
        val mapFile = ImageHelper.getResourcePath("singleElementsMap.png");
        val extractorParam = new TilesetExtractorParam(tilesets, ImageHelper.getImage("singleElementsMap.png"), 15);

        val result = tilesetExtractor.run(extractorParam);

        assertEquals(mapFile.toFile().length(), ImageHelper.save(result).length(), 7_404);
    }

    @Test
    public void mapWithDoubleElementsPerCoordinatesShouldReturnProperFile() throws IOException, URISyntaxException {
        val mapFile = ImageHelper.getResourcePath("doubleMap.png");
        val extractorParam = new TilesetExtractorParam(tilesets, ImageHelper.getImage("doubleMap.png"), 15);

        val result = tilesetExtractor.run(extractorParam);

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
    public void mapWithPentagramsShouldReturnProperFile() throws IOException, URISyntaxException {
        val mapFile = ImageHelper.getResourcePath("pentagram.png");
        val extractorParam = new TilesetExtractorParam(new Tilesets("tilesets-pentagram"), ImageHelper.getImage("pentagram.png"), 15);

        val result = tilesetExtractor.run(extractorParam);

        assertEquals(mapFile.toFile().length(), ImageHelper.save(result).length(), 7_404);
    }
}
