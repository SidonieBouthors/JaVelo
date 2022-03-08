package ch.epfl.javelo.data;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public record GraphEdges (ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations) {
    private final static int OFFSET_EDGE_DIRECTION = 0;
    private final static int OFFSET_DESTINATION_NODE_ID = OFFSET_EDGE_DIRECTION;
    private final static int OFFSET_EDGE_LENGTH = OFFSET_DESTINATION_NODE_ID+Integer.BYTES;
    private final static int OFFSET_EDGE_ELEVATION = OFFSET_EDGE_LENGTH+Short.BYTES;
    private final static int OFFSET_EDGE_OSM_ID = OFFSET_EDGE_ELEVATION+Short.BYTES;
    private final static int EDGE_BYTES = OFFSET_EDGE_OSM_ID + Short.BYTES;
    /**
     * Returns true iff the edge of given ID goes in the opposite direction
     * of the OSM road it comes from
     * @param edgeId    : ID of the edge
     * @return whether the edges direction is inverted
     */
    public boolean isInverted(int edgeId){
        return edgesBuffer.getInt(edgeId*EDGE_BYTES + OFFSET_EDGE_DIRECTION) < 0;
    }

    /**
     * Returns the ID of the destination node of the edge of given ID
     * @param edgeId    : ID of the edge
     * @return ID of the destination node of the edge
     */
    public int targetNodeId(int edgeId){

        return edgeId;
    }

    /**
     * Returns length (in meters) of the edge of given ID
     * @param edgeId    : ID of the edge
     * @return length of the edge
     */
    public double length(int edgeId){

        return 0;
    }

    /**
     * Returns the elevation gain (in meters) of the edge of given ID
     * @param edgeId    : ID of the edge
     * @return positive elevation gain of the edge
     */
    public double elevationGain(int edgeId){

        return 0;
    }

    /**
     * Returns true iff the edge of given ID has a profile
     * @param edgeId    : ID of the edge
     * @return whether the edge has a profile
     */
    public boolean hasProfile(int edgeId){

        return false;
    }

    /**
     * Returns the table of samples of the profile of the edge of given ID
     * Returns empty table if the edge has no profile
     * @param edgeId    : ID of the edge
     * @return table of samples of edge profile
     */
    public float[] profileSamples(int edgeId){

        return new float[0];
    }

    /**
     * Returns the attributes ID associated to the edge of given ID
     * @param edgeId    : ID of the edge
     * @return attributes ID of the edge
     */
    public int attributesIndex(int edgeId){

        return edgeId;
    }
}
