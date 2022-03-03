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
    private record Sector(int startNodeId, int endNodeId) {

    }

    /**
     * Returns the list of all the Sectors intersecting with the square of center and size given
     * @param center    : Center of square
     * @param distance  : distance between center and a side (half of a side of the square)
     * @return List of Sectors intersecting with square
     */
    public List<Sector> sectorsInArea(PointCh center, double distance){

        List<Sector> output = new ArrayList<>();

        double eastCoord = center.e();
        double northCoord = center.n();
        double eastBorder = eastCoord-distance;
        double westBorder = eastCoord + distance;
        double northBorder = northCoord+distance;
        double southBorder = northCoord - distance;
        final int xDistanceOfSectors = 2730; //meters
        final int yDistanceOfSectors = 1730; //meters
        final short numberOfSqares =128;
        double actualX = SwissBounds.MIN_E;
        double actualY = SwissBounds.MIN_N;
        




    }
}
