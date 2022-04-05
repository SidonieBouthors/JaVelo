package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MultiRouteTest2 {
    PointCh point1 = new PointCh(2535123, 1152123);
    PointCh point2 = new PointCh(2535234, 1152234);
    PointCh point3 = new PointCh(2535345, 1152345);
    PointCh point4 = new PointCh(2535460, 1152263);
    PointCh point5 = new PointCh(2535617, 1152381);
    PointCh point6 = new PointCh(2535843, 1152401);
    PointCh point7 = new PointCh(2535747.34, 1152285.4);
    PointCh point8 = new PointCh(2535656, 1152175);

    DoubleUnaryOperator profile1 = Functions.sampled(new float[]
            {50, 30, 40, 60}, point1.distanceTo(point2));
    DoubleUnaryOperator profile2 = Functions.sampled(new float[]
            {60, 80, 100, 80}, point2.distanceTo(point3));
    DoubleUnaryOperator profile3 = Functions.sampled(new float[]
            {80, 50, 10, 30}, point3.distanceTo(point4));
    DoubleUnaryOperator profile4 = Functions.sampled(new float[]
            {30, 50, 80, 60}, point4.distanceTo(point5));
    DoubleUnaryOperator profile5 = Functions.sampled(new float[]
            {60, 70, 90, 100}, point5.distanceTo(point6));
    DoubleUnaryOperator profile6 = Functions.sampled(new float[]
            {100, 90, 80, 70}, point6.distanceTo(point7));
    DoubleUnaryOperator profile7 = Functions.sampled(new float[]
            {70, 60, 80, 70}, point7.distanceTo(point8));
    DoubleUnaryOperator profile3Reverse = Functions.sampled(new float[]
            {30, 10, 50, 80}, point4.distanceTo(point3));
    DoubleUnaryOperator profile5Reverse = Functions.sampled(new float[]
            {100, 90, 70, 60}, point6.distanceTo(point5));
    DoubleUnaryOperator profile7Reverse = Functions.sampled(new float[]
            {70, 80, 60, 70}, point8.distanceTo(point7));


    Edge edge1 = new Edge(0, 1, point1, point2, point1.distanceTo(point2), profile1);
    Edge edge2 = new Edge(1, 2, point2, point3, point2.distanceTo(point3), profile2);
    Edge edge3 = new Edge(2, 3, point3, point4, point3.distanceTo(point4), profile3);
    Edge edge4 = new Edge(3, 4, point4, point5, point4.distanceTo(point5), profile4);
    Edge edge5 = new Edge(4, 5, point5, point6, point5.distanceTo(point6), profile5);
    Edge edge6 = new Edge(5, 6, point6, point7, point6.distanceTo(point7), profile6);
    Edge edge7 = new Edge(6, 7, point7, point8, point7.distanceTo(point8), profile7);
    Edge edge3Reverse = new Edge(3, 2, point4, point3, point4.distanceTo(point3), profile3Reverse);
    Edge edge5Reverse = new Edge(5, 4, point6, point5, point6.distanceTo(point5), profile5Reverse);
    Edge edge7Reverse = new Edge(7, 6, point8, point7, point8.distanceTo(point7), profile7Reverse);

    List<Edge> edges1;
    List<PointCh> points1;
    SingleRoute singleRoute1;

    List<Edge> edges2;
    List<PointCh> points2;
    SingleRoute singleRoute2;

    List<Edge> edges3;
    List<PointCh> points3;
    SingleRoute singleRoute3;

    List<Edge> edges4;
    List<PointCh> points4;
    SingleRoute singleRoute4;

    MultiRoute multiRoute1;
    MultiRoute multiRoute2;
    MultiRoute multiRoute3;
    MultiRoute multiRoute4;
    MultiRoute multiRoute5;

    void create() {
        this.edges1 = List.of(edge1, edge2);
        this.edges2 = List.of(edge3, edge3Reverse, edge3);
        this.edges3 = List.of(edge4, edge5, edge5Reverse, edge5);
        this.edges4 = List.of(edge6, edge7, edge7Reverse, edge7);
        this.points1 = List.of(point1, point2, point3);
        this.points2 = List.of(point3, point4, point3, point4);
        this.points3 = List.of(point4, point5, point6, point5, point6);
        this.points4 = List.of(point6, point7, point8, point7, point8);

        singleRoute1 = new SingleRoute(edges1);
        singleRoute2 = new SingleRoute(edges2);
        singleRoute3 = new SingleRoute(edges3);
        singleRoute4 = new SingleRoute(edges4);

        List<Route> routes1 = List.of(singleRoute1, singleRoute2, singleRoute3, singleRoute4);
        multiRoute1 = new MultiRoute(List.copyOf(routes1));

        List<Route> routeIntermediaire1 = List.of(singleRoute1, singleRoute2);
        multiRoute2 = new MultiRoute((List.copyOf(routeIntermediaire1)));

        List<Route> routes2 = List.of(singleRoute3, singleRoute4);
        multiRoute3 = new MultiRoute(List.copyOf(routes2));

        List<Route> routes3 = List.of(multiRoute2, singleRoute3, singleRoute4);
        multiRoute4 = new MultiRoute(List.copyOf(routes3));

        List<Route> routes4 = List.of(multiRoute2, multiRoute3);
        multiRoute5 = new MultiRoute(List.copyOf(routes4));

    }

    @Test
    void MultiRouteThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new MultiRoute(List.of());
        });
    }

    @Test
    void IndexOfSegmentAtWorksOnSingleRoutesOnly() {
        create();

        //multiRoute 1
        assertEquals(0, multiRoute1.indexOfSegmentAt(100));
        assertEquals(1, multiRoute1.indexOfSegmentAt(500));
        assertEquals(1, multiRoute1.indexOfSegmentAt(600));
        assertEquals(2, multiRoute1.indexOfSegmentAt(1000));
        assertEquals(3, multiRoute1.indexOfSegmentAt(2100));

        //multiRoute 2
        assertEquals(0, multiRoute2.indexOfSegmentAt(100));
        assertEquals(0, multiRoute2.indexOfSegmentAt(200));
        assertEquals(1, multiRoute2.indexOfSegmentAt(650));
        assertEquals(1, multiRoute2.indexOfSegmentAt(700));

        //multiRoute 3
        assertEquals(0, multiRoute3.indexOfSegmentAt(100));
        assertEquals(0, multiRoute3.indexOfSegmentAt(500));
        assertEquals(0, multiRoute3.indexOfSegmentAt(650));
        assertEquals(0, multiRoute3.indexOfSegmentAt(800));
        assertEquals(1, multiRoute3.indexOfSegmentAt(1000));
        assertEquals(1, multiRoute3.indexOfSegmentAt(1200));
        assertEquals(1, multiRoute3.indexOfSegmentAt(1400));
    }

    @Test
    void IndexOfSegmentAtWorksOnMixedRoutes() {
        create();

        //multiRoute4
        assertEquals(0, multiRoute4.indexOfSegmentAt(100));
        assertEquals(0, multiRoute4.indexOfSegmentAt(200));
        assertEquals(1, multiRoute4.indexOfSegmentAt(650));
        assertEquals(1, multiRoute4.indexOfSegmentAt(700));
        assertEquals(1, multiRoute4.indexOfSegmentAt(725));
        assertEquals(2, multiRoute4.indexOfSegmentAt(1000));
        assertEquals(2, multiRoute4.indexOfSegmentAt(1500));
        assertEquals(3, multiRoute4.indexOfSegmentAt(1750));
        assertEquals(3, multiRoute4.indexOfSegmentAt(2100));

    }

    @Test
    void IndexOfSegmentAtWorksOnMultiRoutesOfMultiRoutes() {
        create();

        //multiRoute5
        assertEquals(0, multiRoute5.indexOfSegmentAt(100));
        assertEquals(0, multiRoute5.indexOfSegmentAt(200));
        assertEquals(1, multiRoute5.indexOfSegmentAt(650));
        assertEquals(1, multiRoute5.indexOfSegmentAt(700));
        assertEquals(2, multiRoute5.indexOfSegmentAt(1000));
        assertEquals(2, multiRoute5.indexOfSegmentAt(1500));
        assertEquals(3, multiRoute5.indexOfSegmentAt(1750));
        assertEquals(3, multiRoute5.indexOfSegmentAt(2100));
    }

    @Test
    void IndexOfSegmentAtWorksOnEdgeCases() {
        create();

        //clamp works on negative cases
        assertEquals(0, multiRoute1.indexOfSegmentAt(-1));
        assertEquals(0, multiRoute2.indexOfSegmentAt(-1));
        assertEquals(0, multiRoute3.indexOfSegmentAt(-1));
        assertEquals(0, multiRoute4.indexOfSegmentAt(-1));
        assertEquals(0, multiRoute5.indexOfSegmentAt(-1));

        //clamp works on position > length of the road
        assertEquals(3, multiRoute1.indexOfSegmentAt(multiRoute1.length() + 1));
        assertEquals(1, multiRoute2.indexOfSegmentAt(multiRoute2.length() + 1));
        assertEquals(1, multiRoute3.indexOfSegmentAt(multiRoute3.length() + 1));
        assertEquals(3, multiRoute4.indexOfSegmentAt(multiRoute4.length() + 1));
        assertEquals(3, multiRoute5.indexOfSegmentAt(multiRoute5.length() + 1));

        //works for position = 0
        assertEquals(0, multiRoute1.indexOfSegmentAt(0));
        assertEquals(0, multiRoute2.indexOfSegmentAt(0));
        assertEquals(0, multiRoute3.indexOfSegmentAt(0));
        assertEquals(0, multiRoute4.indexOfSegmentAt(0));
        assertEquals(0, multiRoute5.indexOfSegmentAt(0));

        //works for position = length
        assertEquals(3, multiRoute1.indexOfSegmentAt(multiRoute1.length()));
        assertEquals(1, multiRoute2.indexOfSegmentAt(multiRoute2.length()));
        assertEquals(1, multiRoute3.indexOfSegmentAt(multiRoute3.length()));
        assertEquals(3, multiRoute4.indexOfSegmentAt(multiRoute4.length()));
        assertEquals(3, multiRoute5.indexOfSegmentAt(multiRoute4.length()));

    }

    @Test
    void LengthWorks() {
        create();

        double expectedLength1And4And5 = singleRoute1.length() + singleRoute2.length() + singleRoute3.length() + singleRoute4.length();
        double expectedLength2 = singleRoute1.length() + singleRoute2.length();
        double expectedLength3 = singleRoute3.length() + singleRoute4.length();

        assertEquals(expectedLength1And4And5, multiRoute1.length());
        assertEquals(expectedLength2, multiRoute2.length());
        assertEquals(expectedLength3, multiRoute3.length());
        assertEquals(expectedLength1And4And5, multiRoute4.length());
        assertEquals(expectedLength1And4And5, multiRoute5.length());
    }

    @Test
    void EdgesWorks() {
        create();

        List<Edge> expected1And4And5 = new ArrayList<>();
        expected1And4And5.addAll(edges1);
        expected1And4And5.addAll(edges2);
        expected1And4And5.addAll(edges3);
        expected1And4And5.addAll(edges4);

        List<Edge> expected2 = new ArrayList<>();
        expected2.addAll(edges1);
        expected2.addAll(edges2);

        List<Edge> expected3 = new ArrayList<>();
        expected3.addAll(edges3);
        expected3.addAll(edges4);

        assertEquals(expected1And4And5, multiRoute1.edges());
        assertEquals(expected2, multiRoute2.edges());
        assertEquals(expected3, multiRoute3.edges());
        assertEquals(expected1And4And5, multiRoute4.edges());
        assertEquals(expected1And4And5, multiRoute5.edges());
    }

    @Test
    void PointsWorks() {
        create();

        List<PointCh> expectedPoints1And4And5 = List.of(point1, point2, point3, point4, point3, point4, point5, point6, point5, point6, point7, point8, point7, point8);
        List<PointCh> expectedPoints2 = List.of(point1, point2, point3, point4, point3, point4);
        List<PointCh> expectedPoints3 = List.of(point4, point5, point6, point5, point6, point7, point8, point7, point8);

        assertEquals(expectedPoints1And4And5, multiRoute1.points());
        assertEquals(expectedPoints2, multiRoute2.points());
        assertEquals(expectedPoints3, multiRoute3.points());
        assertEquals(expectedPoints1And4And5, multiRoute4.points());
        assertEquals(expectedPoints1And4And5, multiRoute5.points());
    }

    @Test
    void PointAtWorksOnKnownValues() {
        create();

        //multiRoute1
        assertEquals(new PointCh(2535193.7106781187, 1152193.7106781187), multiRoute1.pointAt(100));
        assertEquals(new PointCh(2535423.5203409707, 1152289.0115829601), multiRoute1.pointAt(500));
        assertEquals(new PointCh(2535347.900817107, 1152342.9315912803), multiRoute1.pointAt(600));
        assertEquals(new PointCh(2535682.665075113, 1152386.811068594), multiRoute1.pointAt(1000));
        assertEquals(new PointCh(2535716.326987979, 1152247.915474851), multiRoute1.pointAt(2100));

        //multiRoute2
        assertEquals(new PointCh(2535193.7106781187, 1152193.7106781187), multiRoute2.pointAt(100));
        assertEquals(new PointCh(2535264.4213562375, 1152264.4213562373), multiRoute2.pointAt(200));
        assertEquals(new PointCh(2535388.611396146, 1152313.9031784004), multiRoute2.pointAt(650));
        assertEquals(new PointCh(2535429.3219751846, 1152284.8747655204), multiRoute2.pointAt(700));

        //multiRoute3
        assertEquals(new PointCh(2535539.9388587554, 1152323.0814352431), multiRoute3.pointAt(100));
        assertEquals(new PointCh(2535766.5819807285, 1152394.2373434273), multiRoute3.pointAt(500));
        assertEquals(new PointCh(2535617.1659131846, 1152381.0146825828), multiRoute3.pointAt(650));
        assertEquals(new PointCh(2535766.250154359, 1152394.2079782619), multiRoute3.pointAt(800));
        assertEquals(new PointCh(2535764.6152768503, 1152306.2762492567), multiRoute3.pointAt(1000));
        assertEquals(new PointCh(2535674.879029043, 1152197.8185330231), multiRoute3.pointAt(1200));
        assertEquals(new PointCh(2535692.308568288, 1152218.8851099082), multiRoute3.pointAt(1400));

        //multiRoute4
        assertEquals(new PointCh(2535193.7106781187, 1152193.7106781187), multiRoute4.pointAt(100));
        assertEquals(new PointCh(2535264.4213562375, 1152264.4213562373), multiRoute4.pointAt(200));
        assertEquals(new PointCh(2535388.611396146, 1152313.9031784004), multiRoute4.pointAt(650));
        assertEquals(new PointCh(2535429.3219751846, 1152284.8747655204), multiRoute4.pointAt(700));
        assertEquals(new PointCh(2535682.665075113, 1152386.811068594), multiRoute4.pointAt(1000));
        assertEquals(new PointCh(2535728.7186335917, 1152390.8866047426), multiRoute4.pointAt(1500));
        assertEquals(new PointCh(2535756.759730425, 1152296.7832410324), multiRoute4.pointAt(1750));
        assertEquals(new PointCh(2535716.326987979, 1152247.915474851), multiRoute4.pointAt(2100));

        //multiRoute5
        assertEquals(new PointCh(2535193.7106781187, 1152193.7106781187), multiRoute5.pointAt(100));
        assertEquals(new PointCh(2535264.4213562375, 1152264.4213562373), multiRoute5.pointAt(200));
        assertEquals(new PointCh(2535388.611396146, 1152313.9031784004), multiRoute5.pointAt(650));
        assertEquals(new PointCh(2535429.3219751846, 1152284.8747655204), multiRoute5.pointAt(700));
        assertEquals(new PointCh(2535682.665075113, 1152386.811068594), multiRoute5.pointAt(1000));
        assertEquals(new PointCh(2535728.7186335917, 1152390.8866047426), multiRoute5.pointAt(1500));
        assertEquals(new PointCh(2535756.759730425, 1152296.7832410324), multiRoute5.pointAt(1750));
        assertEquals(new PointCh(2535716.326987979, 1152247.915474851), multiRoute5.pointAt(2100));
    }

    @Test
    void PointAtWorksOnEdgeValues() {
        create();

        //works on negative values
        assertEquals(point1, multiRoute1.pointAt(-1));
        assertEquals(point1, multiRoute2.pointAt(-1));
        assertEquals(point4, multiRoute3.pointAt(-1));
        assertEquals(point1, multiRoute4.pointAt(-1));
        assertEquals(point1, multiRoute5.pointAt(-1));

        //works on values greater than length
        assertEquals(point8, multiRoute1.pointAt(multiRoute1.length() + 1));
        assertEquals(point4, multiRoute2.pointAt(multiRoute2.length() + 1));
        assertEquals(point8, multiRoute3.pointAt(multiRoute3.length() + 1));
        assertEquals(point8, multiRoute4.pointAt(multiRoute4.length() + 1));
        assertEquals(point8, multiRoute5.pointAt(multiRoute4.length() + 1));

        //works on values equal to 0
        assertEquals(point1, multiRoute1.pointAt(0));
        assertEquals(point1, multiRoute2.pointAt(0));
        assertEquals(point4, multiRoute3.pointAt(0));
        assertEquals(point1, multiRoute4.pointAt(0));
        assertEquals(point1, multiRoute5.pointAt(0));

        //works on values equal to length
        assertEquals(point8, multiRoute1.pointAt(multiRoute1.length()));
        assertEquals(point4, multiRoute2.pointAt(multiRoute2.length()));
        assertEquals(point8, multiRoute3.pointAt(multiRoute3.length()));
        assertEquals(point8, multiRoute4.pointAt(multiRoute4.length()));
        assertEquals(point8, multiRoute5.pointAt(multiRoute4.length()));
    }

    @Test
    void ElevationAtWorksOnKnownValues() {
        create();

        //multiRoute1
        assertEquals(39.11099408612291, multiRoute1.elevationAt(100), 1e-12);
        assertEquals(10.96713441948415, multiRoute1.elevationAt(500), 1e-12);
        assertEquals(77.72979530758599, multiRoute1.elevationAt(600), 1e-12);
        assertEquals(68.7166028911134, multiRoute1.elevationAt(1000), 1e-12);
        assertEquals(60.37202453747647, multiRoute1.elevationAt(2100), 1e-12);

        //multiRoute2
        assertEquals(39.11099408612291, multiRoute2.elevationAt(100), 1e-12);
        assertEquals(76.44397634449163, multiRoute2.elevationAt(200), 1e-12);
        assertEquals(44.49245619568782, multiRoute2.elevationAt(650), 1e-12);
        assertEquals(13.994074009369504, multiRoute2.elevationAt(700), 1e-12);

        //multiRoute3
        assertEquals(65.8248234904391, multiRoute3.elevationAt(100), 1e-12);
        assertEquals(89.7120302818561, multiRoute3.elevationAt(500), 1e-12);
        assertEquals(60.02202387408028, multiRoute3.elevationAt(650), 1e-12);
        assertEquals(89.62393478553496, multiRoute3.elevationAt(800), 1e-12);
        assertEquals(75.4177117448635, multiRoute3.elevationAt(1000), 1e-12);
        assertEquals(76.20068832149688, multiRoute3.elevationAt(1200), 1e-12);
        assertEquals(76.14939678896384, multiRoute3.elevationAt(1400), 1e-12);

        //multiRoute4
        assertEquals(39.11099408612291, multiRoute4.elevationAt(100), 1e-12);
        assertEquals(76.44397634449163, multiRoute4.elevationAt(200), 1e-12);
        assertEquals(44.49245619568782, multiRoute4.elevationAt(650), 1e-12);
        assertEquals(13.994074009369504, multiRoute4.elevationAt(700), 1e-12);
        assertEquals(68.7166028911134, multiRoute4.elevationAt(1000), 1e-12);
        assertEquals(79.65981422787853, multiRoute4.elevationAt(1500), 1e-12);
        assertEquals(72.95412829561062, multiRoute4.elevationAt(1750), 1e-12);
        assertEquals(60.37202453747647, multiRoute4.elevationAt(2100), 1e-12);

        //multiRoute5
        assertEquals(39.11099408612291, multiRoute5.elevationAt(100), 1e-12);
        assertEquals(76.44397634449163, multiRoute5.elevationAt(200), 1e-12);
        assertEquals(44.49245619568782, multiRoute5.elevationAt(650), 1e-12);
        assertEquals(13.994074009369504, multiRoute5.elevationAt(700), 1e-12);
        assertEquals(68.7166028911134, multiRoute5.elevationAt(1000), 1e-12);
        assertEquals(79.65981422787853, multiRoute5.elevationAt(1500), 1e-12);
        assertEquals(72.95412829561062, multiRoute5.elevationAt(1750), 1e-12);
        assertEquals(60.37202453747647, multiRoute5.elevationAt(2100), 1e-12);
    }

    @Test
    void ElevationAtWorksOnEdgeValues() {
        create();

        //works on negative values
        assertEquals(50, multiRoute1.elevationAt(-1), 1e-12);
        assertEquals(50, multiRoute2.elevationAt(-1), 1e-12);
        assertEquals(30, multiRoute3.elevationAt(-1), 1e-12);
        assertEquals(50, multiRoute4.elevationAt(-1), 1e-12);
        assertEquals(50, multiRoute5.elevationAt(-1), 1e-12);

        //works on values greater than length
        assertEquals(70, multiRoute1.elevationAt(multiRoute1.length() + 1), 1e-12);
        assertEquals(30, multiRoute2.elevationAt(multiRoute2.length() + 1), 1e-12);
        assertEquals(70, multiRoute3.elevationAt(multiRoute3.length() + 1), 1e-12);
        assertEquals(70, multiRoute4.elevationAt(multiRoute4.length() + 1), 1e-12);
        assertEquals(70, multiRoute5.elevationAt(multiRoute5.length() + 1), 1e-12);

        //works on values equal to 0
        assertEquals(50, multiRoute1.elevationAt(0), 1e-12);
        assertEquals(50, multiRoute2.elevationAt(0), 1e-12);
        assertEquals(30, multiRoute3.elevationAt(0), 1e-12);
        assertEquals(50, multiRoute4.elevationAt(0), 1e-12);
        assertEquals(50, multiRoute5.elevationAt(0), 1e-12);

        //works on values equal to length
        assertEquals(70, multiRoute1.elevationAt(multiRoute1.length()), 1e-12);
        assertEquals(30, multiRoute2.elevationAt(multiRoute2.length()), 1e-12);
        assertEquals(70, multiRoute3.elevationAt(multiRoute3.length()), 1e-12);
        assertEquals(70, multiRoute4.elevationAt(multiRoute4.length()), 1e-12);
        assertEquals(70, multiRoute5.elevationAt(multiRoute5.length()), 1e-12);
    }

    @Test
    void NodeClosestToWorksOnKnownValues() {
        create();

        //multiRoute1
        assertEquals(1, multiRoute1.nodeClosestTo(100));
        assertEquals(3, multiRoute1.nodeClosestTo(500));
        assertEquals(2, multiRoute1.nodeClosestTo(600));
        assertEquals(4, multiRoute1.nodeClosestTo(1000));
        assertEquals(6, multiRoute1.nodeClosestTo(2100));

        //multiRoute2
        assertEquals(1, multiRoute2.nodeClosestTo(100));
        assertEquals(1, multiRoute2.nodeClosestTo(200));
        assertEquals(2, multiRoute2.nodeClosestTo(650));
        assertEquals(3, multiRoute2.nodeClosestTo(700));

        //multiRoute3
        assertEquals(4, multiRoute3.nodeClosestTo(100));
        assertEquals(5, multiRoute3.nodeClosestTo(500));
        assertEquals(4, multiRoute3.nodeClosestTo(650));
        assertEquals(5, multiRoute3.nodeClosestTo(800));
        assertEquals(6, multiRoute3.nodeClosestTo(1000));
        assertEquals(7, multiRoute3.nodeClosestTo(1200));
        assertEquals(7, multiRoute3.nodeClosestTo(1400));

        //multiRoute4
        assertEquals(1, multiRoute4.nodeClosestTo(100));
        assertEquals(1, multiRoute4.nodeClosestTo(200));
        assertEquals(2, multiRoute4.nodeClosestTo(650));
        assertEquals(3, multiRoute4.nodeClosestTo(700));
        assertEquals(4, multiRoute4.nodeClosestTo(1000));
        assertEquals(4, multiRoute4.nodeClosestTo(1500));
        assertEquals(6, multiRoute4.nodeClosestTo(1750));
        assertEquals(6, multiRoute4.nodeClosestTo(2100));

        //multiRoute5
        assertEquals(1, multiRoute5.nodeClosestTo(100));
        assertEquals(1, multiRoute5.nodeClosestTo(200));
        assertEquals(2, multiRoute5.nodeClosestTo(650));
        assertEquals(3, multiRoute5.nodeClosestTo(700));
        assertEquals(4, multiRoute5.nodeClosestTo(1000));
        assertEquals(4, multiRoute5.nodeClosestTo(1500));
        assertEquals(6, multiRoute5.nodeClosestTo(1750));
        assertEquals(6, multiRoute5.nodeClosestTo(2100));

    }

    @Test
    void NodeClosestToWorksOnEdgeValues() {
        create();

        //works on negative values
        assertEquals(0, multiRoute1.nodeClosestTo(-1));
        assertEquals(0, multiRoute2.nodeClosestTo(-1));
        assertEquals(3, multiRoute3.nodeClosestTo(-1));
        assertEquals(0, multiRoute4.nodeClosestTo(-1));
        assertEquals(0, multiRoute5.nodeClosestTo(-1));

        //works on values greater than length
        assertEquals(7, multiRoute1.nodeClosestTo(multiRoute1.length() + 1));
        assertEquals(3, multiRoute2.nodeClosestTo(multiRoute2.length() + 1));
        assertEquals(7, multiRoute3.nodeClosestTo(multiRoute3.length() + 1));
        assertEquals(7, multiRoute4.nodeClosestTo(multiRoute4.length() + 1));
        assertEquals(7, multiRoute5.nodeClosestTo(multiRoute5.length() + 1));

        //works on values equal to 0
        assertEquals(0, multiRoute1.nodeClosestTo(0));
        assertEquals(0, multiRoute2.nodeClosestTo(0));
        assertEquals(3, multiRoute3.nodeClosestTo(0));
        assertEquals(0, multiRoute4.nodeClosestTo(0));
        assertEquals(0, multiRoute5.nodeClosestTo(0));

        //works on values equal to length
        assertEquals(7, multiRoute1.nodeClosestTo(multiRoute1.length()));
        assertEquals(3, multiRoute2.nodeClosestTo(multiRoute2.length()));
        assertEquals(7, multiRoute3.nodeClosestTo(multiRoute3.length()));
        assertEquals(7, multiRoute4.nodeClosestTo(multiRoute4.length()));
        assertEquals(7, multiRoute5.nodeClosestTo(multiRoute5.length()));
    }

    @Test
    void PointClosestToWorksOnValuesOutsideOfSegments() {
        create();

        //multiRoute1
        assertEquals(new RoutePoint(new PointCh(2535151.33161, 1152151.3316100002), 40.0669471060211, 23.862506270602033), multiRoute1.pointClosestTo(new PointCh(2535168.20495, 1152134.45827)));
        assertEquals(new RoutePoint(new PointCh(2535284.984775, 1152284.984775), 229.0810657028165, 41.96730365141696), multiRoute1.pointClosestTo(new PointCh(2535314.66014,1152255.30941)));
        assertEquals(new RoutePoint(new PointCh(2535423.259410883, 1152289.1976374576), 410.0722098832407, 26.526615400166015), multiRoute1.pointClosestTo(new PointCh(2535407.8589,1152267.59936)));
        assertEquals(new RoutePoint(new PointCh(2535532.0362687283, 1152317.1419089804), 827.7924046784461, 22.58244776137388), multiRoute1.pointClosestTo(new PointCh(2535518.46841,1152335.19406)));
        assertEquals(new RoutePoint(new PointCh(2535703.020791555, 1152388.6124594295), 1020.4352685523834, 25.541510684196023), multiRoute1.pointClosestTo(new PointCh(2535700.76928,1152414.05454)));
        assertEquals(new RoutePoint(new PointCh(2535797.894931484, 1152346.4929341371), 1685.477458553404, 24.148620127378198), multiRoute1.pointClosestTo(new PointCh(2535816.4996,1152331.09741)));
        assertEquals(new RoutePoint(new PointCh(2535709.192834078, 1152239.292630635), 1824.6175577146926, 26.286132011657273), multiRoute1.pointClosestTo(new PointCh(2535729.44582,1152222.53622)));

        //multiRoute2
        assertEquals(new RoutePoint(new PointCh(2535151.33161, 1152151.3316100002), 40.0669471060211, 23.862506270602033), multiRoute2.pointClosestTo(new PointCh(2535168.20495, 1152134.45827)));
        assertEquals(new RoutePoint(new PointCh(2535284.984775, 1152284.984775), 229.0810657028165, 41.96730365141696), multiRoute2.pointClosestTo(new PointCh(2535314.66014,1152255.30941)));
        assertEquals(new RoutePoint(new PointCh(2535423.259410883, 1152289.1976374576), 410.0722098832407, 26.526615400166015), multiRoute2.pointClosestTo(new PointCh(2535407.8589,1152267.59936)));

        //multiRoute3
        assertEquals(new RoutePoint(new PointCh(2535532.0362687283, 1152317.1419089804), 90.11420709481865, 22.58244776137388), multiRoute3.pointClosestTo(new PointCh(2535518.46841,1152335.19406)));
        assertEquals(new RoutePoint(new PointCh(2535703.020791555, 1152388.6124594295), 282.75707096875595, 25.541510684196023), multiRoute3.pointClosestTo(new PointCh(2535700.76928,1152414.05454)));
        assertEquals(new RoutePoint(new PointCh(2535797.894931484, 1152346.4929341371), 947.7992609697766, 24.148620127378198), multiRoute3.pointClosestTo(new PointCh(2535816.4996,1152331.09741)));
        assertEquals(new RoutePoint(new PointCh(2535709.192834078, 1152239.292630635), 1086.939360131065, 26.286132011657273), multiRoute3.pointClosestTo(new PointCh(2535729.44582,1152222.53622)));

        //multiRoute4
        assertEquals(new RoutePoint(new PointCh(2535151.33161, 1152151.3316100002), 40.0669471060211, 23.862506270602033), multiRoute4.pointClosestTo(new PointCh(2535168.20495, 1152134.45827)));
        assertEquals(new RoutePoint(new PointCh(2535284.984775, 1152284.984775), 229.0810657028165, 41.96730365141696), multiRoute4.pointClosestTo(new PointCh(2535314.66014,1152255.30941)));
        assertEquals(new RoutePoint(new PointCh(2535423.259410883, 1152289.1976374576), 410.0722098832407, 26.526615400166015), multiRoute4.pointClosestTo(new PointCh(2535407.8589,1152267.59936)));
        assertEquals(new RoutePoint(new PointCh(2535532.0362687283, 1152317.1419089804), 827.7924046784461, 22.58244776137388), multiRoute4.pointClosestTo(new PointCh(2535518.46841,1152335.19406)));
        assertEquals(new RoutePoint(new PointCh(2535703.020791555, 1152388.6124594295), 1020.4352685523834, 25.541510684196023), multiRoute4.pointClosestTo(new PointCh(2535700.76928,1152414.05454)));
        assertEquals(new RoutePoint(new PointCh(2535797.894931484, 1152346.4929341371), 1685.477458553404, 24.148620127378198), multiRoute4.pointClosestTo(new PointCh(2535816.4996,1152331.09741)));
        assertEquals(new RoutePoint(new PointCh(2535709.192834078, 1152239.292630635), 1824.6175577146926, 26.286132011657273), multiRoute4.pointClosestTo(new PointCh(2535729.44582,1152222.53622)));

        //multiRoute5
        assertEquals(new RoutePoint(new PointCh(2535151.33161, 1152151.3316100002), 40.0669471060211, 23.862506270602033), multiRoute5.pointClosestTo(new PointCh(2535168.20495, 1152134.45827)));
        assertEquals(new RoutePoint(new PointCh(2535284.984775, 1152284.984775), 229.0810657028165, 41.96730365141696), multiRoute5.pointClosestTo(new PointCh(2535314.66014,1152255.30941)));
        assertEquals(new RoutePoint(new PointCh(2535423.259410883, 1152289.1976374576), 410.0722098832407, 26.526615400166015), multiRoute5.pointClosestTo(new PointCh(2535407.8589,1152267.59936)));
        assertEquals(new RoutePoint(new PointCh(2535532.0362687283, 1152317.1419089804), 827.7924046784461, 22.58244776137388), multiRoute5.pointClosestTo(new PointCh(2535518.46841,1152335.19406)));
        assertEquals(new RoutePoint(new PointCh(2535703.020791555, 1152388.6124594295), 1020.4352685523834, 25.541510684196023), multiRoute5.pointClosestTo(new PointCh(2535700.76928,1152414.05454)));
        assertEquals(new RoutePoint(new PointCh(2535797.894931484, 1152346.4929341371), 1685.4774585534042, 24.148620127378198), multiRoute5.pointClosestTo(new PointCh(2535816.4996,1152331.09741)));
        assertEquals(new RoutePoint(new PointCh(2535709.192834078, 1152239.292630635), 1824.6175577146926, 26.286132011657273), multiRoute5.pointClosestTo(new PointCh(2535729.44582,1152222.53622)));
    }

    @Test
    void PointClosestToWorksForValuesOnSegments() {
        create();

        //multiRoute1
        assertEquals(new RoutePoint(point1, 0, 0), multiRoute1.pointClosestTo(point1));
        assertEquals(new RoutePoint(point2, 156.97770542341354, 0), multiRoute1.pointClosestTo(point2));
        assertEquals(new RoutePoint(point3, 313.9554108468271, 0), multiRoute1.pointClosestTo(point3));
        assertEquals(new RoutePoint(point4, 455.19633975909386, 0), multiRoute1.pointClosestTo(point4));
        assertEquals(new RoutePoint(point5, 934.078299416595, 0), multiRoute1.pointClosestTo(point5));
        assertEquals(new RoutePoint(point6, 1160.961529294874, 0), multiRoute1.pointClosestTo(point6));
        assertEquals(new RoutePoint(point7, 1764.7753002570983, 0), multiRoute1.pointClosestTo(point7));
        assertEquals(new RoutePoint(point8, 1908.062269657502, 0), multiRoute1.pointClosestTo(point8));

        //multiRoute2
        assertEquals(new RoutePoint(point1, 0, 0), multiRoute2.pointClosestTo(point1));
        assertEquals(new RoutePoint(point2, 156.97770542341354, 0), multiRoute2.pointClosestTo(point2));
        assertEquals(new RoutePoint(point3, 313.9554108468271, 0), multiRoute2.pointClosestTo(point3));
        assertEquals(new RoutePoint(point4, 455.19633975909386, 0), multiRoute2.pointClosestTo(point4));

        //multiRoute3
        assertEquals(new RoutePoint(point4, 0, 0), multiRoute3.pointClosestTo(point4));
        assertEquals(new RoutePoint(point5, 196.4001018329675, 0), multiRoute3.pointClosestTo(point5));
        assertEquals(new RoutePoint(point6, 423.2833317112465, 0), multiRoute3.pointClosestTo(point6));
        assertEquals(new RoutePoint(point7, 1027.0971026734708, 0), multiRoute3.pointClosestTo(point7));
        assertEquals(new RoutePoint(point8, 1170.3840720738744, 0), multiRoute3.pointClosestTo(point8));

        //multiRoute4
        assertEquals(new RoutePoint(point1, 0, 0), multiRoute4.pointClosestTo(point1));
        assertEquals(new RoutePoint(point2, 156.97770542341354, 0), multiRoute4.pointClosestTo(point2));
        assertEquals(new RoutePoint(point3, 313.9554108468271, 0), multiRoute4.pointClosestTo(point3));
        assertEquals(new RoutePoint(point4, 455.19633975909386, 0), multiRoute4.pointClosestTo(point4));
        assertEquals(new RoutePoint(point5, 934.078299416595, 0), multiRoute4.pointClosestTo(point5));
        assertEquals(new RoutePoint(point6, 1160.961529294874, 0), multiRoute4.pointClosestTo(point6));
        assertEquals(new RoutePoint(point7, 1764.7753002570983, 0), multiRoute4.pointClosestTo(point7));
        assertEquals(new RoutePoint(point8, 1908.062269657502, 0), multiRoute4.pointClosestTo(point8));

        //multiRoute5
        assertEquals(new RoutePoint(point1, 0, 0), multiRoute5.pointClosestTo(point1));
        assertEquals(new RoutePoint(point2, 156.97770542341354, 0), multiRoute5.pointClosestTo(point2));
        assertEquals(new RoutePoint(point3, 313.9554108468271, 0), multiRoute5.pointClosestTo(point3));
        assertEquals(new RoutePoint(point4, 455.19633975909386, 0), multiRoute5.pointClosestTo(point4));
        assertEquals(new RoutePoint(point5, 934.078299416595, 0), multiRoute5.pointClosestTo(point5));
        assertEquals(new RoutePoint(point6, 1160.961529294874, 0), multiRoute5.pointClosestTo(point6));
        assertEquals(new RoutePoint(point7, 1764.7753002570983, 0), multiRoute5.pointClosestTo(point7));
        assertEquals(new RoutePoint(point8, 1908.062269657502, 0), multiRoute5.pointClosestTo(point8));
    }
}
