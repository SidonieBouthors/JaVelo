package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import static java.lang.Double.NaN;
import static java.lang.Double.POSITIVE_INFINITY;

public record RoutePoint(PointCh point, double position, double distanceToReference) {
    /**
     * Constant representing a non-existent point
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
        if (this.distanceToReference <= that.distanceToReference){
            return this;
        }
        else {
            return that;
        }
    }

    /**
     * Returns this if it's distance to reference is lesser than or equal to thatDistanceToReference
     * Otherwise returns a new RoutePoint with given coordinates
     * @param thatPoint                 : parameter for new RoutePoint
     * @param thatPosition              : parameter for new RoutePoint
     * @param thatDistanceToReference   : distanceToReference compared to the one of this
     * @return the point with the least distance to reference between this and a potential that with given parameters
     */
    public RoutePoint min(PointCh thatPoint, double thatPosition, double thatDistanceToReference){
        if (this.distanceToReference <= thatDistanceToReference){
            return this;
        }
        else {
            return new RoutePoint(thatPoint, thatPosition, thatDistanceToReference);
        }
    }
}
