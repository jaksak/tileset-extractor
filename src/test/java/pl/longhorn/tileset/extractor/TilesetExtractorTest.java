package pl.longhorn.tileset.extractor;

import lombok.val;
import org.junit.jupiter.api.Test;
import pl.longhorn.tileset.extractor.extractor.TilesetExtractor;
import pl.longhorn.tileset.extractor.extractor.TilesetExtractorParam;
import pl.longhorn.tileset.extractor.tileset.Tilesets;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TilesetExtractorTest {

    private final TilesetExtractor tilesetExtractor = new TilesetExtractor();
    private final Tilesets tilesets = getTilesets("test-tilesets");

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
        val extractorParam = new TilesetExtractorParam(getTilesets("tilesets-pentagram"), ImageHelper.getImage("pentagram.png"), 15);

        val result = tilesetExtractor.run(extractorParam);

        assertEquals(mapFile.toFile().length(), ImageHelper.save(result).length(), 7_404);
    }

    private Tilesets getTilesets(String name) throws URISyntaxException, IOException {
        List<BufferedImage> images = Files.walk(ImageHelper.getResourcePath("tilesets/" + name))
                .filter(Files::isRegularFile)
                .map(path -> {
                    try {
                        return ImageIO.read(path.toFile());
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .collect(Collectors.toList());
        return new Tilesets(images);
    }
}
