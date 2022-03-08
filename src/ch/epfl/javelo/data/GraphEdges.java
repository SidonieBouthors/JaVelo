package ch.epfl.javelo.data;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public record GraphEdges (ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations) {

    /**
     * Returns true iff the edge of given ID goes in the opposite direction
     * of the OSM road it comes from
     * @param edgeId    : ID of the edge
     * @return whether the edges direction is inverted
     */
    boolean isInverted(int edgeId){
        return true;
    }

    /**
     * Returns the ID of the destination node of the edge of given ID
     * @param edgeId
     * @return
     */
    int targetNodeId(int edgeId){

        return edgeId;
    }
    double length(int edgeId){

        return 0;
    }
    double elevationGain(int edgeId){

        return 0;
    }
    boolean hasProfile(int edgeId){

        return false;
    }
    float[] profileSamples(int edgeId){

        return new float[0];
    }
    int attributesIndex(int edgeId){

        return edgeId;
    }
}
