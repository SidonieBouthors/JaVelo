package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.*;

/**
 * RouteComputer
 *
 * @author Sidonie Bouthors (343678)
 * @author François Théron (346077)
 */
public final class RouteComputer {

    private static final float ALREADY_EXPLORED = Float.NEGATIVE_INFINITY;

    private final Graph graph;
    private final CostFunction costFunction;

    /**
     * Builds a RouteComputer for the given graph and cost function
     * @param graph             : graph from which the routes are calculated
     * @param costFunction      : cost function for the edges in the graph
     */
    public RouteComputer(Graph graph, CostFunction costFunction){
        this.graph = graph;
        this.costFunction = costFunction;
    }

    /**
     * Returns the route with the smallest total cost between the two nodes of given ID
     * Returns null if no such route exists
     * Returns either one if two equal best routes exist
     * @throws IllegalArgumentException if both given nodes are identical
     * @param startNodeId   : ID of the node at which the route starts
     * @param endNodeId     : ID of the node at which the route ends
     * @return route with the smallest total cost between the two nodes of given ID
     */
    public Route bestRouteBetween(int startNodeId, int endNodeId) {
        Preconditions.checkArgument(startNodeId != endNodeId);

        PointCh endNodePoint = graph.nodePoint(endNodeId);
        PriorityQueue<WeightedNode> toExplore = new PriorityQueue<>();
        double[] distance = new double[graph.nodeCount()];
        int[] predecessor = new int[graph.nodeCount()];

        //set distance and predecessor default for all nodes in the graph
        Arrays.fill(distance, Float.POSITIVE_INFINITY);

        //set start node distance and add to toExplore
        distance[startNodeId] = 0;
        toExplore.add(new WeightedNode(startNodeId, 0f));

        WeightedNode node;

        while (!toExplore.isEmpty()) {
            //get the closest node to explore
            do {
                node = toExplore.remove();
            } while (distance[node.nodeId] ==ALREADY_EXPLORED);

            //check if last node is reached
            if (node.nodeId == endNodeId) {
                break; //end exploration
            }

            //for each edge from the node
            for (int i = 0; i < graph.nodeOutDegree(node.nodeId); i++) {

                int edgeID = graph.nodeOutEdgeId(node.nodeId, i);
                int toNodeId = graph.edgeTargetNodeId(edgeID);
                double d = distance[node.nodeId] + graph.edgeLength(edgeID) * costFunction.costFactor(node.nodeId, edgeID);
                if (d < distance[toNodeId]) {
                    distance[toNodeId] = d;
                    predecessor[toNodeId] = node.nodeId;
                }

                //add new node to toExplore (+ as the crow flies distance for A*)
                PointCh toNodePoint = graph.nodePoint(toNodeId);
                double distanceToEnd = toNodePoint.distanceTo(endNodePoint);
                double minimalTotalDistance = distance[toNodeId] + distanceToEnd;
                toExplore.add(new WeightedNode(toNodeId, (float) minimalTotalDistance));
            }
            distance[node.nodeId] = ALREADY_EXPLORED;
        }
        System.out.println(distance[endNodeId]);
        System.out.println("end node "+endNodeId);
        //if no route was found
        if (distance[endNodeId] == Float.POSITIVE_INFINITY) {
            return null;
        }
        System.out.println(predecessor);

        return createRoute(startNodeId, endNodeId, predecessor);
    }

    private SingleRoute createRoute(int startNodeId , int endNodeId, int[] predecessor){
        //create the corresponding shortest route found
        List<Edge> edges = new ArrayList<>();
        int toNodeId = endNodeId;
        int fromNodeId = endNodeId;

        while (fromNodeId != startNodeId) {

            toNodeId = fromNodeId;
            fromNodeId = predecessor[toNodeId];

            for (int j = 0; j < graph.nodeOutDegree(fromNodeId); j++) {
                int edgeId = graph.nodeOutEdgeId(fromNodeId, j);

                if (graph.edgeTargetNodeId(edgeId) == toNodeId) {
                    Edge edge = Edge.of(graph, edgeId, fromNodeId, toNodeId);
                    edges.add(edge);
                    break;
                }
            }
        }

        Collections.reverse(edges);
        return new SingleRoute(edges);
    }

    private record WeightedNode(int nodeId, float distance) implements Comparable<WeightedNode> {
        @Override
        public int compareTo(WeightedNode that) {
            return Float.compare(this.distance, that.distance);
        }
    }
}
