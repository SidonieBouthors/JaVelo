package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Q28_4;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public record GraphEdges (ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations) {
    private final static int OFFSET_DIRECTION = 0;
    private final static int OFFSET_DESTINATION_NODE_ID = OFFSET_DIRECTION;
    private final static int OFFSET_LENGTH = OFFSET_DESTINATION_NODE_ID + Integer.BYTES;
    private final static int OFFSET_ELEVATION = OFFSET_LENGTH + Short.BYTES;
    private final static int OFFSET_OSM_ID = OFFSET_ELEVATION + Short.BYTES;
    private final static int EDGE_BYTES = OFFSET_OSM_ID + Short.BYTES;

    /**
     * Returns true iff the edge of given ID goes in the opposite direction
     * of the OSM road it comes from
     * @param edgeId    : ID of the edge
     * @return whether the edges' direction is inverted
     */
    public boolean isInverted(int edgeId){
        return edgesBuffer.getInt(edgeId * EDGE_BYTES + OFFSET_DIRECTION) < 0;
    }

    /**
     * Returns the ID of the destination node of the edge of given ID
     * @param edgeId    : ID of the edge
     * @return ID of the destination node of the edge
     */
    public int targetNodeId(int edgeId){
        return Bits.extractUnsigned(edgesBuffer.getInt(edgeId * EDGE_BYTES + OFFSET_DESTINATION_NODE_ID), 0, 31);
    }

    /**
     * Returns length (in meters) of the edge of given ID
     * @param edgeId    : ID of the edge
     * @return length of the edge
     */
    public double length(int edgeId){
        return Q28_4.asDouble(edgesBuffer.getShort(EDGE_BYTES * edgeId + OFFSET_LENGTH));
    }

    /**
     * Returns the elevation gain (in meters) of the edge of given ID
     * @param edgeId    : ID of the edge
     * @return positive elevation gain of the edge
     */
    public double elevationGain(int edgeId){
        return edgesBuffer.getShort(edgeId * EDGE_BYTES + OFFSET_ELEVATION);
    }

    /**
     * Returns true iff the edge of given ID has a profile
     * @param edgeId    : ID of the edge
     * @return whether the edge has a profile
     */
    public boolean hasProfile(int edgeId){
        return Bits.extractUnsigned(profileIds.get(edgeId), 29, 2)!=0;
    }

    /**
     * Returns the table of samples of the profile of the edge of given ID
     * Returns empty table if the edge has no profile
     * @param edgeId    : ID of the edge
     * @return table of samples of edge profile
     */
    public float[] profileSamples(int edgeId){
        int profileInfo = profileIds.get(edgeId);
        int profileType = Bits.extractUnsigned(profileInfo, 30, 2);
        int firstSampleId = Bits.extractUnsigned(profileInfo, 0, 30);
        int lengthOfEdge = Short.toUnsignedInt(edgesBuffer.getShort(EDGE_BYTES * edgeId + OFFSET_LENGTH));
        int numberOfSamples = 1 + Math2.ceilDiv(lengthOfEdge,Q28_4.ofInt(2));
        float[] samples = new float[numberOfSamples];
        switch (profileType) {
            case 1:
                for (int i = 0; i < numberOfSamples; i++) {
                    samples[i] = elevations.get(firstSampleId + i);
                }
                return samples;
            case 2:
                samples[0] = elevations.get(firstSampleId);
                for (int i = 1; i < numberOfSamples; i++) {
                    samples[i] = samples[i - 1] + elevations.get(firstSampleId + i);
                }
                return samples;
            case 3:
                samples[0] = elevations.get(firstSampleId);
                for (int i = 1; i < numberOfSamples; i++) {
                    samples[i] = samples[i - 1] + elevations.get(firstSampleId + i);
                }
                return samples;
            default : return new float[0]; //case 0
        }
    }

    /**
     * Returns the attributes ID associated to the edge of given ID
     * @param edgeId    : ID of the edge
     * @return attributes ID of the edge
     */
    public int attributesIndex(int edgeId){

        return edgesBuffer.getShort(edgeId*EDGE_BYTES + OFFSET_OSM_ID);
    }
}
