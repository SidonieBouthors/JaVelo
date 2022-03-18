package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

import java.util.Arrays;

/**
 * @author Sidonie Bouthors (343678)
 * @author François Théron (346077)
 */
public final class ElevationProfileComputer {

    private ElevationProfileComputer() {
    }

    /**
     * Returns the profile of the route, guarantying that the spacing between samples is at most maxStepLength
     * @param route         : the route
     * @param maxStepLength : maximum spacing between samples
     * @return the profile of the route
     * @throws IllegalArgumentException if the maximum spacing is <= 0
     */
    public static ElevationProfile elevationProfile(Route route, double maxStepLength) {
        Preconditions.checkArgument(maxStepLength > 0);

        double routeLength = route.length();
        int nbOfSamples = (int) (Math.ceil(routeLength / maxStepLength) + 1);
        double stepLength = routeLength / (nbOfSamples - 1);

        // Creating First Tab
        float[] samples = new float[nbOfSamples];
        for (int i = 0; i < nbOfSamples; i++) {
            samples[i] = (float) route.elevationAt(i * stepLength);
        }

        // Finding not Nan values

        int firstSampleNotNaNFromRight = -1;
        int firstSampleNotNaNFromLeft = firstSampleNotNaNFromRight;


        for (int i = 0; i < nbOfSamples; i++) {


            if (firstSampleNotNaNFromRight==-1 && !Float.isNaN(samples[i])) {
                firstSampleNotNaNFromRight=i;

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
            return new ElevationProfile(routeLength, samples);


            //Proposition pour les deux première étapes

            //Remplir les NaN tout à gauche
            int i = 0;
            while (!Float.isNaN(i)) {
                i++;
            } //trouver l'index de la première valeur valide
            if (i==nbOfSamples){Arrays.fill(samples, 0);} // si aucun trouvé, remplir de 0
            Arrays.fill(samples, 0, i, samples[i]); //remplir toutes les valeurs avant celle-ci

            //Remplir les NaN tout à droite
            int j = nbOfSamples;
            while (!Float.isNaN(j)) {
                j--;
            } //trouver l'index de la dernière valeur valide
            Arrays.fill(samples, j, nbOfSamples, samples[j]); //remplir toutes les valeurs apres celle-ci

            int firstNaNIndex; int nextCorrectValueIndex; boolean findNext = false;
            for (int k = 0; k < nbOfSamples; k++) {
                if (Float.isNaN(samples[k])){ //si on trouve une valeur NaN on garde son index et on se met en mode findNext (trouver la prochaine valeur valide)
                    firstNaNIndex = k;
                    findNext = true;
                } else if (findNext){ //si on trouve la prochaine valeur valide
                    nextCorrectValueIndex = k; //on stocke son index
                    double step = 1 / (double)(nextCorrectValueIndex - firstNaNIndex); //on calcule 1 divisé par le nombre de NaN du "trou" qu'on est en train de boucher
                    double x = step;
                    for (int l = firstNaNIndex; l < nextCorrectValueIndex; l++) {
                        //pour chaque NaN du trou, on interpole avec la valeur valide juste avant et juste après
                        samples[l]=(float)Math2.interpolate(samples[firstNaNIndex-1], samples[nextCorrectValueIndex], x);
                        x += step;
                    }
                }
            }

        }
    }

