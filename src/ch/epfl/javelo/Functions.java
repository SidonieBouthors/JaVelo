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

        return null;
    }

}
