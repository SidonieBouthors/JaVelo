package ch.epfl.javelo.routing;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.function.DoubleUnaryOperator;

public record Edge(int fromNodeId, int toNodeId, PointCh fromPoint, PointCh toPoint, double length, DoubleUnaryOperator profile) {
    public static Edge of(Graph graph, int edgeId, int fromNodeId, int toNodeId){
        //TO DO
        return null;
    }

    public double positionClosestTo(PointCh point){

        return 0;
    }
    public PointCh pointAt(double position){

        return null;
    }
    public double elevationAt(double position){

        return position;
    }
}
