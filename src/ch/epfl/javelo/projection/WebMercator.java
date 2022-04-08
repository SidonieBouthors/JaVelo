package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;

/**
 * WebMercator
 *
 * @author Sidonie Bouthors (343678)
 * @author François Théron (346077)
 */
public final class WebMercator {

    private WebMercator (){}

    /**
     * Returns x coordinate of a point of longitude lon
     * @param lon   : longitude
     * @return x coordinate
     */
    public static double x(double lon){
        return (lon + Math.PI)/(2 * Math.PI);
    }

    /**
     * Returns y coordinate of a point of latitude lat
     * @param lat   : latitude
     * @return y coordinate
     */
    public static double y(double lat){
        return (Math.PI - Math2.asinh(Math.tan(lat)))/(2*Math.PI);
    }

    /**
     * Returns longitude (in radians) of a point of coordinate x
     * @param x : x coordinate
     * @return longitude
     */
    public static double lon(double x){
        return 2 * Math.PI * x - Math.PI;
    }

    /**
     * Returns latitude (in radians) of a point of coordinate y
     * @param y : y coordinate
     * @return longitude
     */
    public static double lat(double y){
        return Math.atan(Math.sinh(Math.PI - 2*Math.PI*y));
    }
}
