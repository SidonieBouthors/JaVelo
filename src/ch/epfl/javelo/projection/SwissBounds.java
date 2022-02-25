package ch.epfl.javelo.projection;

/**
 * @author Sidonie Bouthors (343678)
 * @author François Théron (346077)
 */
public final class SwissBounds {
    /**
     * Non instantiable
     */
    private SwissBounds(){}
    /**
     * Smallest Swiss E coordinate
     */
    public final static double MIN_E = 2485000;
    /**
     * Largest Swiss E coordinate
     */
    public final static double MAX_E = 2834000;
    /**
     * Smallest Swiss N coordinate
     */
    public final static double MIN_N = 1075000;
    /**
     * Largest Swiss N coordinate
     */
    public final static double MAX_N = 1296000;
    /**
     * Width of Switzerland
     */
    public final static double WIDTH= MAX_E-MIN_E;
    /**
     * Height of Switzerland
     */
    public final static double HEIGHT = MAX_N-MIN_N;

    /**
     * Returns true iff the point at given coordinates are in
     * bounds of Switzerland
     * @param e     : east coordinate
     * @param n     : north coordinate
     * @return if the point is in Swiss bounds
     */
    public static boolean containsEN(double e, double n) {
        return e <= MAX_E && e >= MIN_E && n <= MAX_N && n >= MIN_N;
    }

}
