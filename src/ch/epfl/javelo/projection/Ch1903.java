package ch.epfl.javelo.projection;

/**
 * @author Sidonie Bouthors (343678)
 * @author François Théron (346077)
 */
public final class Ch1903{
    /**
     * Non instantiable
     */
    private Ch1903 () {}

    /**
     * Returns the East coordinate of the point in the swiss system
     *  @param lon  : longitude
     * @param lat   : latitude
     * @return east coordinate
     */
    public static double e (double lon, double lat){

        double lambda1 = Math.pow(10,-4)*(3600*Math.toDegrees(lon) - 26782.5);
        double phi1 = Math.pow(10,-4)*(3600*Math.toDegrees(lat) - 169028.66);
        return  2600072.37
                + 211455.93*lambda1
                - 10938.51*lambda1*phi1
                - 0.36*lambda1*phi1*phi1
                - 44.54*Math.pow(lambda1,3);
    }
    /**
     *Returns the North coordinate of the point in the swiss system
     * @param lon   : longitude
     * @param lat   : latitude
     * @return north coordinate
     */
    public static double n (double lon, double lat){
        double lambda1 = Math.pow(10,-4)*(3600*Math.toDegrees(lon)-26782.5);
        double phi1 = Math.pow(10,-4)*(3600*Math.toDegrees(lat)-169028.66);
        return 1200147.07
                + 308807.95*phi1
                + 3745.25*lambda1*lambda1
                + 76.63*phi1*phi1
                - 194.56*lambda1*lambda1*phi1
                + 119.79 * Math.pow(phi1,3);
    }
    /**
    * Returns the longitude of the point in the WGS84 system
     * @param e     : east coordinate
     * @param n     : north coordinate
     * @return the longitude
     */
    public static double lon ( double e, double n){
        double x = Math.pow(10, -6) * (e - 2600000);
        double y = Math.pow(10, -6) * (n - 1200000);
        double lambda0 = 2.6779094
                        + 4.728982 * x
                        + 0.791484 * x * y
                        + 0.1306 * x * y * y
                        - 0.0436 * x * x * x;
        return Math.toRadians(lambda0 * 100/36);
    }
    /**
     *Returns the latitude of the point in the WGS84 system
     * @param e     : east coordinate
     * @param n     : north coordinate
     * @return the latitude
     */
    public static double lat (double e, double n){
        double x = Math.pow(10, -6) * (e - 2600000);
        double y = Math.pow(10, -6) * (n - 1200000);
        double phi0 = 16.9023892
                    + 3.238272 * y
                    - 0.270978 * x * x
                    - 0.002528 * y * y
                    - 0.0447 * x * x * y
                    - 0.0140 * y * y * y;
        return Math.toRadians(phi0 * 100/36);
    }
}
