package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.function.DoubleUnaryOperator;

public record Edge(int fromNodeId, int toNodeId, PointCh fromPoint, PointCh toPoint, double length, DoubleUnaryOperator profile) {
    /**
     * Builds an Edge using edge of given edgeId in graph to obtain last three prameters
     * @param graph         : graph that the edge is a part of
     * @param edgeId        : ID of the edge in the graph
     * @param fromNodeId    : ID of starting node of the edge
     * @param toNodeId      : ID of ending node of the edge
     * @return an Edge using the given parameters
     */
    public static Edge of(Graph graph, int edgeId, int fromNodeId, int toNodeId){
        PointCh edgeFromPoint = graph.nodePoint(fromNodeId);
        PointCh edgeToPoint = graph.nodePoint(toNodeId);
        double edgeLength = graph.edgeLength(edgeId);
        DoubleUnaryOperator edgeProfile = graph.edgeProfile(edgeId);
        return new Edge(fromNodeId, toNodeId, edgeFromPoint, edgeToPoint, edgeLength, edgeProfile);
    }

    public double positionClosestTo(PointCh point){
        return Math2.projectionLength(point.e(), point.n(), fromPoint.e(), fromPoint.n(), toPoint.e(), toPoint.n());
    }
    public PointCh pointAt(double position){
        return null;
    }
    public double elevationAt(double position){

        return position;
    }
}
