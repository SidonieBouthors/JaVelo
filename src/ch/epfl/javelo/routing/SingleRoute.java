package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * SingleRoute
 *
 * @author Sidonie Bouthors (343678)
 * @author François Théron (346077)
 */
public final class SingleRoute implements Route{

    private final List<Edge> edges;
    private final double[] edgePositions;
    private final List<PointCh> points;
    private final double totalLength;

    /**
     * Constructs the single route composed of the given edges
     * @throws IllegalArgumentException if edge list is empty
     * @param edges : list of edges of the single route
     */
    public SingleRoute(List<Edge> edges){

        Preconditions.checkArgument(!edges.isEmpty());

        this.edges = List.copyOf(edges);

        //create list of edge positions
        this.edgePositions = new double[edges.size() + 1];
        this.edgePositions[0] = 0;
        double position = 0;
        for (int i = 1; i < edges.size() + 1; i++) {
            position += edges.get(i-1).length();
            this.edgePositions[i] = position;
        }
        //create list of points
        this.points = new ArrayList<>();
        this.points.add(edges.get(0).fromPoint());
        for (Edge edge: edges){
            this.points.add(edge.toPoint());
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
    public double length() { return totalLength; }

    /**
     * @inheritDoc
     */
    @Override
    public List<Edge> edges() {
        return List.copyOf(edges);
    }

    /**
     * @inheritDoc
     */
    @Override
    public List<PointCh> points() { return List.copyOf(points); }

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
            found = -(found+2);
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
            else {
                return edges.get(found).elevationAt(0);
            }
        }
        else {
            found = -(found + 2);
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
            else {
                return edges.get(found).fromNodeId();
            }
        }
        else {
            found = -(found + 2);
            Edge edge = edges.get(found);
            double positionOnEdge = position - edgePositions[found];

            return positionOnEdge < edge.length() / 2 ?
                    edge.fromNodeId() : edge.toNodeId();
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {

        Edge edge;
        double closestPosition;
        PointCh closestPoint;
        double distanceToPoint;
        RoutePoint closestRoutePoint = RoutePoint.NONE;

        for (int i = 0; i < edges.size(); i++) {
            edge = edges.get(i);
            closestPosition = Math2.clamp(0, edge.positionClosestTo(point), edge.length());
            closestPoint = edge.pointAt(closestPosition);
            distanceToPoint = Math2.norm(closestPoint.e() - point.e(), closestPoint.n() - point.n());
            closestRoutePoint = closestRoutePoint.min(closestPoint,closestPosition + edgePositions[i], distanceToPoint);
        }

        return closestRoutePoint;
    }
}
