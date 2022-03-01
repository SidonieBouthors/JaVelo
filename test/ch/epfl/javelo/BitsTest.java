package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BitsTest {

    @Test
    void testExtractSigned() {
        String initbin = "01110111110101001011101010101010";
        int  a = Integer.parseInt(initbin,2);
        int actual = Bits.extractSigned(a, 1, 2);

        int expected =Integer.parseInt("11",2);
        assertEquals(expected,actual);
    }
    @Test
    void testExtractUnsigned() {
        String initbin = "01110111110101001011101010101010";
        int  a = Integer.parseInt(initbin,2);
        int actual = Bits.extractUnsigned(a, 30, 0);

        int expected =Integer.parseInt("10",2);
        assertEquals(expected,actual);
    }

}