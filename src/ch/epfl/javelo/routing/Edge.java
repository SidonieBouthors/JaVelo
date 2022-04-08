package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.function.DoubleUnaryOperator;

/**
 * Edge
 *
 * @author Sidonie Bouthors (343678)
 * @author François Théron (346077)
 *
 * Edge with the given parameters
 * @param fromNodeId    : ID of starting node of the edge
 * @param toNodeId      : ID of ending node of the edge
 * @param fromPoint     : starting point of the edge
 * @param toPoint       : ending point of the edge
 * @param length        : length of the edge
 * @param profile       : elevation profile of the edge
 */
public record Edge(int fromNodeId, int toNodeId, PointCh fromPoint,
                   PointCh toPoint, double length, DoubleUnaryOperator profile) {
    /**
     * Builds an Edge using edge of given edgeId in graph to obtain last three parameters
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

        return new Edge(fromNodeId, toNodeId, edgeFromPoint,
                        edgeToPoint, edgeLength, edgeProfile);
    }

    /**
     * Returns the position along the edge that is closest to the given point
     * @param point : point
     * @return position on edge closest to the point
     */
    public double positionClosestTo(PointCh point){

       return Math2.projectionLength(
               fromPoint.e(), fromPoint.n(),
               toPoint.e(), toPoint.n(),
               point.e(), point.n());

    }

    /**
     * Returns the point situated at a given position on the edge
     * @param position  : position on the edge
     * @return point situated at given position on the edge
     */
    public PointCh pointAt(double position){
        Math2.clamp(0, position, this.length);

        double factor = length / position;
        double e = (toPoint.e() - fromPoint.e())/factor + fromPoint.e();
        double n = (toPoint.n() - fromPoint.n())/factor + fromPoint.n();

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
