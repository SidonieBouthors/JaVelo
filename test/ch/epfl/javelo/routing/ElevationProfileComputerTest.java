package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.*;

class ElevationProfileComputerTest {

    @Test
    void test() throws IOException {
        List<Edge> a = new ArrayList<>();

        DoubleUnaryOperator function = Functions.sampled(new float[]{0,1,2,3,4,5},6);
        a.add(new Edge(0, 1,
                new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N),
                6, function));

        SingleRoute c = new SingleRoute(a);

        assertEquals(5, function.applyAsDouble(6));

        ElevationProfile d = ElevationProfileComputer.elevationProfile(c,1);
        System.out.println(d.elevationAt(1.25));
        System.out.println("length");
        assertEquals(6,d.length());
        System.out.println("maxelev");
        assertEquals(5,d.maxElevation());
        System.out.println("totalascent");
        assertEquals(5,d.totalAscent());
        System.out.println("total descent ");
        assertEquals(0, d.totalDescent());

    }

    @Test
    void testInterpolation() throws IOException {

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
        ElevationProfile elevationProfile = ElevationProfileComputer.elevationProfile(route, 0.75);
        //assertEquals(Math2.interpolate(2, 5, 0.25), elevationProfile.minElevation());
        //assertEquals(9.2, elevationProfile.maxElevation());
        //assertEquals(9, elevationProfile.length());
        for (int i = 0; i <= 12; i++) {
            System.out.println("pos : " + (i*0.75));
            System.out.println("route el : " + route.elevationAt(i*0.75));
            System.out.println("profile el : " + elevationProfile.elevationAt(i * 0.75));
            System.out.println("");
        }

    }

}