import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.longhorn.tileset.extractor.ImageComparator;
import pl.longhorn.tileset.extractor.InvalidDimensionException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ImageComparatorTest {

    ImageComparator imageComparator;

    @Test
    public void completelyDifferentImageShouldHaveNoIdenticalPixels() throws IOException {
        val baseImage = getImage("completelyDifferent");
        val otherImage = getImage("noAlpha");

        val result = imageComparator.compare(baseImage, otherImage);

        assertEquals(0, result.getIdenticalPixels());
    }

    @Test
    public void theSameImagesShouldHaveAllIdenticalPixels() throws IOException {
        val baseImage = getImage("noAlpha");
        val comparedImage = getImage("noAlpha");

        val result = imageComparator.compare(baseImage, comparedImage);

        assertEquals(1156, result.getIdenticalPixels());
    }

    @Test
    public void invalidDimensionShouldThrowException() throws IOException {
        val baseImage = getImage("base");
        val tooLargeImage = getImage("tooLarge");

        Assertions.assertThrows(InvalidDimensionException.class, () -> imageComparator.compare(baseImage, tooLargeImage));
    }

    @BeforeEach
    public void init() {
        imageComparator = new ImageComparator();
    }

    private BufferedImage getImage(String fileName) throws IOException {
        return ImageIO.read(new File(
                getClass().getClassLoader().getResource(fileName + ".png").getFile()));
    }
}
