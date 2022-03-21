package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.function.DoubleUnaryOperator;

public class ElevationProfile {

    private final double length;
    private final float[] elevationSamples;
    private  final DoubleSummaryStatistics sampleStats;
    private final double totalAscent;
    private final double totalDescent;

    /**
     * Builds the Elevation Profile  of an itinerary of length (in meters),
     * and of give samples
     * @throws IllegalArgumentException iff length <= 0 or if elevationSamples contains less than 2 samples
     * @param length    : length of the itinerary
     * @param elevationSamples  : altitude samples along the itinerary
     */
    public ElevationProfile(double length, float[] elevationSamples){
        Preconditions.checkArgument(length > 0 && elevationSamples.length >= 2);
        this.length = length;
        this.elevationSamples = elevationSamples.clone();

        //Create statistics from which to get min and max
        this.sampleStats = new DoubleSummaryStatistics();
        for (float sample:elevationSamples){
            sampleStats.accept(sample);
        }

        //Calculate ascent and descent (so they are only calculated once)
        double d = 0; double ascent = 0; double descent = 0;
        for (int i = 1; i < elevationSamples.length; i++) {
            d = elevationSamples[i] - elevationSamples[i-1];
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
        Math2.clamp(0,position,length);
        DoubleUnaryOperator elevationFunction = Functions.sampled(elevationSamples, length);
        return elevationFunction.applyAsDouble(position);
    }
}
