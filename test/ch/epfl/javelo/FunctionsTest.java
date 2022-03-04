package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.*;
public class FunctionsTest {

    @Test

    public void testSample() {

        DoubleUnaryOperator  a = Functions.sampled(new float [] {0, 4, 16},4);


        assertEquals(9,a.applyAsDouble(3),10e-6);

    }
}
