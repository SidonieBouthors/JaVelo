package ch.epfl.javelo.data;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

public class Graph {

    private final GraphNodes nodes;
    private final GraphSectors sectors;
    private final GraphEdges edges;
    private final List<AttributeSet> attributeSet;

    public Graph(GraphNodes nodes, GraphSectors sectors, GraphEdges edges, List<AttributeSet> attributeSets){
        this.nodes=nodes;
        this.sectors=sectors;
        this.edges=edges;
        this.attributeSet=attributeSets;
    }

    public static Graph loadFrom(Path basePath) throws IOException {

        Path nodes = basePath.resolve("nodes.bin");
        Path sector = basePath.resolve("sectors.bin");
        Path edges = basePath.resolve("edges.bin");
        Path attributes = basePath.resolve("attributes.bin");
        Path profile = basePath.resolve("profile_ids.bin");
        Path elevation =basePath.resolve("elevations.bin");

        ByteBuffer edgesBuffer;
        IntBuffer nodesBuffer;
        ByteBuffer sectorBuffer;

        List<AttributeSet> attributeSetList = new ArrayList<>();
        LongBuffer attributeSetLong;

        IntBuffer profilesIds;
        ShortBuffer elevations;

        try (FileChannel channel = FileChannel.open(edges)) {
            edgesBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        }
        try (FileChannel channel = FileChannel.open(nodes)) {
            nodesBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()).asIntBuffer();
        }
        try (FileChannel channel = FileChannel.open(sector)) {
            sectorBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        }
        try (FileChannel channel = FileChannel.open(attributes)) {
            attributeSetLong = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()).asLongBuffer();
            for (int i = 0; i < attributeSetLong.capacity(); i++) {
                attributeSetList.add(new AttributeSet(attributeSetLong.get(i)));
            }
        }
        try (FileChannel channel = FileChannel.open(elevation)) {
            elevations = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()).asShortBuffer();

        }
        try (FileChannel channel = FileChannel.open(profile)) {
            profilesIds = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()).asIntBuffer();

        }
        return new Graph(new GraphNodes(nodesBuffer),new GraphSectors(sectorBuffer),new GraphEdges(edgesBuffer,profilesIds,elevations),attributeSetList);
    }

    /**
     * Qui retourne le nombre total de nœuds dans le graphe,
     * @return
     */
    public int nodeCount(){
        return nodes.count();
    }

    /**
     * qui retourne la position du nœud d'identité donnée
     * @param nodeId
     * @return
     */
    public PointCh nodePoint(int nodeId){
        return new PointCh(nodes.nodeE(nodeId), nodes.nodeN(nodeId));
    }

    /**
     * , qui retourne le nombre d'arêtes sortant du nœud d'identité donnée,
     * @param nodeId
     * @return
     */
    public int nodeOutDegree(int nodeId){
        return nodes.outDegree(nodeId);
    }

    /**
     * qui retourne l'identité de la edgeIndex-ième arête sortant du nœud d'identité nodeId,
     *
     * @param nodeId
     * @param edgeIndex
     * @return
     */
    public int nodeOutEdgeId(int nodeId, int edgeIndex) {
        return nodes.edgeId(nodeId,edgeIndex);
    }

    /**
     * qui retourne l'identité du nœud se trouvant le plus proche du point donné, à la distance maximale donnée (en mètres), ou -1 si aucun nœud ne correspond à ces critères,
     *
     * @param point
     * @param searchDistance
     * @return
     */
    public int nodeClosestTo(PointCh point, double searchDistance){

        double shortestDistance = SwissBounds.MAX_N;
        int indexWithShortestDistanceFromPoint = -1;
        List<GraphSectors.Sector> searching = sectors.sectorsInArea(point, searchDistance);

        for (GraphSectors.Sector sector : searching) {

            for (int i = sector.startNodeId(); i < sector.endNodeId(); i++) {

                double squaredDistance = Math2.squaredNorm(nodes.nodeE(i) - point.e(), nodes.nodeN(i) - point.n());
                if (squaredDistance < shortestDistance) {
                    shortestDistance = squaredDistance;
                    indexWithShortestDistanceFromPoint = i;
                }

            }
        }
        return indexWithShortestDistanceFromPoint;
    }



    /**
     * , qui retourne l'identité du nœud destination de l'arête d'identité donnée,
     *
     * @param edgeId
     * @return
     */
    public int edgeTargetNodeId(int edgeId){
        return edges.targetNodeId(edgeId);
    }

    /**
     * , qui retourne vrai ssi l'arête d'identité donnée va dans le sens contraire de la voie OSM dont elle provient,
     * @param edgeId
     * @return
     */
    public boolean edgeIsInverted(int edgeId) {
        return edges.isInverted(edgeId);
    }

    /**
     * , qui retourne l'ensemble des attributs OSM attachés à l'arête d'identité donnée,
     * @param edgeId
     * @return
     */
    public AttributeSet edgeAttributes(int edgeId) {
        return edgeAttributes(edgeId);
    }

    /**
     * qui retourne la longueur, en mètres, de l'arête d'identité donnée,
     * @param edgeId
     * @return
     */
    public double edgeLength(int edgeId) {
        return edges.length(edgeId);
    }

    /**
     *  qui retourne le dénivelé positif total de l'arête d'identité donnée
     * @param edgeId
     * @return
     */
    public double edgeElevationGain(int edgeId){
        return edges.elevationGain(edgeId);
    }

    /**
     * qui retourne le profil en long de l'arête d'identité donnée, sous la forme d'une fonction  ; si l'arête ne possède pas de profil, alors cette fonction doit retourner Double.NaN pour n'importe quel argument.
     * @param edgeId
     * @return
     */
    public DoubleUnaryOperator edgeProfile(int edgeId){
        return null;
    }
}
