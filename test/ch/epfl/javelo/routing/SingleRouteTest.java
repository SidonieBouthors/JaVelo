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
    void bigTest(){
        Edge edge0 = new Edge(0, 1,
                new PointCh(2485000, 1076000),
                new PointCh(2485000, SwissBounds.MAX_N),
                0.5, Functions.constant(Double.NaN)
        );
        Edge edge1 = new Edge(1, 2,
                new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N),
                1.5, Functions.sampled(new float[]{2f, 5f}, 1.5)
        );
        Edge edge2 = new Edge(2, 3,
                new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N),
                0.4, Functions.sampled(new float[]{5f, 4f}, 0.4)
        );
        Edge edge3 = new Edge(3, 4,
                new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N),
                1.6, Functions.constant(Double.NaN)
        );
        Edge edge4 = new Edge(4, 5,
                new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N),
                1, Functions.sampled(new float[]{8.2f, 7f}, 1)
        );
        Edge edge5 = new Edge(5, 6,
                new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N),
                0.8, Functions.sampled(new float[]{7f, 10f}, 0.8)
        );
        Edge edge6 = new Edge(6, 7,
                new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N),
                1, Functions.constant(Double.NaN)
        );
        Edge edge7 = new Edge(7, 8,
                new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N),
                0.5, Functions.constant(Double.NaN)
        );
        Edge edge8 = new Edge(8, 9,
                new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N),
                0.7, Functions.sampled(new float[]{4f, 6f}, 0.7)
        );
        Edge edge9 = new Edge(9, 10,
                new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N),
                1, Functions.constant(Double.NaN)
        );
        List<Edge> edgeList = new ArrayList<Edge>();
        edgeList.add(edge0);
        edgeList.add(edge1);
        edgeList.add(edge2);
        edgeList.add(edge3);
        edgeList.add(edge4);
        edgeList.add(edge5);
        edgeList.add(edge6);
        edgeList.add(edge7);
        edgeList.add(edge8);
        edgeList.add(edge9);
        Route route = new SingleRoute(edgeList);
        assertEquals(9, route.length());
        List<Edge> testEdges = route.edges();
        for (int i = 0; i < edgeList.size(); i++) {
            assertEquals(edgeList.get(i), testEdges.get(i));
        }

    }
}