package ch.epfl.javelo.projection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PointWebMercatorTest {


    @Test
    void testToPointCh(){
        PointWebMercator mercator = new PointWebMercator(0.23, 0.54);

        PointCh ch = mercator.toPointCh();
    }

}