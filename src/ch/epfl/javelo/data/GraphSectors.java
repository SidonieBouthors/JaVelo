package ch.epfl.javelo.data;

import ch.epfl.javelo.Math2;
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

    private static final double SECTOR_WIDTH = SwissBounds.WIDTH / 128; //meters
    private static final double SECTOR_HEIGHT = SwissBounds.HEIGHT / 128; //meters
    private static final short GRID_DIMENSIONS =128; //number of sectors in a row/column

    private static final int OFFSET_FIRST_NODE_ID = 0;
    private static final int OFFSET_NUMBER_OF_NODES = OFFSET_FIRST_NODE_ID + Integer.BYTES;
    private static final int SECTOR_BYTES = OFFSET_NUMBER_OF_NODES + Short.BYTES;

    public record Sector(int startNodeId, int endNodeId) {}

    /**
     * Returns the list of all the Sectors intersecting with the square of center and size given
     * @param center    : center of the square
     * @param distance  : distance between center and a side (half of a side of the square)
     * @return List of Sectors intersecting with square
     */
    public List<Sector> sectorsInArea(PointCh center, double distance){

        List<Sector> output = new ArrayList<>();

        double bottomLeftEastDistance = Math2.clamp(0, center.e() - distance - SwissBounds.MIN_E, SwissBounds.WIDTH);
        System.out.println("bottom left east : " +bottomLeftEastDistance);
        double bottomLeftNorthDistance = Math2.clamp(0, center.n() - distance - SwissBounds.MIN_N, SwissBounds.HEIGHT);
        System.out.println("bottom left north : " +bottomLeftNorthDistance);
        double topRightEastDistance = Math2.clamp(0, center.e() + distance - SwissBounds.MIN_E, SwissBounds.WIDTH);
        System.out.println("top right east : " +topRightEastDistance);
        double topRightNorthDistance = Math2.clamp(0, center.n() + distance - SwissBounds.MIN_N, SwissBounds.HEIGHT);
        System.out.println("top right north : " +topRightNorthDistance);

        int indexBottomLeft = (int)(bottomLeftEastDistance/(double)SECTOR_WIDTH) + (int)(bottomLeftNorthDistance/(double)SECTOR_HEIGHT) * GRID_DIMENSIONS;
        System.out.println("bottom left east distance / sectorwidth :"+bottomLeftEastDistance/SECTOR_WIDTH);
        System.out.println("bottom left north distance / sectorheight :"+(bottomLeftNorthDistance/SECTOR_HEIGHT));
        System.out.println("index bottom left : "+indexBottomLeft);
        int coteHeight = (int)Math.ceil((topRightNorthDistance-bottomLeftNorthDistance)/(double)SECTOR_HEIGHT);
        System.out.println("coteHeight : "+coteHeight);
        int coteWidth = (int)Math.ceil((topRightEastDistance-bottomLeftEastDistance)/(double)SECTOR_WIDTH);
        System.out.println("coteWidth : " + coteWidth);

        for (int i = 0; i < SECTORS_GRID_SUBDIVISIONS * coteHeight; i += SECTORS_GRID_SUBDIVISIONS) {

            for (int j = indexBottomLeft + i ; j < indexBottomLeft + coteWidth + i ; j++) {

                int sectorIndexInBuffer = j * SECTOR_BYTES;
                System.out.println(j);
                int startNodeId = buffer.getInt(sectorIndexInBuffer + OFFSET_FIRST_NODE_ID);
                int numberOfNodes = Short.toUnsignedInt(buffer.getShort(sectorIndexInBuffer + OFFSET_NUMBER_OF_NODES));
                int endNodeId = startNodeId + numberOfNodes;
                output.add(new Sector(startNodeId, endNodeId));
            }
        }
        return output;
    }

}


