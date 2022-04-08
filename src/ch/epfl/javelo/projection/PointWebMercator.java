package ch.epfl.javelo.projection;

import ch.epfl.javelo.Preconditions;

/**
 * PointWebMercator
 *
 * @author Sidonie Bouthors (343678)
 * @author François Théron (346077)
 *
 * PointWebMercator with the given coordinates
 * @param x     : x coordinate
 * @param y     : y coordinate
 */
public record PointWebMercator(double x, double y) {
    /**
     * z such that 2^z is the size in pixels of the whole world map at level of zoom 0 (256)
     */
    public static final int ZOOM_ZERO = 8;

    /**
     * PointWebMercator Constructor (Compact)
     * @throws IllegalArgumentException for x or y not in [0,1]
     * @param x     : x coordinate
     * @param y     : y coordinate
     */
    public PointWebMercator{
        Preconditions.checkArgument(0 <= x && x <= 1
                                            && 0 <= y && y <= 1);
    }

    /**
     * Constructs a PointWebMercator whose coordinates are x and y at specified zoom level
     * @param zoomLevel     : level of zoom
     * @param x             : x coordinate
     * @param y             : y coordinate
     * @return PointWebMercator with specified coordinates at zoom level
     */
    public static PointWebMercator of(int zoomLevel, double x, double y){
        return new PointWebMercator(Math.scalb(x, -(ZOOM_ZERO+zoomLevel)),
                                    Math.scalb(y, -(ZOOM_ZERO+zoomLevel)));
    }

    /**
     * Returns a PointWebMercator that corresponds to the givenPointCh
     * @param pointCh   : given PointCh
     * @return PointWebMercator corresponding to the PointCh
     */
    public static PointWebMercator ofPointCh(PointCh pointCh){

        double x = WebMercator.x(Ch1903.lon(pointCh.e(), pointCh.n()));
        double y = WebMercator.y(Ch1903.lat(pointCh.e(), pointCh.n()));

        return new PointWebMercator(x, y);
    }

    /**
     * Returns the x coordinates with zoom level applied
     * @param zoomLevel     : level of zoom
     * @return x coordinate with zoom applied
     */
    public double xAtZoomLevel(int zoomLevel){
        return Math.scalb(x, ZOOM_ZERO+zoomLevel);
    }

    /**
     * Returns the y coordinates with zoom level applied
     * @param zoomLevel     : level of zoom
     * @return y coordinate with zoom applied
     */
    public double yAtZoomLevel(int zoomLevel){
        return Math.scalb(y, ZOOM_ZERO+zoomLevel);
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

        return SwissBounds.containsEN(e, n)? new PointCh(e,n) :  null;
    }
}
