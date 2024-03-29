package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

import java.util.DoubleSummaryStatistics;
import java.util.function.DoubleUnaryOperator;

/**
 * ElevationProfile
 *
 * @author Sidonie Bouthors (343678)
 * @author François Théron (346077)
 */
public final class ElevationProfile {

    private final double length;
    private final DoubleUnaryOperator elevationFunction;
    private  final DoubleSummaryStatistics sampleStats;
    private final double totalAscent;
    private final double totalDescent;



    /**
     * Builds the Elevation Profile  of an itinerary of length (in meters),
     * and of give samples
     * @throws IllegalArgumentException iff length <= 0 or if elevationSamples contains < 2 samples
     * @param length    : length of the itinerary
     * @param elevationSamples  : altitude samples along the itinerary
     */
    public ElevationProfile(double length, float[] elevationSamples){

        Preconditions.checkArgument(length > 0 && elevationSamples.length >= 2);

        this.length = length;
        this.elevationFunction = Functions.sampled(elevationSamples, length);


        //Create statistics from which to get min and max
        this.sampleStats = new DoubleSummaryStatistics();
        for (float sample:elevationSamples){
            sampleStats.accept(sample);
        }

        //Calculate ascent and descent (so they are only calculated once)
        double ascent = 0;
        double descent = 0;
        for (int i = 1; i < elevationSamples.length; i++) {
            double d = elevationSamples[i] - elevationSamples[i-1];
            if (d > 0){ascent += d;}
            else {descent -= d;}
        }
        this.totalAscent = ascent;
        this.totalDescent = descent;
    }

    /**
     * Returns length of ElevationProfile
     * @return length (in meters)
     */
    public double length(){
        return length;
    }

    /**
     * Returns minimum elevation of the profile
     * @return min elevation (in meters)
     */
    public double minElevation(){
        return sampleStats.getMin();
    }
    /**
     * Returns maximum elevation of the profile
     * @return max elevation (in meters)
     */
    public double maxElevation(){
        return sampleStats.getMax();
    }

    /**
     * Returns total positive elevation of the profile
     * @return total positive elevation (in meters)
     */
    public double totalAscent(){ return totalAscent; }
    /**
     * Returns total negative elevation of the profile
     * @return total negative elevation (in meters)
     */
    public double totalDescent(){ return totalDescent; }

    /**
     * Returns altitude of the profile at given position
     * @param position  : position on the itinerary
     * @return altitude at position
     */
    public double elevationAt(double position){
        return elevationFunction.applyAsDouble(position);
    }
}
