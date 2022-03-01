package ch.epfl.javelo.data;

import ch.epfl.javelo.Preconditions;

import java.nio.IntBuffer;

/**
 * @author Sidonie Bouthors (343678)
 * @author François Théron (346077)
 */

public record GraphNodes(IntBuffer buffer) {
    /*
     qui retourne le nombre total de nœuds,
     */
    int count(){
       return buffer().capacity()%3;
    }

    /**
     * , qui retourne la coordonnée E du nœud d'identité donnée
     * @param nodeI
     * @return
     */
    double nodeE(int nodeI){
        Preconditions.checkArgument(nodeI>0);
        return buffer().get((nodeI-1)*3);
    }

    /**
     * qui retourne la coordonnée N du nœud d'identité donnée,
     * @param nodeI
     * @return
     */
    double nodeN(int nodeI){
        return buffer().get((nodeI-1)*3+1);
    }

    /**
     * qui retourne le nombre d'arêtes sortant du nœud d'identité donné,
     * @param nodeId
     * @return
     */
    int outDegree(int nodeId){
        return 0;
    }

    /**
     * qui retourne l'identité de la edgeIndex-ième arête sortant du nœud d'identité nodeId.
     *
     * @param nodeId
     * @param edgeIndex
     * @return
     */
    int edgeId(int nodeId, int edgeIndex){
        return 0;
    }
}
