package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Q28_4;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 * @author Sidonie Bouthors (343678)
 * @author François Théron (346077)
 */
public record GraphEdges (ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations) {

    private final static int OFFSET_DIRECTION = 0;
    private final static int OFFSET_DESTINATION_NODE_ID = OFFSET_DIRECTION;
    private final static int OFFSET_LENGTH = OFFSET_DESTINATION_NODE_ID + Integer.BYTES;
    private final static int OFFSET_ELEVATION = OFFSET_LENGTH + Short.BYTES;
    private final static int OFFSET_OSM_ID = OFFSET_ELEVATION + Short.BYTES;
    private final static int EDGE_BYTES = OFFSET_OSM_ID + Short.BYTES;

    private final static int Q0_4_LENGTH = 4;
    private final static int Q4_4_LENGTH = 8;
    private final static int Q0_4_PER_SHORT = Short.SIZE / Q0_4_LENGTH;
    private final static int Q4_4_PER_SHORT = Short.SIZE / Q4_4_LENGTH;

    /**
     * Returns true iff the edge of given ID goes in the opposite direction
     * of the OSM road it comes from
     * @param edgeId    : ID of the edge
     * @return whether the edges' direction is inverted
     */
    public boolean isInverted(int edgeId){
        int index = edgeId * EDGE_BYTES + OFFSET_DIRECTION;
        return edgesBuffer.getInt(index) < 0;
    }

    /**
     * Returns the ID of the destination node of the edge of given ID
     * @param edgeId    : ID of the edge
     * @return ID of the destination node of the edge
     */
    public int targetNodeId(int edgeId){
        int index = edgeId * EDGE_BYTES + OFFSET_DESTINATION_NODE_ID;
        return isInverted(edgeId) ?  ~edgesBuffer.getInt(index): edgesBuffer.getInt(index);
    }

    /**
     * Returns length (in meters) of the edge of given ID
     * @param edgeId    : ID of the edge
     * @return length of the edge
     */
    public double length(int edgeId){
        int index = EDGE_BYTES * edgeId + OFFSET_LENGTH;
        short length = edgesBuffer.getShort( index );
        return Q28_4.asDouble(Short.toUnsignedInt(length ));
    }

    /**
     * Returns the elevation gain (in meters) of the edge of given ID
     * @param edgeId    : ID of the edge
     * @return positive elevation gain of the edge
     */
    public double elevationGain(int edgeId){
        int index = edgeId * EDGE_BYTES + OFFSET_ELEVATION;
        short elevationGain = edgesBuffer.getShort( index );
        return Q28_4.asDouble(Short.toUnsignedInt( elevationGain ));
    }

    /**
     * Returns true iff the edge of given ID has a profile
     * @param edgeId    : ID of the edge
     * @return whether the edge has a profile
     */
    public boolean hasProfile(int edgeId){
        return Bits.extractUnsigned(profileIds.get(edgeId), 30, 2)!=0;
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

        int lengthIndex = EDGE_BYTES * edgeId + OFFSET_LENGTH;
        int lengthOfEdge = Short.toUnsignedInt( edgesBuffer.getShort(lengthIndex) );
        int numberOfSamples = 1 + Math2.ceilDiv(lengthOfEdge,Q28_4.ofInt(2));

        float[] samples = new float[numberOfSamples];

        switch (profileType) {

            case 1:
                for (int i = 0; i < numberOfSamples; i++) {
                    short sample = elevations.get(firstSampleId + i);
                    samples[i] = Q28_4.asFloat(Short.toUnsignedInt( sample ));
                }
                break;

            case 2:
                short firstSample = elevations.get(firstSampleId);
                samples[0] = Q28_4.asFloat(Short.toUnsignedInt( firstSample ));


                int sampleIndex = 1;
                for (int i = 1; i < numberOfSamples; i += Q4_4_PER_SHORT) {
                    short nextTwoSamples = elevations.get(firstSampleId + sampleIndex);
                    for (int j = 0; j < Q4_4_PER_SHORT; j++) {
                        int index = i + j;
                        if (index < samples.length) {
                            int bits = Bits.extractSigned(nextTwoSamples,
                                    8 - Q4_4_LENGTH * j, Q4_4_LENGTH);
                            float elevationDifference = Q28_4.asFloat(bits);
                            samples[index] = samples[index - 1] + elevationDifference;
                        }
                    }
                    sampleIndex++;
                }

                break;

            case 3:
                firstSample = elevations.get(firstSampleId);
                samples[0] = Q28_4.asFloat(Short.toUnsignedInt( firstSample ));

                sampleIndex = 1;
                for (int i = 1; i < numberOfSamples; i += Q0_4_PER_SHORT) {
                    short nextFourSamples = elevations.get(firstSampleId + sampleIndex);
                    for (int j = 0; j < Q0_4_PER_SHORT; j++) {
                        int index = i + j;
                        if (index < samples.length){
                            int bits = Bits.extractSigned(nextFourSamples,
                                                    12 - Q0_4_LENGTH * j, Q0_4_LENGTH);
                            float elevationDifference = Q28_4.asFloat(bits);
                            samples[index] = samples[index-1] + elevationDifference;
                        }
                    }
                    sampleIndex++;
                }

                break;

            default : samples = new float[0]; //case 0
        }

        if (isInverted(edgeId)) {
            return reverseSamples(samples);
        }

        return samples;
    }

    /**
     * Returns the attributes ID associated to the edge of given ID
     * @param edgeId    : ID of the edge
     * @return attributes ID of the edge
     */
    public int attributesIndex(int edgeId){
        int index = edgeId * EDGE_BYTES + OFFSET_OSM_ID;
        short attributesIndex = edgesBuffer.getShort( index );
        return Short.toUnsignedInt(attributesIndex);
    }

    private float[] reverseSamples(float[] samples){
        float[] invertedSamples = new float[samples.length];
        for (int i = 0; i < samples.length; i++) {
            invertedSamples[i] = samples[samples.length - 1 - i ];
        }
        return invertedSamples;
    }
}
