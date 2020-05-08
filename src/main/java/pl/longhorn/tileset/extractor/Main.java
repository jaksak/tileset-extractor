package pl.longhorn.tileset.extractor;

import lombok.val;

import java.io.IOException;
import java.net.URISyntaxException;

public class Main {

    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        TilesetExtractor tilesetExtractor = new TilesetExtractor();
        val file = tilesetExtractor.run("tileset", "map.png");
        System.out.println("Used file: " + file.getName());
    }
}
