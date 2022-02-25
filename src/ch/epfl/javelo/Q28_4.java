package ch.epfl.javelo;

public final class Q28_4 {
    /**
     * Non instantiable
     */
    private Q28_4 () {}

    /**
     * Returns Q28.4 value corresponding to a given int
     * @param i     : int to convert
     * @return Q28.4 value
     */
    public static int ofInt(int i){
        return i << 4;
    }

    /**
     * Returns double corresponding to given Q28.4 value
     * @param q28_4     : Q28.4 value to convert
     * @return double value
     */
    public static double asDouble(int q28_4){
        return Math.scalb(q28_4, -4);
    }

    /**
     * Retruns float corresponding to given Q28.4 value
     * @param q28_4     : Q28.4 value to convert
     * @return float value
     */
    public static float asFloat(int q28_4){
        return Math.scalb(q28_4, -4);
    }
}
