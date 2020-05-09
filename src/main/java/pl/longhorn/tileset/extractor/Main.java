package pl.longhorn.tileset.extractor;

import lombok.val;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.UUID;

public class Main {

    public static void main(String[] args) throws IOException, URISyntaxException {
        TilesetExtractor tilesetExtractor = new TilesetExtractor();
        val imageResult = tilesetExtractor.run("tileset", "map.png");
        val fileResult = Paths.get("build", UUID.randomUUID().toString() + ".png").toFile();
        ImageIO.write(imageResult, "PNG", fileResult);
        System.out.println("Used file: " + fileResult.getName());
    }
}
