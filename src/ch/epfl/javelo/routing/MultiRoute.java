package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.List;

/**
 * MultiRoute
 *
 * @author Sidonie Bouthors (343678)
 * @author François Théron (346077)
 */

public final class MultiRoute implements Route{

    private final List<Route> segments;
    private final double length;
    private final List<Edge> edges;
    private final double[] segmentPositions;

    /**
     * Builds a MultiRoute with the given segments
     * @param segments  : segments of the MultiRoute
     */
    public MultiRoute(List<Route> segments){

        Preconditions.checkArgument(!segments.isEmpty());

        this.segments= List.copyOf(segments);

        //calculate segment positions, edge list and length of route altogether

        List<Edge> totalEdges = new ArrayList<>();
        segmentPositions = new double[segments.size() + 1];
        segmentPositions[0] = 0;
        double position = 0;

        for (int i = 0; i < segments.size(); i++) {
            Route route = segments.get(i);
            position += route.length();
            segmentPositions[i+1] = position;
            totalEdges.addAll(route.edges());
        }

        this.length = position;
        this.edges = totalEdges;
    }

    /**
     * @inheritDoc
     */
    @Override
    public int indexOfSegmentAt(double position){
        position = Math2.clamp(0, position, length);
        int previousIndexes = 0;
        int i = 0;
        Route segment = segments.get(0);
        while (position > segmentPositions[i+1]) {
            segment = segments.get(i);
            previousIndexes += segment.indexOfSegmentAt(segment.length()) + 1;
            i++;
        }
        return previousIndexes +
                segment.indexOfSegmentAt(position - segmentPositions[i]);
    }

    /**
     * @inheritDoc
     */
    @Override
    public double length(){
        return length;
    }

    /**
     * @inheritDoc
     */
    @Override
    public List<Edge> edges(){
        return List.copyOf(edges);
    }

    /**
     * @inheritDoc
     */
    @Override
    public List<PointCh> points(){
        List<PointCh> points = new ArrayList<>();
        Route segment;
        for (int i = 0; i < segments.size() - 1; i++) {
            segment = segments.get(i);
            points.addAll(segment.points());
            points.remove(points.size() - 1);
        }
        segment = segments.get(segments.size() - 1);
        points.addAll(segment.points());
        return points;
    }

    /**
     * @inheritDoc
     */
    @Override
    public PointCh pointAt(double position){
        position = Math2.clamp(0, position, length);
        int i =0;
        while (position > segmentPositions[i+1]){ i++; }
        return segments.get(i).pointAt(position - segmentPositions[i]);
    }

    /**
     * @inheritDoc
     */
    @Override
    public double elevationAt(double position){
        position = Math2.clamp(0, position, length);
        int i = 0;
        while (position > segmentPositions[i+1]){ i++; }
        return segments.get(i).elevationAt(position - segmentPositions[i]);
    }

    /**
     * @inheritDoc
     */
    @Override
    public int nodeClosestTo(double position){
        position = Math2.clamp(0, position, length);
        int i = 0;
        while (position > segmentPositions[i+1]){ i++; }
        return segments.get(i).nodeClosestTo(position - segmentPositions[i]);
    }

    /**
     * @inheritDoc
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point){
        Route segment;
        RoutePoint closestRoutePoint = RoutePoint.NONE;
        for (int i = 0; i < segments.size(); ++i) {
            segment = segments.get(i);
            closestRoutePoint = closestRoutePoint
                                    .min(segment.pointClosestTo(point)
                                                .withPositionShiftedBy(segmentPositions[i]));
        }
        return closestRoutePoint;
    }
}
