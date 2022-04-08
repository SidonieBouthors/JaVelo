package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import static java.lang.Double.NaN;
import static java.lang.Double.POSITIVE_INFINITY;

/**
 * RoutePoint
 *
 * @author Sidonie Bouthors (343678)
 * @author François Théron (346077)
 *
 * Builds RoutePoint with the given parameters
 * @param point                 : corresponding point
 * @param position              : position along it's Route
 * @param distanceToReference   : distance to reference point
 */
public record RoutePoint(PointCh point, double position, double distanceToReference) {

    /**
     * Represents a non-existent point
     */
    public static final RoutePoint NONE = new RoutePoint(null, NaN, POSITIVE_INFINITY);

    /**
     * Returns a RoutePoint identical to this but with a position shifted by the given difference
     * @param positionDifference    : shift in the position to apply
     * @return a RoutePoint with its position shifted
     */
    public RoutePoint withPositionShiftedBy(double positionDifference){
        return new RoutePoint(point, position + positionDifference, distanceToReference);
    }

    /**
     * Returns this if it's distance to reference is lesser or equal to the one of that
     * Otherwise, returns that
     * @param that  : another RoutePoint
     * @return the point with the least distance to reference
     */
    public RoutePoint min(RoutePoint that){
        return this.distanceToReference <= that.distanceToReference ? this : that;
    }

    /**
     * Returns this if it's distance to reference is lesser than or equal to thatDistanceToReference
     * Otherwise returns a new RoutePoint with given coordinates
     * @param thatPoint                 : parameter for new RoutePoint
     * @param thatPosition              : parameter for new RoutePoint
     * @param thatDistanceToReference   : distanceToReference compared to the one of this
     * @return the point with the least distance to reference between this and a potential that
     */
    public RoutePoint min(PointCh thatPoint, double thatPosition, double thatDistanceToReference){
        return this.distanceToReference <= thatDistanceToReference ?
                this : new RoutePoint(thatPoint, thatPosition, thatDistanceToReference) ;
    }
}
