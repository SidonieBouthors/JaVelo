package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

public record PointCh(double e, double n) {

    /**
     * Constructs a point in Switzerland with east and north coordinates
     * @throws IllegalArgumentException if coordinates are not in Swiss bounds
     * @param e : east coordinate
     * @param n : north coordinate
     */
    public PointCh{
        Preconditions.checkArgument(SwissBounds.containsEN(e,n));
    }

    /**
     * Returns square of distance (in meters)
     * between this and that
     * @param that  : other point in Switzerland
     * @return squared distance between the two points
     */
    public double squaredDistanceTo(PointCh that){
        return  Math2.squaredNorm(e - that.e, n - that.n);
    }

    /**
     * Returns distance (in m)
     * between this and that
     * @param that  : other point in Switzerland
     * @return distance between the two points
     */
    public double distanceTo(PointCh that){
        return Math2.norm(e - that.e, n - that.n);
    }

    /**
     * Returns longitude of the point in the WGS84 system
     * @return longitude in radians
     */
    public double lon(){
        return Ch1903.lon(e,n);
    }

    /**
     * Returns latitude of the point in the WGS84 system
     * @return latitude in radians
     */
    public double lat(){
        return Ch1903.lat(e,n);
    }
}
