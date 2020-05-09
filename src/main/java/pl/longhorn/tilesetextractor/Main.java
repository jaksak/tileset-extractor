package pl.longhorn.tilesetextractor;

import lombok.val;
import pl.longhorn.tilesetextractor.extractor.TilesetExtractor;

import java.io.IOException;
import java.net.URISyntaxException;

public class Main {

    public static void main(String[] args) throws IOException, URISyntaxException {
        TilesetExtractor tilesetExtractor = new TilesetExtractor();
        val imageResult = tilesetExtractor.run("tileset", "map.png");
        System.out.println("Used file: " + ImageHelper.save(imageResult));
    }
}
