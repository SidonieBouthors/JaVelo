package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class RouteComputer {

    Graph graph;
    CostFunction costFunction;

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
     * @return route with the smallest total costbetween the two nodes of given ID
     */
    public Route bestRouteBetween(int startNodeId, int endNodeId){
        Preconditions.checkArgument(startNodeId != endNodeId);
        PriorityQueue<WeightedNode> toExplore = new PriorityQueue<>();
        double[] distance = new double[graph.nodeCount()];
        int[] predecessor = new int[graph.nodeCount()];
        //set distance and predecessor default for all nodes in the graph
        for (int i = 0; i < graph.nodeCount(); i++) {
            distance[i] = Float.POSITIVE_INFINITY;
            predecessor[i] = 0;
        }
        //set start node distance and add to toExplore
        distance[startNodeId] = 0;
        toExplore.add(new WeightedNode(startNodeId, 0f));

        while (!toExplore.isEmpty()){
            //get the closest node to explore
            WeightedNode node = toExplore.remove();
            while (distance[node.nodeId] == Float.NEGATIVE_INFINITY) {
                node = toExplore.remove();
            }
            //check if last node is reached
            if (node.nodeId == endNodeId) {
                System.out.println("Exploration done!");
                break; //terminer ici
            }
            //for each edge from the node
            for (int i = 0; i < graph.nodeOutDegree(node.nodeId); i++) {
                int edgeID = graph.nodeOutEdgeId(node.nodeId, i);
                int toNodeID = graph.edgeTargetNodeId(edgeID);

                double d =  node.distance + graph.edgeLength(edgeID);
                if( d < distance[toNodeID]) {
                    distance[toNodeID] = d;
                    predecessor[toNodeID] = node.nodeId;
                }
                //add new node to toExplore
                toExplore.add(new WeightedNode(toNodeID, (float)distance[toNodeID]));
                System.out.println("added node to explore");
            }
            distance[node.nodeId] = Float.NEGATIVE_INFINITY;
        }

        //create the corresponding shortest route found
        int nodeID = endNodeId;
        if (distance[nodeID] == Float.POSITIVE_INFINITY){
            return null;
        }
        //create list of nodes of the route
        List<Integer> nodes = new ArrayList<>(List.of(new Integer[]{nodeID}));
        while (nodeID != startNodeId) {
            nodeID = predecessor[nodeID];
            nodes.add(0, nodeID);
        }
        //create list of edges of the route
        List<Edge> edges = new ArrayList<>();
        for (int i = 0; i < nodes.size() - 1; i++) {
            int fromNodeId = nodes.get(i);
            int toNodeId = nodes.get(i+1);
            for (int j = 0; j < graph.nodeOutDegree(fromNodeId); j++) {
                int edgeId = graph.nodeOutEdgeId(fromNodeId, j);
                if (graph.edgeTargetNodeId(edgeId) == toNodeId){
                    edges.add(Edge.of(graph, edgeId, fromNodeId, toNodeId));
                    System.out.println("edge : " + edgeId);
                }
            }
        }
        return new SingleRoute(edges);
    }

    //private ou public???
    private record WeightedNode(int nodeId, float distance) implements Comparable<WeightedNode> {
        @Override
        public int compareTo(WeightedNode that) {
            return Float.compare(this.distance, that.distance);
        }
    }
}

/*
        pour chaque nœud N du graphe:
        distance[N] = ∞
        prédécesseur[N] = 0
        distance[Nd] = 0
        en_exploration = {Nd}
        tant que en_exploration n'est pas vide:
        N = nœud de en_exploration tel que distance[N] minimale
        (N est retiré de en_exploration)
        si N = Na:
        terminer, car le plus court chemin a été trouvé
        pour chaque arête A sortant de N:
        N′ = nœud d'arrivée de A
        d = distance[N] + longueur de A
        si d < distance[N′]:
        distance[N′] = d
         prédécesseur[N′] = N
        ajouter N′ à en_exploration

        terminer, car aucun chemin n'a été trouvé
         */