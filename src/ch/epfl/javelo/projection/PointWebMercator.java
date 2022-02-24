package ch.epfl.javelo.projection;

import ch.epfl.javelo.Preconditions;

public record PointWebMercator(double x, double y) {
    public PointWebMercator{
        Preconditions.checkArgument(0 < x && x < 1 && 0 < y && y < 1);
    }
    PointWebMercator of(int zoomLevel, double x, double y){

        return null;
    }

    PointWebMercator ofPointCh(PointCh pointCh){

        return null;
    }

    public double xAtZoomLevel(int zoomLevel){

        return 0;
    }
    public double yAtZoomLevel(int zoomLevel){

        return 0;
    }
    public double lon(){

        return 0;
    }
    public double lat(){

        return 0;
    }
    public PointCh toPointCh(){

        return null;
    }
}
