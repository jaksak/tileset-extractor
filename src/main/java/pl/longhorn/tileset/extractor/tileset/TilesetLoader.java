package pl.longhorn.tileset.extractor.tileset;

import lombok.val;
import pl.longhorn.tileset.extractor.ImageHelper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class TilesetLoader {

    private List<Tileset> tilesets = new LinkedList<>();

    public TilesetLoader(String tilesetDirectory) throws URISyntaxException, IOException {
        Files.walk(ImageHelper.getResourcePath(tilesetDirectory))
                .filter(Files::isRegularFile)
                .forEach(this::addTilesets);
    }

    private void addTilesets(Path path) {
        try {
            val tilsetsImage = ImageIO.read(path.toFile());
            ImageHelper.split(tilsetsImage)
                    .forEach(this::tryAddTilset);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void tryAddTilset(BufferedImage image) {
        int nonAlpaPixels = getNonAlpaPixels(image);
        if (nonAlpaPixels > 0 && isUnique(image)) {
            tilesets.add(new Tileset(image, nonAlpaPixels));
        }
    }

    private int getNonAlpaPixels(BufferedImage image) {
        int nonAlpaPixels = 0;
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                if (!ImageHelper.pixelIsAlpha(image.getRGB(x, y))) {
                    nonAlpaPixels++;
                }
            }
        }
        return nonAlpaPixels;
    }

    private boolean isUnique(BufferedImage image) {
        return tilesets.stream()
                .noneMatch(compared -> bufferedImagesEqual(image, compared.getImage()));
    }

    private boolean bufferedImagesEqual(BufferedImage img1, BufferedImage img2) {
        if (img1.getWidth() == img2.getWidth() && img1.getHeight() == img2.getHeight()) {
            for (int x = 0; x < img1.getWidth(); x++) {
                for (int y = 0; y < img1.getHeight(); y++) {
                    if (img1.getRGB(x, y) != img2.getRGB(x, y))
                        return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }
}
