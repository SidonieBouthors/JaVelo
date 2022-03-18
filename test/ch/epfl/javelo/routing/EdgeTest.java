package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.*;

class EdgeTest {

    @Test
    void edgeMainTest(){
        PointCh fromPoint = new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        double length = Math.sqrt(Math.pow(SwissBounds.HEIGHT,2)+Math.pow(SwissBounds.WIDTH, 2));
        DoubleUnaryOperator profile = Functions.constant(10.05);
        Edge edge = new Edge(1, 2, fromPoint, toPoint, length, profile);

        int eDecalage = 0;
        int nDecalage = 0;
        while (eDecalage < SwissBounds.WIDTH && nDecalage < SwissBounds.HEIGHT) {
            PointCh closest1 = new PointCh(SwissBounds.MAX_E - eDecalage, SwissBounds.MIN_N + nDecalage);
            PointCh closest2 = new PointCh(SwissBounds.MIN_E + eDecalage, SwissBounds.MAX_N - nDecalage);
            System.out.println(edge.positionClosestTo(closest1));
            assertEquals(length, edge.positionClosestTo(closest1)+ edge.positionClosestTo(closest2), 10e-6);
            eDecalage += SwissBounds.WIDTH / 200;
            nDecalage += SwissBounds.HEIGHT / 200;
        }

        PointCh middle = edge.pointAt(length/2);
        PointCh midPoint = new PointCh(SwissBounds.MIN_E + SwissBounds.WIDTH/2, SwissBounds.MIN_N + SwissBounds.HEIGHT/2);
        assertEquals(middle, midPoint);

        for (int i = 0; i < length; i+=10) {
            assertEquals(10.05, edge.elevationAt(i));
        }
    }

}