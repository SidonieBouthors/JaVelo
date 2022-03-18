package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Sidonie Bouthors (343678)
 * @author François Théron (346077)
 */
public final class SingleRoute implements Route{

    public final List<Edge> edges;
    public final double[] edgePositions;
    public final List<PointCh> points;

    /**
     * Constructs the single route composed of the given edges
     * @throws IllegalArgumentException
     * @param edges
     */
    public SingleRoute(List<Edge> edges){
        Preconditions.checkArgument(edges.size() > 0);
        this.edges = List.copyOf(edges);
        edgePositions = new double[edges.size() + 1];
        int position = 0;
        edgePositions[0] = 0;
        for (int i = 0; i < edges.size(); i++) {
            position += edges.get(i).length();
            edgePositions[i] = position;
        }
        points = new ArrayList<>();
        points.add(edges.get(0).fromPoint());
        for (Edge edge: edges){
            points.add(edge.toPoint());
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public int indexOfSegmentAt(double position) { return 0; }

    /**
     * @inheritDoc
     */
    @Override
    public double length() {
        int totalLength=0;
        for(Edge edge:edges){
            totalLength += edge.length();
        }
        return totalLength;
    }

    /**
     * @inheritDoc
     */
    @Override
    public List<Edge> edges() {
        return Collections.unmodifiableList(edges);
    }

    /**
     * @inheritDoc
     */
    @Override
    public List<PointCh> points() {
        return Collections.unmodifiableList(points); //CHECK
    }

    /**
     * @inheritDoc
     */
    @Override
    public PointCh pointAt(double position) {
        Math2.clamp(0, position, length());
        int found = Arrays.binarySearch(edgePositions, position);
        if (found >= 0){
            return points.get(found);
        }
        else {
            found = (found+2)*(-1);
            Edge edge = edges.get(found);
            double positionOnEdge = position - edgePositions[found];
            return edge.pointAt(positionOnEdge);
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public double elevationAt(double position) {
        Math2.clamp(0, position, length());
        int found = Arrays.binarySearch(edgePositions, position);
        if (found >= 0){
            return edges.get(found).elevationAt(0);
        }
        else {
            found = (found+2)*(-1);
            Edge edge = edges.get(found);
            double positionOnEdge = position - edgePositions[found];
            return edge.elevationAt(positionOnEdge);
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public int nodeClosestTo(double position) {
        Math2.clamp(0, position, length());
        int found = Arrays.binarySearch(edgePositions, position);
        if (found >= 0){
            return edges.get(found).fromNodeId();
        }
        else {
            found = (found+2)*(-1);
            Edge edge = edges.get(found);
            double positionOnEdge = position - edgePositions[found];
            if(positionOnEdge < edge.length()/2){
                return edge.fromNodeId();
            }
            else {
                return edge.toNodeId();
            }
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        double closestPosition = edges.get(0).positionClosestTo(point);
        PointCh closestPoint = edges.get(0).pointAt(closestPosition);
        double distanceToPoint = SwissBounds.WIDTH;
        for(Edge edge:edges){
            double closestPositionOnEdge = edge.positionClosestTo(point);
            PointCh closestPointOnEdge = edge.pointAt(closestPositionOnEdge);
            double distanceToPointOnEdge = Math2.norm(closestPointOnEdge.e() - point.e(), closestPointOnEdge.n() - point.n());
            if (distanceToPointOnEdge < distanceToPoint){
                distanceToPoint = distanceToPointOnEdge;
                closestPoint = closestPointOnEdge;
                closestPosition = closestPositionOnEdge;
            }
        }
        return new RoutePoint(closestPoint, closestPosition, distanceToPoint);
    }
}
