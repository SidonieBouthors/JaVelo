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

    /**
     * @author Sidonie Bouthors (343678)
     * @author François Théron (346077)

     * Sector with the given parameters
     * @param startNodeId   : first node of the sector
     * @param endNodeId     : last node of sector
     */
    public record Sector(int startNodeId, int endNodeId) {}

    /**
     * Returns the list of all the Sectors intersecting with the square of center and size given
     * @param center    : center of the square
     * @param distance  : distance between center and a side (half of a side of the square)
     * @return List of Sectors intersecting with square
     */
    public List<Sector> sectorsInArea(PointCh center, double distance){

        List<Sector> output = new ArrayList<>();

        // Distance (north and east distance) from the Swiss border to the different corner of the square
        double bottomLeftEastDistance = (int)Math2.clamp(0,
                (center.e() - distance - SwissBounds.MIN_E) /SECTOR_WIDTH,
                GRID_DIMENSIONS - 1);

        double bottomLeftNorthDistance = (int)Math2.clamp(0,
                (center.n() - distance - SwissBounds.MIN_N) /SECTOR_HEIGHT,
                GRID_DIMENSIONS - 1);

        double topRightEastDistance = (int) Math2.clamp(0,
                (center.e() + distance - SwissBounds.MIN_E) /SECTOR_WIDTH,
                GRID_DIMENSIONS - 1);

        double topRightNorthDistance = (int)Math2.clamp(0,
                (center.n() + distance - SwissBounds.MIN_N) /SECTOR_HEIGHT,
                GRID_DIMENSIONS-1);

        //Calculating index of the bottom left corner, and the height and the width of the square
        int indexBottomLeft = (int) bottomLeftEastDistance +
                                (int) bottomLeftNorthDistance * GRID_DIMENSIONS;

        int height = (int) Math.ceil(topRightNorthDistance - bottomLeftNorthDistance);
        int width = (int) Math.ceil(topRightEastDistance - bottomLeftEastDistance);

        //Adding all the sectors in the square
        for (int i = 0; i <= GRID_DIMENSIONS * height; i += GRID_DIMENSIONS) {

            for (int j = indexBottomLeft + i ; j <= indexBottomLeft + width + i ; j++) {

                int sectorIndexInBuffer = j * SECTOR_BYTES;
                int startNodeId = buffer.getInt(sectorIndexInBuffer + OFFSET_FIRST_NODE_ID);
                int numberOfNodes = Short.toUnsignedInt(
                                    buffer.getShort(sectorIndexInBuffer + OFFSET_NUMBER_OF_NODES));
                int endNodeId = startNodeId + numberOfNodes;

                output.add(new Sector(startNodeId, endNodeId));
            }
        }
        return output;
    }

}


