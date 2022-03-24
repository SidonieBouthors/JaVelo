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
        //System.out.println("nb of samples: " + nbOfSamples);
        double stepLength = routeLength / (nbOfSamples - 1);
        System.out.println("steplength : " + stepLength);
        //// Creating Original Samples
        float[] samples = new float[nbOfSamples];
        double position = 0;
        for (int i = 0; i < nbOfSamples; i++) {
            //System.out.println("samples ["+i+"] : " + route.elevationAt(position));
            samples[i] = (float) route.elevationAt(position);
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
        int nextCorrectIndex;
        boolean findNext = false;
        for (int k = 0; k < nbOfSamples; k++) {
            if (findNext && !Float.isNaN(samples[k])) {
                nextCorrectIndex = k;
                double step = 1 / (double) (nextCorrectIndex - firstNaNIndex + 1);
                double x = step;
                for (int l = firstNaNIndex; l < nextCorrectIndex; l++) {
                    samples[l] = (float) Math2.interpolate(samples[firstNaNIndex - 1], samples[nextCorrectIndex], x);
                    System.out.println("y0 : "+ samples[firstNaNIndex-1] + " y1 : "+samples[nextCorrectIndex] + " x : " + x);
                    x += step;
                }
                System.out.println("x : " + x);
                findNext = false;
            }
            else if (!findNext && Float.isNaN(samples[k])) {
                firstNaNIndex = k;
                findNext = true;
            }
        }

        System.out.println("\n final samples");
        for(float sample:samples){
            System.out.println(sample);
        }

        return new ElevationProfile(routeLength, samples);

    }
}


