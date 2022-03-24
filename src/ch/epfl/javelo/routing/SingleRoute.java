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

    private final List<Edge> edges;
    private final double[] edgePositions;
    private final List<PointCh> points;
    double totalLength;

    /**
     * Constructs the single route composed of the given edges
     * @throws IllegalArgumentException
     * @param edges
     */
    public SingleRoute(List<Edge> edges){
        Preconditions.checkArgument(edges.size() > 0);
        this.edges = List.copyOf(edges);
        edgePositions = new double[edges.size() + 1];
        double position = 0;
        edgePositions[0] = 0;
        for (int i = 1; i < edges.size() + 1; i++) {
            position += edges.get(i-1).length();
            edgePositions[i] = position;
        }
        //create list of points
        points = new ArrayList<>();
        points.add(edges.get(0).fromPoint());
        for (Edge edge: edges){
            points.add(edge.toPoint());
        }
        //calculate length
        double length = 0;
        for(Edge edge:edges){
            length += edge.length();
        }
        this.totalLength = length;
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


    private Edge getEdgeAtPosition(double position){
        position = Math2.clamp(0, position, length());
        int found = Arrays.binarySearch(edgePositions, position);
        if (found >= 0){
            found = Math2.clamp(0, found, edges.size());
        } else {
            found = (found+2)*(-1);
        }
        return edges.get(found);
    }

    /**
     * @inheritDoc
     */
    @Override
    public PointCh pointAt(double position) {
        position = Math2.clamp(0, position, length());
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
        position = Math2.clamp(0, position, length());
        int found = Arrays.binarySearch(edgePositions, position);
        if (found >= 0){
            if (found == edges.size()) {
                Edge edge = edges.get(found-1);
                return edge.elevationAt(edge.length());
            }
            return edges.get(found).elevationAt(0);
        }
        else {
            found = -(found+2);
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
        position = Math2.clamp(0, position, length());
        int found = Arrays.binarySearch(edgePositions, position);
        if (found >= 0){
            if (found == edges.size()) {
                Edge edge = edges.get(found-1);
                return edge.toNodeId();
            }
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
        Edge edge = edges.get(0);
        double closestPosition = edge.positionClosestTo(point);
        PointCh closestPoint = edge.pointAt(closestPosition);
        double distanceToPoint = SwissBounds.WIDTH;
        RoutePoint closestRoutePoint = new RoutePoint(closestPoint, closestPosition, distanceToPoint);
        for (int i = 0; i < edges.size(); i++) {
            edge = edges.get(i);
            double closestPositionOnEdge = edge.positionClosestTo(point);
            PointCh closestPointOnEdge = edge.pointAt(closestPositionOnEdge);
            double distanceToPointOnEdge = Math2.norm(closestPointOnEdge.e() - point.e(), closestPointOnEdge.n() - point.n());
            closestRoutePoint = closestRoutePoint
                    .min(closestPointOnEdge,closestPositionOnEdge, distanceToPointOnEdge)
                    .withPositionShiftedBy(edgePositions[i]);
        }
        return new RoutePoint(closestPoint, closestPosition, distanceToPoint);
    }
}
