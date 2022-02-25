package ch.epfl.javelo.projection;

import ch.epfl.javelo.Preconditions;

/**
 * @author Sidonie Bouthors (343678)
 * @author François Théron (346077)
 */
public record PointWebMercator(double x, double y) {

    /**
     * PointWebMercator Constructor (Compact)
     * @throws IllegalArgumentException for x or y not in [0,1]
     * @param x     : x coordinate
     * @param y     : y coordinate
     */
    public PointWebMercator{
        Preconditions.checkArgument(0 <= x && x <= 1 && 0 <= y && y <= 1);
    }

    /**
     * Constructs a PointWebMercator with specified zoom level
     * @param zoomLevel     : level of zoom
     * @param x             : x coordinate
     * @param y             : y coordinate
     * @return PointWebMercator with specified coordinates and zoom level
     */
    public static PointWebMercator of(int zoomLevel, double x, double y){
        return new PointWebMercator(Math.scalb(x, zoomLevel), Math.scalb(y, zoomLevel));
    }
    public static PointWebMercator ofPointCh(PointCh pointCh){
        double x = WebMercator.x(Ch1903.lon(pointCh.e(), pointCh.n()));
        double y = WebMercator.x(Ch1903.lat(pointCh.e(), pointCh.n()));
        return new PointWebMercator(x, y);
    }

    /**
     * Returns the coordinates of x with zoom level applied
     * @param zoomLevel     : level of zoom
     * @return x coordinate with zoom applied
     */
    public double xAtZoomLevel(int zoomLevel){
        return Math.scalb(x, zoomLevel);
    }

    /**
     * Returns the coordinates of y with zoom level applied
     * @param zoomLevel     : level of zoom
     * @return y coordinate with zoom applied
     */
    public double yAtZoomLevel(int zoomLevel){
        return Math.scalb(y, zoomLevel);
    }

    /**
     * Returns longitude of the PointWebMercator
     * @return longitude
     */
    public double lon(){
        return WebMercator.lon(x);
    }

    /**
     * Returns latitude of the PointWebMercator
     * @return latitude
     */
    public double lat(){
        return WebMercator.lat(y);
    }

    /**
     * Returns PointCh equivalent of the PointWebMercator
     * Returns null if it is not in Switzerland
     * @return PointCh equivalent of this
     */
    public PointCh toPointCh(){
        double e = Ch1903.e(lon(), lat());
        double n = Ch1903.n(lon(), lat());
        if (SwissBounds.containsEN(e, n)) {
            return new PointCh(e,n);
        } else { return null; }
    }
}
