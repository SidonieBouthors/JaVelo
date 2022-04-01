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
    public static final MultiRoute COMPLEX_MULTI_ROUTE = complexMultiRoute();

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

    public static MultiRoute complexMultiRoute() {

        Route singleRoute0 = new SingleRoute(ALL_EDGES.subList(0,2));
        Route singleRoute1 = new SingleRoute(ALL_EDGES.subList(2,4));
        Route singleRoute2 = new SingleRoute(ALL_EDGES.subList(4,6));
        Route singleRoute3 = new SingleRoute(ALL_EDGES.subList(6,8));
        Route singleRoute4 = new SingleRoute(ALL_EDGES.subList(8,10));
        Route singleRoute5 = new SingleRoute(ALL_EDGES.subList(10,12));

        Route subroute0 = new MultiRoute(List.of(singleRoute0, singleRoute1));
        Route subroute1 = new MultiRoute(List.of(singleRoute2, singleRoute3));

        List<Route> segment0 = new ArrayList<>(List.of(subroute0, subroute1));
        List<Route> segment1 = new ArrayList<>(List.of(singleRoute4, singleRoute5));

        List<Route> segments = new ArrayList<>();
        Stream.of(segment0, segment1).forEach(segments::addAll);

        return new MultiRoute(segments);
    }

    @Test
    void indexOfSegmentAtWorksOnKnownValues() {
        assertEquals(0, MULTI_ROUTE.indexOfSegmentAt(100));
        assertEquals(3, MULTI_ROUTE.indexOfSegmentAt(4000));
        assertEquals(4, MULTI_ROUTE.indexOfSegmentAt(4100));
        assertEquals(5, MULTI_ROUTE.indexOfSegmentAt(5500));
        assertEquals(5, MULTI_ROUTE.indexOfSegmentAt(6000));
    }

    @Test
    void indexOfSegmentAtWorksOnComplexRoutes() {
        assertEquals(0, COMPLEX_MULTI_ROUTE.indexOfSegmentAt(100));
        assertEquals(3, COMPLEX_MULTI_ROUTE.indexOfSegmentAt(4000));
        assertEquals(4, COMPLEX_MULTI_ROUTE.indexOfSegmentAt(4100));
        assertEquals(5, COMPLEX_MULTI_ROUTE.indexOfSegmentAt(5500));
        assertEquals(5, COMPLEX_MULTI_ROUTE.indexOfSegmentAt(6000));
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

    @Test
    void pointAt() {
        PointCh pointOnRouteA = new PointCh(2_553_000, 1_152_300);
        PointCh pointOnRouteB = new PointCh(2_550_252, 1_152_300);
        PointCh pointOnRouteC = new PointCh(2_556_000, 1_152_300);
        PointCh pointOnRouteD = new PointCh(2_550_000, 1_152_300);


        assertEquals(pointOnRouteA,MULTI_ROUTE.pointAt(3000));
        assertEquals(pointOnRouteB,MULTI_ROUTE.pointAt(252));
        assertEquals(pointOnRouteC,MULTI_ROUTE.pointAt(6000));
        assertEquals(pointOnRouteC,MULTI_ROUTE.pointAt(7000));
        assertEquals(pointOnRouteD,MULTI_ROUTE.pointAt(-3));
    }
    @Test
    void Elevationat() {
        assertEquals(49, MULTI_ROUTE.elevationAt(499));
        assertEquals(50, MULTI_ROUTE.elevationAt(500));
        assertEquals(75, MULTI_ROUTE.elevationAt(3700));
        assertEquals(0,MULTI_ROUTE.elevationAt(2500));

    }
    @Test
    void NodeCloestto() {
        assertEquals(1,MULTI_ROUTE.nodeClosestTo(500));
        assertEquals(0,MULTI_ROUTE.nodeClosestTo(249));
        assertEquals(0,MULTI_ROUTE.nodeClosestTo(-1));
        assertEquals(12,MULTI_ROUTE.nodeClosestTo(6001));
        assertEquals(6,MULTI_ROUTE.nodeClosestTo(3001));

    }
    @Test
    void pointClosestTo() {

        PointCh result = new PointCh(2550000,1152300);
        PointCh totest = new PointCh(2550000, 1152400);

        PointCh result1 = new PointCh(2556000,1152300);
        PointCh totest1 = new PointCh(2550000, 1153300);
        PointCh totest2 = new PointCh(2559000, 1152300);


         assertEquals(new RoutePoint(result,0,100), MULTI_ROUTE.pointClosestTo(totest));
         assertEquals(new RoutePoint(result,0,1000),MULTI_ROUTE.pointClosestTo(totest1));
        assertEquals(new RoutePoint(result1,6000,3000),MULTI_ROUTE.pointClosestTo(totest2));

    }


    /*******************************************************/

    PointCh point1 = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
    PointCh point2 = new PointCh(SwissBounds.MIN_E + 1000, SwissBounds.MIN_N);
    PointCh point3 = new PointCh(SwissBounds.MIN_E + 1000, SwissBounds.MIN_N + 1000);
    PointCh point4 = new PointCh(SwissBounds.MIN_E + 2000, SwissBounds.MIN_N + 1000);
    PointCh point5 = new PointCh(SwissBounds.MIN_E + 2000, SwissBounds.MIN_N + 2000);
    PointCh point6 = new PointCh(SwissBounds.MIN_E + 3000, SwissBounds.MIN_N + 2000);
    PointCh point7 = new PointCh(SwissBounds.MIN_E + 3000, SwissBounds.MIN_N + 3000);
    PointCh point8 = new PointCh(SwissBounds.MIN_E + 4000, SwissBounds.MIN_N + 3000);
    PointCh point9 = new PointCh(SwissBounds.MIN_E + 4000, SwissBounds.MIN_N + 4000);
    PointCh point10 = new PointCh(SwissBounds.MIN_E + 5000, SwissBounds.MIN_N + 4000);
    PointCh point11 = new PointCh(SwissBounds.MIN_E + 5000, SwissBounds.MIN_N + 5000);
    PointCh point12 = new PointCh(SwissBounds.MIN_E + 6000, SwissBounds.MIN_N + 5000);
    PointCh point13 = new PointCh(SwissBounds.MIN_E + 6000, SwissBounds.MIN_N + 6000);


    Edge edge1 = new Edge(0, 1, point1, point2, point1.distanceTo(point2), DoubleUnaryOperator.identity());
    Edge edge2 = new Edge(1, 2, point2, point3, point2.distanceTo(point3), DoubleUnaryOperator.identity());
    Edge edge3 = new Edge(2, 3, point3, point4, point3.distanceTo(point4), DoubleUnaryOperator.identity());
    Edge edge4 = new Edge(3, 4, point4, point5, point4.distanceTo(point5), DoubleUnaryOperator.identity());
    Edge edge5 = new Edge(4, 5, point5, point6, point5.distanceTo(point6), DoubleUnaryOperator.identity());
    Edge edge6 = new Edge(5, 6, point6, point7, point6.distanceTo(point7), DoubleUnaryOperator.identity());
    Edge edge7 = new Edge(6, 7, point7, point8, point7.distanceTo(point8), DoubleUnaryOperator.identity());
    Edge edge8 = new Edge(7, 8, point8, point9, point8.distanceTo(point9), DoubleUnaryOperator.identity());
    Edge edge9 = new Edge(8, 9, point9, point10, point9.distanceTo(point10), DoubleUnaryOperator.identity());
    Edge edge10 = new Edge(9, 10, point10, point11, point10.distanceTo(point11), DoubleUnaryOperator.identity());
    Edge edge11 = new Edge(10, 11, point11, point12, point11.distanceTo(point12), DoubleUnaryOperator.identity());
    Edge edge12 = new Edge(11, 12, point12, point13, point12.distanceTo(point13), DoubleUnaryOperator.identity());

    private List<Edge> edgesSet1(){
        List<Edge> edgesSet1Array = new ArrayList<>();
        edgesSet1Array.add(edge1);
        edgesSet1Array.add(edge2);
        return edgesSet1Array;
    }

    private List<Edge> edgesSet2(){
        List<Edge> edgesSet2Array = new ArrayList<>();
        edgesSet2Array.add(edge3);
        edgesSet2Array.add(edge4);
        edgesSet2Array.add(edge5);
        return edgesSet2Array;
    }

    private List<Edge> edgesSet3(){
        List<Edge> edgesSet3Array = new ArrayList<>();
        edgesSet3Array.add(edge6);
        return edgesSet3Array;
    }

    private List<Edge> edgesSet4(){
        List<Edge> edgesSet4Array = new ArrayList<>();
        edgesSet4Array.add(edge7);
        edgesSet4Array.add(edge8);
        edgesSet4Array.add(edge9);
        return edgesSet4Array;
    }

    private List<Edge> edgesSet5(){
        List<Edge> edgesSet5Array = new ArrayList<>();
        edgesSet5Array.add(edge10);
        return edgesSet5Array;
    }

    private List<Edge> edgesSet6(){
        List<Edge> edgesSet6Array = new ArrayList<>();
        edgesSet6Array.add(edge11);
        edgesSet6Array.add(edge12);
        return edgesSet6Array;
    }

    private List<Route> segmentsV2Array(Route route1, Route route2){
        List<Route> segments = new ArrayList<>();
        segments.add(route1);
        segments.add(route2);
        return segments;
    }

    private List<Route> segmentsV3Array(Route route1, Route route2, Route route3){
        List<Route> segments = new ArrayList<>();
        segments.add(route1);
        segments.add(route2);
        segments.add(route3);
        return segments;
    }

    private SingleRoute singleRoute(List<Edge> edges){
        return new SingleRoute(edges);
    }

    private MultiRoute multiRoute(List<Route> segments){
        return new MultiRoute(segments);
    }

    SingleRoute single1 = singleRoute(edgesSet1());
    SingleRoute single2 = singleRoute(edgesSet2());
    SingleRoute single3 = singleRoute(edgesSet3());
    SingleRoute single4 = singleRoute(edgesSet4());
    SingleRoute single5 = singleRoute(edgesSet5());
    SingleRoute single6 = singleRoute(edgesSet6());
    MultiRoute multi1 = multiRoute(segmentsV2Array(single1, single2));
    MultiRoute multi2 = multiRoute(segmentsV3Array(single4, single5, single6));
    MultiRoute multiGlobal = multiRoute(segmentsV3Array(multi1, single3, multi2));

    @Test
    void checkExceptionEmptySegmentsArray(){
        List<Route> segments = new ArrayList<>();
        assertThrows(IllegalArgumentException.class, () ->{new MultiRoute(segments);});
    }

    @Test
    void checkAllPoints(){
        List<PointCh> expectedPointsArrayMulti1 =
                List.of(point1, point2, point3, point4, point5, point6);
        List<PointCh> expectedPointsArrayMulti2 =
                List.of(point7, point8, point9, point10, point11, point12, point13);
        List<PointCh> expectedPointsArrayGlobalMulti =
                List.of(point1, point2, point3, point4, point5, point6, point7, point8, point9, point10, point11, point12, point13);


        assertEquals(expectedPointsArrayMulti1, multi1.points());
        assertEquals(expectedPointsArrayMulti2, multi2.points());
        assertEquals(expectedPointsArrayGlobalMulti, multiGlobal.points());
    }

    @Test
    void checkAllEdges(){
        List<Edge> edgeList1 =
                List.of(edge1, edge2, edge3, edge4, edge5);
        List<Edge> edgeList2 =
                List.of(edge7, edge8, edge9, edge10, edge11, edge12);
        List<Edge> edgeListGlobal =
                List.of(edge1, edge2, edge3, edge4, edge5, edge6, edge7, edge8, edge9, edge10, edge11, edge12);

        assertEquals(edgeList1, multi1.edges());
        assertEquals(edgeList2, multi2.edges());
        assertEquals(edgeListGlobal, multiGlobal.edges());
    }

    @Test
    void checkLength(){
        double totalLengthMulti1 = point1.distanceTo(point2) + point2.distanceTo(point3)
                + point3.distanceTo(point4) + point4.distanceTo(point5) + point5.distanceTo(point6);
        double totalLengthMulti2 = point7.distanceTo(point8) + point8.distanceTo(point9)
                + point9.distanceTo(point10) + point10.distanceTo(point11) + point11.distanceTo(point12) + point12.distanceTo(point13);
        double totalLengthMultiGlobal = totalLengthMulti1 + totalLengthMulti2 + point6.distanceTo(point7);

        assertEquals(totalLengthMulti1, multi1.length());
        assertEquals(totalLengthMulti2, multi2.length());
        assertEquals(totalLengthMultiGlobal, multiGlobal.length());
    }

    @Test
    void checkIndexOfSegmentAt(){
        //Multi1
        assertEquals(0, multi1.indexOfSegmentAt(0));
        assertEquals(0, multi1.indexOfSegmentAt(1300.2));
        assertEquals(1, multi1.indexOfSegmentAt(3400.5));
        assertEquals(1, multi1.indexOfSegmentAt(5000));
        assertTrue(multi1.indexOfSegmentAt(2000) == 0 ||  multi1.indexOfSegmentAt(2000) == 1);
        //Multi2
        assertEquals(0, multi2.indexOfSegmentAt(2645.876));
        assertEquals(1, multi2.indexOfSegmentAt(3280.476));
        assertEquals(2, multi2.indexOfSegmentAt(4768.98));
        assertEquals(2, multi2.indexOfSegmentAt(6000));
        assertTrue(multi2.indexOfSegmentAt(4000) == 1 || multi2.indexOfSegmentAt(4000) == 2);
        //MultiGlobal
        assertEquals(0, multiGlobal.indexOfSegmentAt(1999.999));
        assertEquals(1, multiGlobal.indexOfSegmentAt(2000.00001));
        assertEquals(2, multiGlobal.indexOfSegmentAt(5789.765));
        assertEquals(3, multiGlobal.indexOfSegmentAt(6666.666));
        assertEquals(4, multiGlobal.indexOfSegmentAt(9999.999));
        assertEquals(5, multiGlobal.indexOfSegmentAt(12000));
    }

    @Test
    void checkPointAt(){
        //Multi1
        assertEquals(point1, multi1.pointAt(0));
        assertEquals(new PointCh(2486000, 1075000), multi1.pointAt(1000));
        assertEquals(new PointCh(2486598.6, 1076000), multi1.pointAt(2598.6));
        assertEquals(point6, multi1.pointAt(5000));
        //Multi2
        assertEquals(point7, multi2.pointAt(0));
        assertEquals(new PointCh(2490000, 1079867.578), multi2.pointAt(3867.578));
        assertEquals(new PointCh(2491000, 1080999.999), multi2.pointAt(5999.999));
        //MultiGlobal
        assertEquals(new PointCh(2490800, 1080000), multiGlobal.pointAt(10800));
        assertEquals(new PointCh(2488000, 1077678.901), multiGlobal.pointAt(5678.901));
        assertEquals(point9, multiGlobal.pointAt(8000));
        assertEquals(new PointCh(2486000, 1075476.4857483), multiGlobal.pointAt(1476.4857483));
    }

    @Test
    void checkElevationAt(){
        //Multi1
        assertEquals(0, multi1.elevationAt(0));
        assertEquals(432, multi1.elevationAt(1432));
        assertEquals(999.9989999999998, multi1.elevationAt(4999.999));
        //Multi2
        assertEquals(0.01, multi2.elevationAt(0.01));
        assertEquals(546.7800000000002, multi2.elevationAt(2546.78));
        assertEquals(1000, multi2.elevationAt(6000));
        //MultiGlobal
        assertEquals(467.78000000000065, multiGlobal.elevationAt(9467.78));
    }

    @Test
    void checkNodeClosestTo(){
        //Multi1
        assertEquals(0, multi1.nodeClosestTo(450));
        assertEquals(2, multi1.nodeClosestTo(1999.999));
        //Multi2
        assertEquals(12, multi2.nodeClosestTo(5500.0001));
        assertEquals(7, multi2.nodeClosestTo(1000));
        //MultiGlobal
        assertEquals(0, multiGlobal.nodeClosestTo(444.444));
        assertEquals(10, multiGlobal.nodeClosestTo(10499.999));
        assertEquals(5, multiGlobal.nodeClosestTo(5000));
    }

    @Test
    void checkPointClosestTo(){
        //Multi1
        assertEquals(new RoutePoint(point1, 0, 542), multi1.pointClosestTo(new PointCh(2485000, 1075542)));
        assertEquals(new RoutePoint(new PointCh(2486000, 1075000), 1000, 1000),
                multi1.pointClosestTo(new PointCh(2487000, 1075000)));
        //Multi2
        assertEquals(new RoutePoint(new PointCh(2489000, 1078000), 1000, 1118.033988749895),
                multi2.pointClosestTo(new PointCh(2489500, 1077000)));
        assertEquals(new RoutePoint(point7, 0, point1.distanceTo(point7)), multi2.pointClosestTo(point1));
        //MultiGlobal
        assertEquals(new RoutePoint(point13, 12000, 9055.938659244552),
                multiGlobal.pointClosestTo(new PointCh(2500000, 1082005)));
    }
}

