package ch.epfl.javelo.projection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PointWebMercatorTest {

    @Test
    void testOf(){
        int x =69561722;
        int y = 47468099;
        PointWebMercator mercator = PointWebMercator.of(19, x, y);
        assertEquals(x, mercator.xAtZoomLevel(19));
        assertEquals(y, mercator.yAtZoomLevel(19));
    }

    @Test
    void testToPointCh(){
        PointWebMercator mercator = new PointWebMercator(0.23, 0.54);

        PointCh ch = mercator.toPointCh();
    }



}