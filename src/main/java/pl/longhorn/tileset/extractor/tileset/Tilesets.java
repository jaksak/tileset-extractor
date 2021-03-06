package pl.longhorn.tileset.extractor.tileset;

import lombok.val;
import pl.longhorn.tileset.extractor.ImageHelper;
import pl.longhorn.tileset.extractor.ProjectConfig;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

public class Tilesets {

    private List<Tileset> tilesets = new CopyOnWriteArrayList<>();
    private int nextId = 0;

    public Tilesets(List<BufferedImage> imageTilesets) {
        imageTilesets.parallelStream()
                .map(ImageHelper::split)
                .flatMap(i -> i)
                .forEach(this::tryAddTileset);
    }

    private void tryAddTileset(BufferedImage image) {
        val imageRgb = ImageHelper.getRgb(image);
        int nonAlphaPixels = getNonAlpaPixels(imageRgb);
        nextId++;
        val newTileset = new Tileset(nextId, image, nonAlphaPixels, getGroundProbability(imageRgb, nonAlphaPixels), imageRgb);
        if (nonAlphaPixels > 0 && isUnique(imageRgb)) {
            addUnique(newTileset);
        }
    }

    private synchronized void addUnique(Tileset newTileset) {
        if (isUnique(newTileset.getRgbCache())) {
            tilesets.add(newTileset);
        }
    }

    private int getGroundProbability(int[][] imageRgb, int nonAlphaPixels) {
        if (nonAlphaPixels == 0) {
            return 0;
        }
        int baseProbability = 0;
        int widthMiddle = ProjectConfig.TILESET_WIDTH / 2;
        int heightMiddle = ProjectConfig.TILESET_HEIGHT / 2;
        for (int x = 0; x < imageRgb.length; x++) {
            for (int y = 0; y < imageRgb[0].length; y++) {
                if (!ImageHelper.pixelIsAlpha(imageRgb[x][y])) {
                    baseProbability += Math.abs(widthMiddle - x) + Math.abs(heightMiddle - y);
                }
            }
        }
        return baseProbability / nonAlphaPixels;
    }

    private int getNonAlpaPixels(int[][] rgbData) {
        int nonAlpaPixels = 0;
        for (int x = 0; x < rgbData.length; x++) {
            for (int y = 0; y < rgbData[0].length; y++) {
                if (!ImageHelper.pixelIsAlpha(rgbData[x][y])) {
                    nonAlpaPixels++;
                }
            }
        }
        return nonAlpaPixels;
    }

    private boolean isUnique(int[][] imageRgb) {
        return tilesets.stream()
                .noneMatch(compared -> rgbAreEquals(imageRgb, compared));
    }

    private boolean rgbAreEquals(int[][] img1, Tileset img2) {
        if (img1.length == img2.getWidth() && img1[0].length == img2.getHeight()) {
            for (int x = 0; x < img2.getWidth(); x++) {
                for (int y = 0; y < img2.getHeight(); y++) {
                    if (img1[x][y] != img2.getRGB(x, y))
                        return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }

    public Stream<Tileset> stream() {
        return tilesets.stream();
    }
}
