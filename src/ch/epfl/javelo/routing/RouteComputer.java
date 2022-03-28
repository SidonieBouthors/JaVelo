package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;

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
     * @return
     */
    public Route bestRouteBetween(int startNodeId, int endNodeId){
        Preconditions.checkArgument(startNodeId != endNodeId);
        PriorityQueue<WeightedNode> toExplore = new PriorityQueue<>();

        for (int i = 0; i < graph.nodeCount(); i++) {
            if (i == startNodeId) {
                toExplore.add(new WeightedNode(0, 0f));
            }
            else {
                toExplore.add(new WeightedNode(i, Float.POSITIVE_INFINITY));
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
        return null;
    }

    //private ou public???
    private record WeightedNode(int nodeId, float distance) implements Comparable<WeightedNode> {
        @Override
        public int compareTo(WeightedNode that) {
            return Float.compare(this.distance, that.distance);
        }
    }
}
