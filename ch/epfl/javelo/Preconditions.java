package ch.epfl.javelo;

/**
 * Preconditions
 *
 * @author Sidonie Bouthors (343678)
 * @author François Théron (346077)
 */
public final class Preconditions {

    /**
     * Non instantiable
     */
    private Preconditions() {}

    /**
     * Throws exception if shouldBeTrue is false
     * @param shouldBeTrue              : argument
     * @throws IllegalArgumentException : if shouldBeTrue is false
     */
    public static void checkArgument (boolean shouldBeTrue){
        if (!shouldBeTrue){
            throw new IllegalArgumentException();
        }
    }
}
