package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SingleRouteTest {

    private SingleRoute testRoute(){
        List<Edge> edges = new ArrayList<Edge>();
        edges.add(new Edge(0, 1,
                new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MAX_E, SwissBounds.MIN_N),
                2, Functions.constant(3)));
        edges.add(new Edge(1, 2,
                new PointCh(SwissBounds.MAX_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N),
                2, Functions.constant(4)));
        edges.add(new Edge(2, 3,
                new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N),
                new PointCh(SwissBounds.MIN_E, SwissBounds.MAX_N),
                2, Functions.constant(5)));
        edges.add(new Edge(4, 5,
                new PointCh(SwissBounds.MIN_E, SwissBounds.MAX_N),
                new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                2, Functions.constant(6)));
        return new SingleRoute(edges);
    }

    @Test
    void singleRouteThrowsExceptionTest(){
        List<Edge> noEdges = new ArrayList<Edge>();
        assertThrows(IllegalArgumentException.class, () -> {
            new SingleRoute(noEdges);
        });
    }

    @Test
    void singleRouteImmutabilityTest(){
        List<Edge> edges = new ArrayList<Edge>();
        edges.add(new Edge(0, 1,
                new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N),
                3, Functions.constant(3)));
        Route testRoute = new SingleRoute(edges);
        assertEquals(3, testRoute.length());
        edges.add(new Edge(1, 2,
                new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N),
                new PointCh(SwissBounds.MAX_E, SwissBounds.MIN_N),
                4, Functions.constant(6)));
        assertEquals(3, testRoute.length());
    }

    @Test
    void indexOfSegmentAtTest(){

    }

    @Test
    void lengthTest(){

    }

    @Test
    void pointAtTest(){

    }

    @Test
    void elevationAtTest(){
        Route testRoute = testRoute();
        assertEquals(3, testRoute.elevationAt(0));
        assertEquals(4, testRoute.elevationAt(2));
        assertEquals(4, testRoute.elevationAt(3));
        assertEquals(5, testRoute.elevationAt(5));
        assertEquals(6, testRoute.elevationAt(8));
    }

    @Test
    void nodeClosestToTest(){
        Route testRoute = testRoute();
    }

    @Test
    void pointClosestToTest(){

    }
}