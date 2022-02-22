package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

public record PointCh(double e, double n) {


    public PointCh{
        Preconditions.checkArgument(SwissBounds.containsEN(e,n));
    }

    public double squaredDistanceTo(PointCh that){
        return  Math2.squaredNorm(e - that.e, n - that.n);

    }

    public double distanceTo(PointCh that){
        return Math2.norm(e - that.e, n - that.n);
    }

    public double lon(){
        return Ch1903.lon(e,n);
    }

    public double lat(){
        return Ch1903.lat(e,n);
    }
}
