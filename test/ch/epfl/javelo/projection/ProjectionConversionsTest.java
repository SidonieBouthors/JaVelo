package ch.epfl.javelo.projection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import static org.junit.jupiter.api.Assertions.*;


public class ProjectionConversionsTest {

    @Test
    void boucletestcoords(){
        double  x = 0.2, y=0.1, e = 1099909809, n =87236487;

        x = WebMercator.lon(x);
        x = WebMercator.x(x);
        assertEquals(0.2,x);
        y = WebMercator.lat(y);
        y = WebMercator.y(y);
        assertEquals(0.1,y);
        PointCh ch = new PointCh(2500000, 1200000);
        PointWebMercator mercator = PointWebMercator.ofPointCh(ch);
        assertEquals(ch.lat(), mercator.lat(), 10e-6);
        assertEquals(ch.lon(), mercator.lon(), 10e-6);
        ch = mercator.toPointCh();
        assertEquals(ch.lat(), mercator.lat(), 10e-6);
        assertEquals(ch.lon(), mercator.lon(), 10e-6);
    }

}
