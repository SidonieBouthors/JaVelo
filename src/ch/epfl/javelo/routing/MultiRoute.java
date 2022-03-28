package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sidonie Bouthors (343678)
 * @author François Théron (346077)
 */

public class MultiRoute implements Route{
    private List<Route> segments;

    public MultiRoute(List<Route> segments){
        Preconditions.checkArgument(!segments.isEmpty());
        this.segments=segments;

    }

    /**
     * @inheritDoc
     */
    @Override
    public int indexOfSegmentAt(double position){

        double actualLength = segments.get(0).length();
        int i =1;
        while(position > actualLength) {
            actualLength += segments.get(i).length();
            i++;
        }
        return i;

    }

    /**
     * @inheritDoc
     */
    @Override
    public double length(){
        double length =0;
        for (Route route : segments) {
            length+=route.length();
        }
        return length;
    }

    /**
     * @inheritDoc
     */
    @Override
    public List<Edge> edges(){
        List<Edge> output = new ArrayList<>();
        for (Route route : segments) {
            output.addAll(route.edges());
        }
        return output;
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
        int i = indexOfSegmentAt(position);
        double actualLength =0;
        for (int j = 0; j < i; j++) {
            segments.get(j);
        }
        return null;
    }

    /**
     * @inheritDoc
     */
    @Override
    public double elevationAt(double position){
        return 0;
    }

    /**
     * @inheritDoc
     */
    @Override
    public int nodeClosestTo(double position){
        return 0;
    }

    /**
     * @inheritDoc
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point){
        return null;
    }
}
