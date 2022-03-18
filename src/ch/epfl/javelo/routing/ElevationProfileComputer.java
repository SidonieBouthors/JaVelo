package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

import java.util.Arrays;

/**
 * @author Sidonie Bouthors (343678)
 * @author François Théron (346077)
 */
public final class ElevationProfileComputer {

    private ElevationProfileComputer() {}

    /**
     * Returns the profile of the route, guarantying that the spacing between samples is at most maxStepLength
     * @throws IllegalArgumentException if the maximum spacing is <= 0
     * @param route             : the route
     * @param maxStepLength     : maximum spacing between samples
     * @return the profile of the route
     * @throws IllegalArgumentException if the maximum spacing is <= 0
     */
    public static ElevationProfile elevationProfile(Route route, double maxStepLength) {

        Preconditions.checkArgument(maxStepLength > 0);

        double routeLength = route.length();
        int nbOfSamples = (int) (Math.ceil(routeLength / maxStepLength) + 1);
        double stepLength = routeLength / (nbOfSamples - 1);
        // Creating Original Samples
        float[] samples = new float[nbOfSamples];
        double position = stepLength;
        for (float sample:samples){
            sample = (float) route.elevationAt(position);
            position += stepLength;
        }

        //Filling in NaN values at start of array
        int i = 0;
        while (i < nbOfSamples && Float.isNaN(samples[i])) { i++; }
        if (i == nbOfSamples) {
            Arrays.fill(samples, 0);
            return new ElevationProfile(routeLength, samples);
        }
        Arrays.fill(samples, 0, i, samples[i]);

        //Filling in NaN values at end of array
        int j = nbOfSamples - 1;
        while (0 <= j && Float.isNaN(samples[j])) { j--; }
        Arrays.fill(samples, j, nbOfSamples, samples[j]);

        int firstNaNIndex = -1;
        int nextCorrectValueIndex;
        boolean findNext = false;
        for (int k = 0; k < nbOfSamples; k++) {
            if (Float.isNaN(samples[k])) {
                firstNaNIndex = k;
                findNext = true;
            } else if (findNext) {
                nextCorrectValueIndex = k;
                double step = 1 / (double) (nextCorrectValueIndex - firstNaNIndex);
                double x = step;
                for (int l = firstNaNIndex; l < nextCorrectValueIndex; l++) {
                    samples[l] = (float) Math2.interpolate(samples[firstNaNIndex - 1], samples[nextCorrectValueIndex], x);
                    x += step;
                }
            }
        }
        return new ElevationProfile(routeLength, samples);

        /*
        int firstSampleNotNaNFromRight = -1;
        int firstSampleNotNaNFromLeft = firstSampleNotNaNFromRight;
        for (int i = 0; i < nbOfSamples; i++) {
            if (firstSampleNotNaNFromRight == -1 && !Float.isNaN(samples[i])) {
                firstSampleNotNaNFromRight = i;
            }
            if (firstSampleNotNaNFromRight != -1 && Float.isNaN(samples[i])) {
                samples[i] = (float) Math2.interpolate(route.elevationAt(firstSampleNotNaNFromRight * stepLength), route.elevationAt(routeLength), i * stepLength);
            }
        }
        for (int i = 0; i < nbOfSamples; i++) {

            if (firstSampleNotNaNFromLeft == -1 && !Float.isNaN(samples[nbOfSamples - i - 1])) {
                firstSampleNotNaNFromLeft = nbOfSamples - i - 1;
            }
            if (firstSampleNotNaNFromLeft != -1 && Float.isNaN(samples[nbOfSamples - i - 1])) {
                samples[i] = (float) Math2.interpolate(route.elevationAt(0), route.elevationAt(firstSampleNotNaNFromLeft * stepLength), i * stepLength);
            }
        }
         */


    }
}


