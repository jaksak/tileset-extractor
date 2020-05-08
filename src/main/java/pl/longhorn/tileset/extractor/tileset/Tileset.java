package pl.longhorn.tileset.extractor.tileset;

import lombok.Value;

import java.awt.image.BufferedImage;

@Value
public class Tileset {

    private final BufferedImage image;
    private final int nonAlphaPixels;
}
