package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GraphSectorsTest {
    @Test
    void sectorintest() {
        int nbOfBytes = 6*16384;
        ByteBuffer sectorBuffer = ByteBuffer.allocate(nbOfBytes);
        short nbDeNoeudParSecteur = 2;
        for (int i = 0; i < 16384 ; i++) {
            sectorBuffer.putInt(nbDeNoeudParSecteur*i);
            sectorBuffer.putShort(nbDeNoeudParSecteur);
        }
        /*
        for (int i = 0; i < 128; i++) {
            if (i%6==0 && i!=0) {
            System.out.println("next sector");}
            System.out.println(sectorBuffer.get(i));
        }
        */

        PointCh point = new PointCh((SwissBounds.MIN_E+2740),SwissBounds.MIN_N );
        double distance = 1;
        GraphSectors graphSector = new GraphSectors(sectorBuffer);
        List<GraphSectors.Sector> sectors = graphSector.sectorsInArea(point, distance);
        System.out.println(" size : "+sectors.size());

        for (GraphSectors.Sector sector : sectors) {
            System.out.println("startnodeid :  "+ sector.startNodeId());

        }
        System.out.println("expected bottomleft east :"+(SwissBounds.WIDTH-distance));
        System.out.println("expected bottomleft north :"+(SwissBounds.HEIGHT-distance));

        System.out.println("expected topright east :"+(SwissBounds.WIDTH));

        System.out.println("expected topright north :"+(SwissBounds.HEIGHT));


    }




}