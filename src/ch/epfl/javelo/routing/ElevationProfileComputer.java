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
     */
    public static ElevationProfile elevationProfile(Route route, double maxStepLength) {
        Preconditions.checkArgument(maxStepLength>0);

        double routeLength = route.length();
        int nbOfSamples = (int)(Math.ceil(routeLength/maxStepLength)+1);
        double stepLength = routeLength/(nbOfSamples-1);

        // Creating First Tab
        float [] samples = new float [nbOfSamples];





            //Proposition pour les deux première étapes

            //Remplir les NaN tout à gauche
            int i = 0;
            while (!Float.isNaN(i)) {
                i++;
            }
            if (i == nbOfSamples - 1) {
                return new ElevationProfile(routeLength, new float[nbOfSamples]);
            }
            //trouver l'index de la première valeur valide
            if (i==nbOfSamples){Arrays.fill(samples, 0);} // si aucun trouvé, remplir de 0
            Arrays.fill(samples, 0, i, samples[i]); //remplir toutes les valeurs avant celle-ci

            //Remplir les NaN tout à droite
            int j = nbOfSamples;
            while (!Float.isNaN(j)) {
                j--;
            } //trouver l'index de la dernière valeur valide
            Arrays.fill(samples, j, nbOfSamples, samples[j]); //remplir toutes les valeurs apres celle-ci

            int firstNaNIndex=-1; int nextCorrectValueIndex; boolean findNext = false;
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
        return new ElevationProfile(routeLength, samples);

        }
}

