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

    private static final int SECTOR_WIDTH = 2730; //meters
    private static final int SECTOR_HEIGHT = 1730; //meters
    private static final short SECTORS_GRID_SUBDIVISIONS =128; //number of sectors in a row/column

    private static final int OFFSET_FIRST_NODE_ID = 0;
    private static final int OFFSET_NUMBER_OF_NODES = OFFSET_FIRST_NODE_ID + Integer.BYTES;
    private static final int SECTOR_BYTES = OFFSET_NUMBER_OF_NODES + Short.BYTES;

    private record Sector(int startNodeId, int endNodeId) {}

    /**
     * Returns the list of all the Sectors intersecting with the square of center and size given
     * @param center    : center of the square
     * @param distance  : distance between center and a side (half of a side of the square)
     * @return List of Sectors intersecting with square
     */
    public List<Sector> sectorsInArea(PointCh center, double distance){

        List<Sector> output = new ArrayList<>();

        double bottomLeftEastDistance = Math2.clamp(0, center.e() - distance - SwissBounds.MIN_E, SwissBounds.WIDTH);
        double bottomLeftNorthDistance = Math2.clamp(0, center.n() - distance - SwissBounds.MIN_N, SwissBounds.HEIGHT);
        double topRightEastDistance = Math2.clamp(0, center.e() + distance - SwissBounds.MIN_E, SwissBounds.WIDTH);
        double topRightNorthDistance = Math2.clamp(0, center.n() + distance - SwissBounds.MIN_N, SwissBounds.HEIGHT);

        int indexBottomLeft = (int)(bottomLeftEastDistance/SECTOR_WIDTH) + (int)(bottomLeftNorthDistance/SECTOR_HEIGHT) * SECTORS_GRID_SUBDIVISIONS;

        int coteHeight = (int)(topRightNorthDistance/SECTOR_HEIGHT);
        int coteWidth = (int)(topRightEastDistance/SECTOR_WIDTH);
        for (int i = 0; i < SECTORS_GRID_SUBDIVISIONS * coteHeight; i+= SECTORS_GRID_SUBDIVISIONS) {
            for (int j = indexBottomLeft; j < indexBottomLeft + coteWidth + i * SECTORS_GRID_SUBDIVISIONS; j++) {
                int sectorIndex = j * SECTOR_BYTES;
                int startNodeId = buffer.getInt(sectorIndex + OFFSET_FIRST_NODE_ID);
                int numberOfNodes = Short.toUnsignedInt(buffer.getShort(sectorIndex + OFFSET_NUMBER_OF_NODES));
                int endNodeId = startNodeId + numberOfNodes;
                output.add(new Sector(startNodeId,endNodeId));
            }
        }
        return output;
    }

}


