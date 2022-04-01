package ch.epfl.javelo.routing;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RouteComputerTest {

    @Test
    void mainTest() throws IOException {
        Graph g = Graph.loadFrom(Path.of("lausanne"));
        CostFunction cf = new CityBikeCF(g);
        RouteComputer rc = new RouteComputer(g, cf);

        int epfl = 159049;
        int agoraEpfl = g.nodeClosestTo(new PointCh(2533080.1, 1152309.6), 500);
        int sauvabelin = 117669;
        int egliseAnglaise = g.nodeClosestTo(new PointCh(2538062.5, 1151701.3), 500);
        int placeEurope = g.nodeClosestTo(new PointCh(2538001.4, 1152477.1), 500);
        long t0 = System.nanoTime();
        Route r = rc.bestRouteBetween(epfl, sauvabelin);
        long t1 = (System.nanoTime() - t0) / 1_000_000;
        System.out.printf("Itinéraire (lausanne) epfl-saubavelin calculé en %d ms (should be approx 250ms)\nKML généré lausanne.kml \n", t1);
        KmlPrinter.write("lausanne.kml", r);

        System.out.println("length lausanne :" + r.length());
        System.out.println(r.edges().size());
        assertEquals(9588.5625, r.length());
        assertEquals(575, r.edges().size());

        g = Graph.loadFrom(Path.of("lausanne"));
        cf = new CityBikeCF(g);
        rc = new RouteComputer(g, cf);
        t0 = System.nanoTime();
        r = rc.bestRouteBetween(agoraEpfl, placeEurope);
        t1 = (System.nanoTime() - t0) / 1_000_000;
        System.out.printf("Itinéraire (lausanne) epfl-flon calculé en %d ms (should be approx 25ms)\n", t1);

        System.out.println("length lausanne :" + r.length());
        System.out.println(r.edges().size());
        assertEquals(6404.1875, r.length());
        assertEquals(357, r.edges().size());

        g = Graph.loadFrom(Path.of("ch_west"));
        cf = new CityBikeCF(g);
        rc = new RouteComputer(g, cf);
        t0 = System.nanoTime();
        r = rc.bestRouteBetween(2046055, 2694240);
        t1 = (System.nanoTime() - t0) / 1_000_000;
        System.out.printf("Itinéraire (ch_west) calculé en %d ms (should be approx 1350ms)\nKML généré ch_west.kml\n", t1);
        KmlPrinter.write("ch_west.kml", r);

        System.out.println("length ch ouest :" +r.length());
        System.out.println(r.edges().size());
        assertEquals(168378.875, r.length());
        assertEquals(6046, r.edges().size());

        g = Graph.loadFrom(Path.of("ch_west"));
        cf = new CityBikeCF(g);
        rc = new RouteComputer(g, cf);
        t0 = System.nanoTime();
        for (int i = 0; i < 100; i++) {
            r = rc.bestRouteBetween(2000000, 2500000);
        }
        t1 = (System.nanoTime() - t0) / 1_000_000;
        System.out.printf("Itinéraire 100 fois calculé en %d ms, moyenne de %d ms par calcul", t1, t1/100);
    }

}

