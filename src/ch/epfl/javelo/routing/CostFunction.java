package ch.epfl.javelo.routing;

public interface CostFunction {
    /**
     * Returns the factor by which the length of the egde of given ID
     * from the node of given ID must be multiplied
     * This factor must always be >= 1
     * @param nodeId    : ID of starting node
     * @param edgeId    : ID of the edge
     * @return multiplicative factor / cost of the edge
     */
    double costFactor(int nodeId, int edgeId);
}
