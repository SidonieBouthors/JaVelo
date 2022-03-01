package ch.epfl.javelo;

import java.util.function.DoubleUnaryOperator;

public final class Functions {
    /**
     * Non instantiable
     */
    private Functions(){}

    /**
     * Returns constant function value y
     * @param y
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
        Preconditions.checkArgument(samples.length>1 && xMax>0);
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

            int sampleSize = samples.length;
            double interval = xMax/sampleSize;
            if (x > xMax){
                return samples[sampleSize - 1];
            }else if (x<0){
                return samples[0];
            } else {
                float xAbove = 0;
                while (x < xAbove) {
                    xAbove+=interval;
                }
                return Math2.interpolate(xAbove-interval, xAbove, x);
            }
        }
    }
}
