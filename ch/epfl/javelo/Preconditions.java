package ch.epfl.javelo;

/**
 * Preconditions
 *
 * @author Sidonie Bouthors (343678)
 * @author François Théron (346077)
 */
public class Preconditions {

    private Preconditions() {}

    public static void checkArgument (boolean shouldBeTrue) throws IllegalArgumentException{
        if (!shouldBeTrue){
            throw new IllegalArgumentException();
        }
    }
}
