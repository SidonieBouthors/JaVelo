package ch.epfl.javelo.routing;

import static org.junit.jupiter.api.Assertions.*;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class MultiRouteTest {

    public static final List<PointCh> ALL_POINTS = allPoints();
    public static final List<Edge> ALL_EDGES = allEdges();
    public static final MultiRoute MULTI_ROUTE = multiRoute();

    public static List<PointCh> allPoints() {
        PointCh point0 = new PointCh(2_550_000, 1_152_300);
        PointCh point1 = new PointCh(2_550_500, 1_152_300);
        PointCh point2 = new PointCh(2_551_000, 1_152_300);
        PointCh point3 = new PointCh(2_551_500, 1_152_300);
        PointCh point4 = new PointCh(2_552_000, 1_152_300);
        PointCh point5 = new PointCh(2_552_500, 1_152_300);
        PointCh point6 = new PointCh(2_553_000, 1_152_300);
        PointCh point7 = new PointCh(2_553_500, 1_152_300);
        PointCh point8 = new PointCh(2_554_000, 1_152_300);
        PointCh point9 = new PointCh(2_554_500, 1_152_300);
        PointCh point10 = new PointCh(2_555_000, 1_152_300);
        PointCh point11 = new PointCh(2_555_500, 1_152_300);
        PointCh point12 = new PointCh(2_556_000, 1_152_300);

        List<PointCh> allPoints = new ArrayList<>(List.of(point0, point1, point2, point3, point4, point5, point6,
                point7, point8, point9, point10, point11, point12));

        return allPoints;
    }

    public static List<Edge> allEdges() {

        Edge edge0 = new Edge(0, 1, ALL_POINTS.get(0), ALL_POINTS.get(1), 500, DoubleUnaryOperator.identity());
        Edge edge1 = new Edge(1, 2, ALL_POINTS.get(1), ALL_POINTS.get(2), 500, DoubleUnaryOperator.identity());
        Edge edge2 = new Edge(2, 3, ALL_POINTS.get(2), ALL_POINTS.get(3), 500, DoubleUnaryOperator.identity());
        Edge edge3 = new Edge(3, 4, ALL_POINTS.get(3), ALL_POINTS.get(4), 500, DoubleUnaryOperator.identity());
        Edge edge4 = new Edge(4, 5, ALL_POINTS.get(4), ALL_POINTS.get(5), 500, DoubleUnaryOperator.identity());
        Edge edge5 = new Edge(5, 6, ALL_POINTS.get(5), ALL_POINTS.get(6), 500, DoubleUnaryOperator.identity());
        Edge edge6 = new Edge(6, 7, ALL_POINTS.get(6), ALL_POINTS.get(7), 500, DoubleUnaryOperator.identity());
        Edge edge7 = new Edge(7, 8, ALL_POINTS.get(7), ALL_POINTS.get(8), 500, DoubleUnaryOperator.identity());
        Edge edge8 = new Edge(8, 9, ALL_POINTS.get(8), ALL_POINTS.get(9), 500, DoubleUnaryOperator.identity());
        Edge edge9 = new Edge(9, 10, ALL_POINTS.get(9), ALL_POINTS.get(10), 500, DoubleUnaryOperator.identity());
        Edge edge10 = new Edge(10, 11, ALL_POINTS.get(10), ALL_POINTS.get(11), 500, DoubleUnaryOperator.identity());
        Edge edge11 = new Edge(11, 12, ALL_POINTS.get(11), ALL_POINTS.get(12), 500, DoubleUnaryOperator.identity());

        List<Edge> edges0 = new ArrayList<>(List.of(edge0, edge1));
        List<Edge> edges1 = new ArrayList<>(List.of(edge2, edge3));
        List<Edge> edges2 = new ArrayList<>(List.of(edge4, edge5));
        List<Edge> edges3 = new ArrayList<>(List.of(edge6, edge7));
        List<Edge> edges4 = new ArrayList<>(List.of(edge8, edge9));
        List<Edge> edges5 = new ArrayList<>(List.of(edge10, edge11));

        List<Edge> allEdges = new ArrayList<>();
        Stream.of(edges0, edges1, edges2, edges3, edges4, edges5).forEach(allEdges::addAll);

        return allEdges;
    }

    public static MultiRoute multiRoute() {

        Route singleRoute0 = new SingleRoute(ALL_EDGES.subList(0,2));
        Route singleRoute1 = new SingleRoute(ALL_EDGES.subList(2,4));
        Route singleRoute2 = new SingleRoute(ALL_EDGES.subList(4,6));
        Route singleRoute3 = new SingleRoute(ALL_EDGES.subList(6,8));
        Route singleRoute4 = new SingleRoute(ALL_EDGES.subList(8,10));
        Route singleRoute5 = new SingleRoute(ALL_EDGES.subList(10,12));

        List<Route> segment0 = new ArrayList<>(List.of(singleRoute0, singleRoute1, singleRoute2));
        List<Route> segment1 = new ArrayList<>(List.of(singleRoute3, singleRoute4, singleRoute5));

        List<Route> segments = new ArrayList<>();
        Stream.of(segment0, segment1).forEach(segments::addAll);

        return new MultiRoute(segments);
    }

    @Test
    void indexOfSegmentAtWorksOnKnownValues() {
        assertEquals(0, MULTI_ROUTE.indexOfSegmentAt(100));
        assertEquals(4, MULTI_ROUTE.indexOfSegmentAt(4000));
        assertEquals(4, MULTI_ROUTE.indexOfSegmentAt(4100));
        assertEquals(5, MULTI_ROUTE.indexOfSegmentAt(5500));
        assertEquals(6, MULTI_ROUTE.indexOfSegmentAt(6000));
    }

    @Test
    void edgesWorksForAllEdges() {
        assertEquals(ALL_EDGES, MULTI_ROUTE.edges());
    }

    @Test
    void pointsWorksForAllPoints() {
        assertEquals(ALL_POINTS, MULTI_ROUTE.points());
    }

    @Test
    void pointAtWorksOnKnownValues() {
        PointCh pointOnRoute5 = new PointCh(2_555_500, 1_152_300);
        PointCh pointOnRoute0 = new PointCh(2_550_000, 1_152_300);
        PointCh pointOnRoute5end = new PointCh(2_556_000, 1_152_300);
        assertEquals(pointOnRoute5, MULTI_ROUTE.pointAt(5500));
        assertEquals(pointOnRoute5end, MULTI_ROUTE.pointAt(6000));
        assertEquals(pointOnRoute0, MULTI_ROUTE.pointAt(0));
    }



}
