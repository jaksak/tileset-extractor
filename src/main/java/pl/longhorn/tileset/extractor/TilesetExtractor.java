package pl.longhorn.tileset.extractor;

import pl.longhorn.tileset.extractor.tileset.TilesetLoader;

import java.io.IOException;
import java.net.URISyntaxException;

public class TilesetExtractor {

    public static void main(String[] args) throws IOException, URISyntaxException {
        TilesetLoader tilesetLoader = new TilesetLoader("tileset");
    }
}