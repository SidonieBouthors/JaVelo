package ch.epfl.javelo.projection;

import ch.epfl.javelo.Preconditions;

public record PointWebMercator(double x, double y) {
    public PointWebMercator{
        Preconditions.checkArgument(0 < x && x < 1 && 0 < y && y < 1);
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

    PointWebMercator ofPointCh(PointCh pointCh){

        return null;
    }

    public double xAtZoomLevel(int zoomLevel){
        return Math.scalb(x, zoomLevel);
    }
    public double yAtZoomLevel(int zoomLevel){
        return Math.scalb(y, zoomLevel);
    }
    public double lon(){
        return WebMercator.lon(x);
    }
    public double lat(){
        return WebMercator.lat(y);
    }
    public PointCh toPointCh(){
        double e = Ch1903.e(lon(), lat());
        double n = Ch1903.n(lon(), lat());
        if (SwissBounds.containsEN(e, n)) {
            return new PointCh(e,n);
        } else { return null; }
    }
}
