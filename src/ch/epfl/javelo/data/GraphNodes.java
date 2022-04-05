package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.Q28_4;

import java.nio.IntBuffer;

/**
 * Record GraphNodes
 * @param buffer    : the buffer memory containing the value of the attributes of all nodes in the graph
 * @author Sidonie Bouthors (343678)
 * @author François Théron (346077)
 */
public record GraphNodes(IntBuffer buffer) {

    private static final int OFFSET_E = 0;
    private static final int OFFSET_N = OFFSET_E + 1;
    private static final int OFFSET_OUT_EDGES = OFFSET_N + 1;
    private static final int NODE_INTS = OFFSET_OUT_EDGES + 1;
    /**
     * Returns the total number of nodes
     * @return total number of nodes
     */
    public int count(){return buffer().capacity()/NODE_INTS;}

    /**
     * Returns the East coordinate of the node of given ID
     * @param nodeId    : ID of the node
     * @return East coordinate of the node
     */
    public double nodeE(int nodeId){
        return Q28_4.asDouble(buffer.get(OFFSET_E + NODE_INTS * nodeId));
    }

    /**
     * Returns the North coordinate of the node of given ID
     * @param nodeId    : ID of the node
     * @return North coordinate of the node
     */
    public double nodeN(int nodeId){
        return Q28_4.asDouble(buffer.get(OFFSET_N + NODE_INTS * nodeId));
    }

    /**
     * Returns the number of edges originating at the node of given ID
     * @param nodeId    : ID of the node
     * @return number of edges originating at the node
     */
    public int outDegree(int nodeId){
        int index = OFFSET_OUT_EDGES + NODE_INTS * nodeId;
        return Bits.extractUnsigned(buffer.get(index),28,4);
    }

    /**
     * Returns ID of the edge of given index originating at the node of given ID
     * @param nodeId        : ID of the node that the edge originates at
     * @param edgeIndex     : index of the edge amongst edges originating at this node
     * @return ID of the edge
     */
    public int edgeId(int nodeId, int edgeIndex){
       Preconditions.checkArgument(0 <= edgeIndex && edgeIndex < outDegree(nodeId));
       int index = OFFSET_OUT_EDGES + NODE_INTS * nodeId;
       return Bits.extractUnsigned(buffer.get(index),0,28) + edgeIndex;
    }
}
