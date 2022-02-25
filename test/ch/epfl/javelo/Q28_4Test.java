package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Q28_4Test {

     @Test
    void asDoubletest() {

         double actual = Q28_4.asDouble(1);
         double expected = 1/16;

         assertEquals(expected, actual);

     }

}