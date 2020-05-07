package pl.longhorn.tilset.extractor;

import java.awt.image.BufferedImage;

public class ImageComparator {

    public ImageComparisonResult compare(BufferedImage baseImage, BufferedImage comparedImage){
        checkDimension(baseImage, comparedImage);
    }

    private void checkDimension(BufferedImage baseImage, BufferedImage comparedImage) {
        if(baseImage.getWidth() != comparedImage.getWidth() || baseImage.getHeight() != comparedImage.getHeight()){
            throw new InvalidDimensionException();
        }
    }
}
