package pl.longhorn.tilesetextractor;

import org.junit.jupiter.api.Test;
import pl.longhorn.tilesetextractor.map.painter.CoordinatesFinder;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CoordinatesFinderTest {

    @Test
    public void shouldReturnProperWidth() {
        assertEquals(0, CoordinatesFinder.getWidth(0, 3));
        assertEquals(64, CoordinatesFinder.getWidth(2, 3));
        assertEquals(32, CoordinatesFinder.getWidth(4, 3));
        assertEquals(0, CoordinatesFinder.getWidth(6, 3));
        assertEquals(64, CoordinatesFinder.getWidth(8, 3));
    }

    @Test
    public void shouldReturnProperHeight() {
        assertEquals(0, CoordinatesFinder.getHeight(0, 3));
        assertEquals(0, CoordinatesFinder.getHeight(2, 3));
        assertEquals(32, CoordinatesFinder.getHeight(4, 3));
        assertEquals(64, CoordinatesFinder.getHeight(6, 3));
        assertEquals(64, CoordinatesFinder.getHeight(8, 3));
    }
}
