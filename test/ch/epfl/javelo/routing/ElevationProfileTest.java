package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ElevationProfileTest {

    @Test
    void elevationProfileMainTest(){
        float[] elevationSamples = {
                380f, 381f, 382f, 300f, 320f
        };
        double length = 4;
        ElevationProfile elevationProfile = new ElevationProfile(length, elevationSamples);

        assertEquals(length, elevationProfile.length());

        assertEquals(300f, elevationProfile.minElevation());
        assertEquals(382f, elevationProfile.maxElevation());

        assertEquals(22, elevationProfile.totalAscent());
        assertEquals(82, elevationProfile.totalDescent());

        assertEquals(300, elevationProfile.elevationAt(3));
    }

    @Test
    void throwsExeption(){
        float[] elevationSamples = {
                380f
        };
        double length = 1;
        assertThrows(IllegalArgumentException.class, () -> {
            new ElevationProfile(length, elevationSamples);
        });
    }
}