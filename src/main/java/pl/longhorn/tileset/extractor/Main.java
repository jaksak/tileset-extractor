package pl.longhorn.tileset.extractor;

import lombok.val;
import pl.longhorn.tileset.extractor.extractor.TilesetExtractor;
import pl.longhorn.tileset.extractor.extractor.TilesetExtractorParam;
import pl.longhorn.tileset.extractor.tileset.TilesetSupplier;
import pl.longhorn.tileset.extractor.tileset.Tilesets;

import java.io.IOException;
import java.net.URISyntaxException;

public class Main {

    public static void main(String[] args) throws IOException, URISyntaxException {
        TilesetExtractor tilesetExtractor = new TilesetExtractor();
        TilesetSupplier tilesetSupplier = new TilesetSupplier();
        Tilesets tilesets = tilesetSupplier.getTilesets("original");
        val mapImage = ImageHelper.getImage("maps/map.png");
        val extractorParam = new TilesetExtractorParam(tilesets, mapImage, 15);
        val imageResult = tilesetExtractor.run(extractorParam);
        System.out.println("Used file: " + ImageHelper.save(imageResult));
    }
}
