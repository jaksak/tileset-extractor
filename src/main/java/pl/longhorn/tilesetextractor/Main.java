package pl.longhorn.tilesetextractor;

import lombok.val;
import pl.longhorn.tilesetextractor.extractor.TilesetExtractor;
import pl.longhorn.tilesetextractor.tileset.Tilesets;

import java.io.IOException;
import java.net.URISyntaxException;

public class Main {

    public static void main(String[] args) throws IOException, URISyntaxException {
        TilesetExtractor tilesetExtractor = new TilesetExtractor();
        Tilesets tilesets = new Tilesets("tileset");
        val mapImage = ImageHelper.getImage("map.png");
        val imageResult = tilesetExtractor.run(tilesets, mapImage);
        System.out.println("Used file: " + ImageHelper.save(imageResult));
    }
}
