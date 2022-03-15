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

    /**
     * Returns the position along the edge that is closest to the given point
     * @param point : point
     * @return position on edge closest to the point
     */
    public double positionClosestTo(PointCh point){
        return Math2.projectionLength(point.e(), point.n(), fromPoint.e(), fromPoint.n(), toPoint.e(), toPoint.n());
    }

    /**
     * Returns the point situated at a given position on the edge
     * @param position  : position on the edge
     * @return point situated at given position on the edge
     */
    public PointCh pointAt(double position){
        double factor = length / position;
        double e = Math.abs(fromPoint.e() - toPoint.e())/factor + Math.min(fromPoint.e(), toPoint.e());
        double n = Math.abs(fromPoint.n() - toPoint.n())/factor + Math.min(fromPoint.n(), toPoint.n());
        return new PointCh(e, n);
    }

    /**
     * Returns the altitude of the given position on the edge
     * @param position  : position on the edge
     * @return altitude at given position
     */
    public double elevationAt(double position){
        return profile.applyAsDouble(position);
    }
}
