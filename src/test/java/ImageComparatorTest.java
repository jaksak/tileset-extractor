import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.longhorn.tileset.extractor.comparator.ImageComparator;
import pl.longhorn.tileset.extractor.comparator.ImageComparatorParam;
import pl.longhorn.tileset.extractor.comparator.InvalidDimensionException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ImageComparatorTest {

    ImageComparator imageComparator = new ImageComparator();

    @Test
    public void completelyDifferentImageShouldHaveNoIdenticalPixels() throws IOException {
        val result = imageComparator.compare(getParam("completelyDifferent", "noAlpha"));

        assertEquals(0, result.getIdenticalPixelAmount());
    }

    @Test
    public void theSameImagesShouldHaveAllIdenticalPixels() throws IOException {
        val result = imageComparator.compare(getParam("noAlpha", "noAlpha"));

        assertEquals(1156, result.getIdenticalPixelAmount());
    }

    @Test
    public void invalidDimensionShouldThrowException() throws IOException {
        Assertions.assertThrows(InvalidDimensionException.class, () -> imageComparator.compare(getParam("base", "tooLarge")));
    }

    private ImageComparatorParam getParam(String imageName1, String imageName2) throws IOException {
        return new ImageComparatorParam(getImage(imageName1), getImage(imageName2), Collections.emptyList());
    }

    private BufferedImage getImage(String fileName) throws IOException {
        return ImageIO.read(new File(
                Objects.requireNonNull(getClass().getClassLoader().getResource(fileName + ".png")).getFile()));
    }
}
