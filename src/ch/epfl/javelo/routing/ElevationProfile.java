package ch.epfl.javelo.routing;

public class ElevationProfile {
    public ElevationProfile(double length, float[] elevationSamples){
        //construit profil en long d'un itinéraire de longueur length
        //et dont les echantillons d'altitude sont elevationSamples
        //IllegalArgumentException si la longueur est negative/nulle, ou si il y a moins de 2 samples
    }

    public double length(){

        return 0;
    }
    public double minElevation(){

        return 0;
    }
    public double maxElevation(){

        return 0;
    }
    public double totalAscent(){

        return 0;
    }
    public double totalDescent(){

        return 0;
    }
    public double elevationAt(double position){

        return position;
    }
}
