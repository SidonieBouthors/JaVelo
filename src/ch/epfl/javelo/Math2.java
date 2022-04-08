package ch.epfl.javelo;

/**
 * Math2
 *
 * @author Sidonie Bouthors (343678)
 * @author François Théron (346077)
 */
public final class Math2 {
    /**
     * Non instantiable
     */
    private Math2(){}

    /**
     * Returns the ceiling of the division
     * @throws IllegalArgumentException     : if x < 0 or y <= 0
     * @param x     : dividend
     * @param y     : divisor
     * @return ceiling division x/y
     */
    public static int ceilDiv(int x, int y){
        Preconditions.checkArgument(x>=0 && y>0);
        return (x+y-1)/y;
    }

    /**
     * Returns y  coordinate of point at x coordinate on the
     * line going through (0, y0) and (1, y1)
     * @param y0    : y0 at x = 0
     * @param y1    : y1 at x = 1
     * @param x     : x coordinate
     * @return y at x
     */
    public static double interpolate(double y0, double y1, double x){
        return Math.fma(x, y1 - y0, y0);
    }

    /**
     * Returns v limited to the interval [min, max]
     * (v in interval, min if v < min, max if v > max)
     * @throws IllegalArgumentException     : for min > max
     * @param min   : minimum of interval
     * @param v     : value to clamp
     * @param max   : maximum of interval
     * @return clamped v
     */
    public static int clamp(int min, int v, int max){
        return (int) clamp((double)min,v,max);
    }
    /**
     * Returns v limited to the interval [min, max]
     * (v in interval, min if v < min, max if v > max)
     * @throws IllegalArgumentException     : for min > max
     * @param min   : minimum of interval
     * @param v     : value to clamp
     * @param max   : maximum of interval
     * @return clamped v
     */
    public static double clamp(double min, double v, double max){
        Preconditions.checkArgument(min<max);
        if (v > max) {
            return max;
        } else return Math.max(v, min);
    }

    /**
     * Returns inverse hyperbolic sine of x
     * @param x     : value
     * @return asinh(x)
     */
    public static double asinh(double x){
        return Math.log(x + Math.sqrt(1 + x*x));
    }

    /**
     * Returns dot product of vectors u and v
     * @param uX    : x coordinate of u
     * @param uY    : y coordinate of u
     * @param vX    : x coordinate of v
     * @param vY    : y coordinate of v
     * @return dot product of u and v
     */
    public static double dotProduct(double uX, double uY, double vX, double vY){
        return uX*vX + uY*vY;
    }

    /**
     * Returns squared norm of u
     * @param uX    : x coordinate of u
     * @param uY    : y coordinate of y
     * @return squared norm of u
     */
    public static double squaredNorm(double uX, double uY){
        return dotProduct(uX, uY, uX, uY);
    }

    /**
     * Returns norm of u
     * @param uX    : x coordinate of u
     * @param uY    : y coordinate of y
     * @return norm of u
     */
    public static double norm(double uX, double uY){
        return Math.sqrt(squaredNorm(uX, uY));
    }

    /**
     * Returns length of projection of AP on AB
     * @param aX    : x coordinate of A
     * @param aY    : y coordinate of A
     * @param bX    : x coordinate of B
     * @param bY    : y coordinate of B
     * @param pX    : x coordinate of P
     * @param pY    : y coordinate of P
     * @return length of the projection of AP on AB
     */
    public static double projectionLength (double aX, double aY, double bX, double bY, double pX, double pY){
        double uX = pX - aX;
        double uY = pY - aY;
        double vX = bX - aX;
        double vY = bY - aY;
        return dotProduct(uX, uY, vX, vY) / norm(vX, vY);
    }
}
