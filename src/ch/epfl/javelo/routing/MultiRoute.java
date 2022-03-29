package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Sidonie Bouthors (343678)
 * @author François Théron (346077)
 */

public class MultiRoute implements Route{
    private final List<Route> segments;
    private final double length;
    private final List<Edge> edges;
    private double[] segmentPositions;

    public MultiRoute(List<Route> segments){
        Preconditions.checkArgument(!segments.isEmpty());

        this.segments= List.copyOf(segments);

        //calculate total length
        double totalLength = 0;
        for (Route route : segments) {
            totalLength+=route.length();
        }
        this.length = totalLength;

        //create edge list
        List<Edge> totalEdges = new ArrayList<>();
        for (Route route : segments) {
            totalEdges.addAll(route.edges());
        }
        this.edges = totalEdges;

        //calculate segment positions
        segmentPositions = new double[this.segments.size() + 1];
        double position = 0;
        segmentPositions[0] = 0;
        for (int i = 1; i < this.segments.size() + 1; i++) {
            position += this.segments.get(i-1).length();
            segmentPositions[i] = position;
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public int indexOfSegmentAt(double position){
        Math2.clamp(0, position, length);
        int previousIndexes = 0;
        int i = 0;
        Route segment = segments.get(0);
        while (position > segmentPositions[i+1]) {
            System.out.println(segmentPositions[i+1]);
            segment = segments.get(i);
            previousIndexes += segment.indexOfSegmentAt(segment.length()) + 1;
            i++;
        }
        System.out.println(i);
        System.out.println("prev : " + previousIndexes );
        System.out.println("pos : " + position + " - " +  segmentPositions[i]);
        System.out.println("segment indx : " + segment.indexOfSegmentAt(position - segmentPositions[i]));
        return previousIndexes + segment.indexOfSegmentAt(position - segmentPositions[i]);
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
        return edges;
    }

    /**
     * @inheritDoc
     */
    @Override
    public List<PointCh> points(){
        List<PointCh> output = new ArrayList<>();
        PointCh endPointFromBefore = null;
        for (Edge edges : edges()) {

            if (edges.fromPoint() != endPointFromBefore ){
                output.add(edges.fromPoint());
            }

            output.add(edges.toPoint());
            endPointFromBefore = edges.toPoint();

        }
        return output;
    }

    /**
     * @inheritDoc
     */
    @Override
    public PointCh pointAt(double position){
        Math2.clamp(0,position,length);
        int i = indexOfSegmentAt(position);
        double actualLength =segmentPositions[i];
        return segments.get(i).pointAt(position-actualLength);
    }

    /**
     * @inheritDoc
     */
    @Override
    public double elevationAt(double position){
        Math2.clamp(0,position,length);
        int i= indexOfSegmentAt(position);
        double actualLength = segmentPositions[i];
        return segments.get(i).elevationAt(position-actualLength);
    }

    /**
     * @inheritDoc
     */
    @Override
    public int nodeClosestTo(double position){
        Math2.clamp(0,position,length);
        int i = indexOfSegmentAt(position);
        double actualLength = segmentPositions[i];
        return segments.get(i).nodeClosestTo(position-actualLength);
    }
    /**
     * @inheritDoc
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point){

        RoutePoint routepoint = RoutePoint.NONE;
        double closestPosition, distanceToRef, actualPosition=0;

        for (int i = 0; i < edges.size(); ++i) {
            Edge edge = edges.get(i);

            //Counting the position on the multi route to use it in routepoint
            actualPosition+=edge.length();

            closestPosition = Math2.clamp(0, edge.positionClosestTo(point), edge.length());

            PointCh pointclosest = edge.pointAt(closestPosition);

            distanceToRef = Math2.norm(pointclosest.e()-point.e(),pointclosest.n()-point.n());

            routepoint = routepoint.min(new RoutePoint(pointclosest,closestPosition,distanceToRef+
                    actualPosition));
        }
        return routepoint;
    }
}
