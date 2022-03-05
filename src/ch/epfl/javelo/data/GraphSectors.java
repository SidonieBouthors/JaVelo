package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Record representing the table containing the 16384 sectors of JaVelo
 * @author Sidonie Bouthors (343678)
 * @author François Théron (346077)
 */
public record GraphSectors(ByteBuffer buffer) {

    private static final int SWISS_WIDTH = 349000; //meters
    private static final int SWISS_HEIGHT= 221000; //meters
    private static final int SECTOR_WIDTH = 2730; //meters
    private static final int SECTOR_HEIGHT = 1730; //meters
    private static final short SECTORS_GRID_SUBDIVISIONS =128;

    static private record Sector(int startNodeId, int endNodeId) {

    }

    /**
     * Returns the list of all the Sectors intersecting with the square of center and size given
     * @param center    : center of the square
     * @param distance  : distance between center and a side (half of a side of the square)
     * @return List of Sectors intersecting with square
     */
    public List<Sector> sectorsInArea(PointCh center, double distance){

        List<Sector> output = new ArrayList<>();

        double eastCoord = center.e();
        double northCoord = center.n();
        double norm = 2*distance;
        List<Integer> indexofallsectors = new ArrayList<>();

        double eastBorder = eastCoord-distance;
        double southBorder = northCoord - distance;

        double distanceFromWest= (eastBorder-SwissBounds.MIN_E);
        double distanceFromSouth = (southBorder-SwissBounds.MIN_N);

        // Cast for the floor Example if indexsector == 0.5 it means that the first sector inside is the 0
        int indexBottomLeft=indexOfSector((int)distanceFromWest/SECTOR_WIDTH,(int)distanceFromSouth/SECTOR_HEIGHT);
        int indexTopRight=indexOfSector((int)(distanceFromWest+norm)/SECTOR_WIDTH,(int)(distanceFromSouth+norm)/SECTOR_HEIGHT);
        int indexBottomRight=indexOfSector((int)(distanceFromWest+norm)/SECTOR_WIDTH,(int)distanceFromSouth/SECTOR_HEIGHT);
        int indexTopLeft = indexOfSector((int)distanceFromWest/SECTOR_WIDTH,(int)(distanceFromSouth+norm)/SECTOR_HEIGHT);
        int i = indexBottomRight;

        for (int j = indexBottomRight; j <= indexBottomLeft; j++) {
            for (i = indexBottomRight; i <=indexTopRight ; i+=SECTORS_GRID_SUBDIVISIONS) {
                indexofallsectors.add(i);
            }
            i++;

        }
        for (int j = 0; j < indexofallsectors.size(); j++) {
            int buffertoInt = buffer.getInt(j);
            int startNodeID = buffertoInt>>Short.SIZE;
            int endNodeID = startNodeID + ((buffertoInt<<Integer.SIZE)>>Integer.SIZE)+1;
            output.add(new Sector(startNodeID,endNodeID));
        }



        return output;
    }

    private  int indexOfSector(int indexFromWest, int indexFromSouth) {

        return  indexFromSouth*(SECTORS_GRID_SUBDIVISIONS)+ indexFromWest;

    }
}


