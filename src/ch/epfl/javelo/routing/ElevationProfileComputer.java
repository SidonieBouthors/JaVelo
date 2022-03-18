package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

/**
 * @author Sidonie Bouthors (343678)
 * @author François Théron (346077)
 */
public final class ElevationProfileComputer {

    private ElevationProfileComputer() {}

    /**
     * Returns the profile of the route, garantying that the spacing between samples is at most maxStepLength
     * @throws IllegalArgumentException if the maximum spacing is <= 0
     * @param route             : the route
     * @param maxStepLength     : maximum spacing between samples
     * @return the profile of the route
     */
    public static ElevationProfile elevationProfile(Route route, double maxStepLength) {
        Preconditions.checkArgument(maxStepLength>0);

        double routeLength = route.length();
        int nbOfSamples = (int)(Math.ceil(routeLength/maxStepLength)+1);
        double stepLength = routeLength/(nbOfSamples-1);

        // Creating First Tab
        float [] samples = new float [nbOfSamples];
        for (int i = 0; i < nbOfSamples; i++) {
            samples[i] = (float)route.elevationAt(i*stepLength);
        }

        // Finding not Nan values

        int firstSampleNotNaNFromRight = -1;
        int firstSampleNotNaNFromLeft= firstSampleNotNaNFromRight;
        boolean rightFound=false;
        boolean leftFound=false;

        for (int i = 0; i < nbOfSamples; i++) {

            if (!rightFound && !Float.isNaN(samples[i])) {
                firstSampleNotNaNFromRight=i;
                rightFound=true;

            }
            if (rightFound && Float.isNaN(samples[i])) {
                samples[i] = (float)Math2.interpolate(route.elevationAt(firstSampleNotNaNFromRight * stepLength), route.elevationAt(routeLength), i * stepLength);
            }
        }
        for (int i = 0; i < nbOfSamples; i++) {
            if (!leftFound && !Float.isNaN(samples[nbOfSamples-i])) {
                firstSampleNotNaNFromLeft = nbOfSamples-i;
                leftFound=true;
            }
            if (leftFound && Float.isNaN(samples[nbOfSamples-i])) {
                samples[i] = (float)Math2.interpolate(route.elevationAt(0),route.elevationAt(firstSampleNotNaNFromLeft * stepLength), i * stepLength);
            }
        }
        return new ElevationProfile(routeLength, samples);
    }
}
