package ch.epfl.javelo;

import java.util.function.DoubleUnaryOperator;

/**
 * Functions
 *
 * @author Sidonie Bouthors (343678)
 * @author François Théron (346077)
 */
public final class Functions {
    /**
     * Non instantiable
     */
    private Functions(){}

    /**
     * Returns constant function value y
     * @param y : constant value of the function
     * @return constant function f(x)=y
     */
    public static DoubleUnaryOperator constant(double y){
        return new Constant(y);
    }

    /**
     * Returns function obtained by interpolation of samples
     * @throws IllegalArgumentException if xMax <= 0 or if samples contains less than 2 elements
     * @param samples   : regularly spaced samples in [0, xMax]
     * @param xMax      : last sample
     * @return function obtained by interpolation
     */
    public static DoubleUnaryOperator sampled(float[] samples, double xMax){
        Preconditions.checkArgument(samples.length > 1 && xMax > 0);
        return new Sampled(samples, xMax);
    }

    /**
     * Record representing a constant function
     */
    private record Constant(double y) implements DoubleUnaryOperator{
        @Override
        public double applyAsDouble(double x) {
            return y;
        }
    }

    /**
     * Record representing a function constructed from a sample (by linear interpolation)
     */
    private record Sampled(float[] samples, double xMax) implements DoubleUnaryOperator{
        @Override
        public double applyAsDouble(double x) {

            x = Math2.clamp(0, x, xMax);
            int maxIndex = samples.length - 1;

            if (x == xMax){ return samples[maxIndex]; }

            double interval = xMax/(maxIndex);

            int indexAbove = (int)( x/interval) +1;

            double xUnder = (indexAbove - 1) * interval;
            return Math2.interpolate(samples[indexAbove-1],
                                     samples[indexAbove],
                                    (x-xUnder)/interval);
        }
    }
}
