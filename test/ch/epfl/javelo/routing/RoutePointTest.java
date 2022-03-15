package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoutePointTest {
    @Test
    void routePointMainTest(){
        PointCh point = new PointCh(2520000, 1145000);
        RoutePoint routePoint = new RoutePoint(point, 100, 10);
        RoutePoint routePoint2 = new RoutePoint(point, 100, 20);
        assertEquals(110, routePoint.withPositionShiftedBy(10).position());
        assertEquals(90, routePoint.withPositionShiftedBy(-10).position());
        assertEquals(routePoint, routePoint.min(routePoint2));
        assertEquals(routePoint, routePoint.min(point, 1000, 26));
    }

}