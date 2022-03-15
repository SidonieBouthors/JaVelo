package ch.epfl.javelo.data;
import ch.epfl.javelo.projection.Ch1903;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.WebMercator;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.LongBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class GraphTest {
    @Test
    void loadfromtest() throws IOException {
        Path path = Path.of("lausanne");

        double e = 2521000;
        double n =1145100;

        System.out.println(Graph.loadFrom(path).nodeCount());
        PointCh b = new PointCh(e,n);

        int a = Graph.loadFrom(path).nodeClosestTo(b,7000);
        System.out.println(Graph.loadFrom(path).edgeIsInverted(3));
        Path osmid = path.resolve("nodes_osmid.bin");
        LongBuffer osmnodesBuffer;
        try (FileChannel channel = FileChannel.open(osmid)) {
            osmnodesBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()).asLongBuffer();
        }
        System.out.println(b.lon()+"  "+b.lat()+"  "+ WebMercator.x(b.lon()) + " "+ WebMercator.y(b.lat())+" "+b.e()+" "+b.n());


        System.out.println(osmnodesBuffer.get(2022));
        System.out.println(Graph.loadFrom(path).nodePoint(2022).e()+" "+ Graph.loadFrom(path).nodePoint(2022).n());
        System.out.println(Graph.loadFrom(path).nodeClosestTo(new PointCh(Graph.loadFrom(path).nodePoint(2022).e()+100,Graph.loadFrom(path).nodePoint(2022).n()+100),100));




    }
    @Test
    void nodeclosestoTEst() {


    }

}
